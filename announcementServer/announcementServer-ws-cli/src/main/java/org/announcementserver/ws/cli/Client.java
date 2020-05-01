package org.announcementserver.ws.cli;

import java.util.ArrayList;
import java.util.List;
import org.announcementserver.common.Constants;
import org.announcementserver.common.CryptoTools;
import org.announcementserver.ws.AnnouncementServerPortType;

import org.announcementserver.ws.RegisterReq;
import org.announcementserver.ws.RegisterRet;
import org.announcementserver.ws.WriteReq;
import org.announcementserver.ws.WriteRet;
import org.announcementserver.ws.ReadReq;
import org.announcementserver.ws.ReadGeneralReq;
import org.announcementserver.ws.ReadRet;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.ws.WebServiceException;

import org.announcementserver.ws.AnnouncementMessage;
import org.announcementserver.ws.EmptyBoardFault_Exception;
import org.announcementserver.ws.InvalidNumberFault_Exception;
import org.announcementserver.ws.MessageSizeFault_Exception;
import org.announcementserver.ws.NumberPostsFault_Exception;
import org.announcementserver.ws.PostTypeFault_Exception;
import org.announcementserver.ws.ReferredAnnouncementFault_Exception;
import org.announcementserver.ws.ReferredUserFault_Exception;
import org.announcementserver.ws.UserNotRegisteredFault_Exception;

enum Operation {
    REGISTER, POST, POSTGENERAL, READ, READGENERAL
};

public class Client extends Thread {
    private FrontEnd parent;
    private Integer servId;
    private Operation op;
    
    public String readKey;
    public String message;
    public List<String> references;
    public String boardKey;
    public Integer number;
    public String clientID;
    public Integer seqNumber;
    public List<RegisterRet> regRets;
    public List<WriteRet> writeRets;
    public List<ReadRet> readRets;
    public Integer wts;
    public Integer rid;
    public List<String> ret;

    public Client(FrontEnd parent, Operation op, Integer id) {
        this.parent = parent;
        this.op = op;
        this.servId = id;
    }

    @Override
    public void run() {
        AnnouncementServerPortType port = parent.ports.get(servId - 1);
        String servName = Constants.SERVER_NAME + servId.toString();
        String username = parent.username;
        String signature = "";
        String hash = null;
        List<String> toHash = new ArrayList<>();

        switch (this.op) {
            case REGISTER:
                RegisterRet response;
                RegisterReq request = new RegisterReq();
                request.setSender(username);
                request.setDestination(servName);

                toHash.add(request.getSender());
                toHash.add(request.getDestination());

                signature = makeSignature(toHash.toArray(new String[0]));

                if (signature == null) return;

                request.setSignature(signature);

                try {
                    response = port.register(request);
                } catch (WebServiceException e) {
                    System.out.println("Hey, dead");
                    return;
                }

                System.out.println(String.format("Response: %s, %s, %d, %d, %d, %s", 
			        response.getSender(), response.getDestination(), response.getSeqNumber(), 
			        response.getWts(), response.getRid(), response.getSignature()));

                if (!response.getSender().equals(servName)) throw new RuntimeException("Received response that wasn't from right server");
                if (!response.getDestination().equals(username)) throw new RuntimeException("Received response that wasn't for me");

                toHash = new ArrayList<>();
                toHash.add(response.getSender());
                toHash.add(response.getDestination());
                toHash.add(String.valueOf(response.getSeqNumber()));
                toHash.add(String.valueOf(response.getWts()));
                toHash.add(String.valueOf(response.getRid()));

                hash = decryptSignature(response.getSender(), response.getSignature());
                
                if (hash == null) return;

                toHash.add(hash);

                if (!checkHash(toHash.toArray(new String[0]))) return;
                
                synchronized(parent) {
                    regRets.add(response);
                    parent.notify();
                }

                return;
            case POST:
                WriteRet postRet = null;
                WriteReq postReq = new WriteReq();
                postReq.setSender(username);
                postReq.setDestination(servName);
                postReq.setSeqNumber(seqNumber);

                AnnouncementMessage post = new AnnouncementMessage();
                post.setMessage(message);
                post.setWriter(username);
                post.getAnnouncementList().addAll(this.references);
                post.setWts(wts);
                post.setType("Personal");

                List<String> messHash = postToSign(post, false);

                String messSig = makeSignature(messHash.toArray(new String[0]));

                if (messSig == null) return;

                post.setSignature(messSig);

                postReq.setAnnouncement(post);

                toHash = new ArrayList<>();
                toHash.add(username);
                toHash.add(servName);
                toHash.add(String.valueOf(seqNumber));
                toHash.addAll(postToSign(post, true));

                signature = makeSignature(toHash.toArray(new String[0]));

                postReq.setSignature(signature);

                try {
                	postRet = port.post(postReq);
                } catch (MessageSizeFault_Exception | PostTypeFault_Exception | ReferredAnnouncementFault_Exception
                		| ReferredUserFault_Exception | UserNotRegisteredFault_Exception e2) {
                	e2.printStackTrace();
                	return;
                }

                hash = decryptSignature(servName, postRet.getSignature());
                
                if (hash == null) {
                    System.out.println("Issue on hash");
                    return;
                }
                
                toHash = new ArrayList<>();

                if (wts != postRet.getWts()) {
                    System.out.println("WTS wrongly set");
                    return; // if server acked wrong w
                } 

                toHash.add(servName);
                toHash.add(username);
                toHash.add(String.valueOf(seqNumber));
                toHash.add(hash);
                
                if (!checkHash(toHash.toArray(new String[0]))) {
                    return;
                }

                synchronized(parent) {
                    System.out.println("Returned");
                    writeRets.add(postRet);
                    parent.notify();
                }
                
                return;                
            case POSTGENERAL:
            	WriteRet postGenRet = null;
            	WriteReq postGenReq = new WriteReq();
            	postGenReq.setSender(username);
            	postGenReq.setDestination(servName);
            	postGenReq.setSeqNumber(seqNumber);
            	
            	AnnouncementMessage postGen = new AnnouncementMessage();
            	postGen.setWriter(username);
            	postGen.setMessage(message);
                postGen.getAnnouncementList().addAll(this.references);
                postGen.setWts(wts);
                postGen.setType("General");
            	
                List<String> messHashG = postToSign(postGen, false);
                
                String messSigG = makeSignature(messHashG.toArray(new String[0]));

                if (messSigG == null) return;
                
                postGen.setSignature(messSigG);

                postGenReq.setAnnouncement(postGen);
                
                toHash = new ArrayList<>();
                toHash.add(username);
                toHash.add(servName);
                toHash.add(String.valueOf(seqNumber));
                toHash.addAll(postToSign(postGen, true));
                
                signature = makeSignature(toHash.toArray(new String[0]));

                if (signature == null) return;
                
                postGenReq.setSignature(signature);

                try {
                	postGenRet = port.postGeneral(postGenReq);
                } catch (MessageSizeFault_Exception | PostTypeFault_Exception | ReferredAnnouncementFault_Exception
                		| ReferredUserFault_Exception | UserNotRegisteredFault_Exception e2) {
                	e2.printStackTrace();
                	return;
                }

                hash = decryptSignature(servName, postGenRet.getSignature());

                if (hash == null) return;
                
                toHash = new ArrayList<>();

                toHash.add(servName);
                toHash.add(username);
                toHash.add(String.valueOf(postGenRet.getSeqNumber()));
                toHash.add(hash);
                
                if (!checkHash(toHash.toArray(new String[0]))) {
                    return;
                }

                synchronized(parent) {
                    writeRets.add(postGenRet);
                    parent.notify();
                }
                
                return;
            case READ:
                toHash = new ArrayList<>();
                
                ReadRet readRes;
                ReadReq readReq = new ReadReq();
                readReq.setSender(username);
                readReq.setDestination(servName);
                readReq.setSeqNumber(seqNumber);
                readReq.setOwner(clientID);
                readReq.setNumber(number);
			
                toHash.add(username);
                toHash.add(servName);
                toHash.add(seqNumber.toString());
                toHash.add(clientID);
                toHash.add(number.toString());

                signature = makeSignature(toHash.toArray(new String[0]));

                if (signature == null) return;

                readReq.setSignature(signature);
                
                try {
                    readRes = port.read(readReq);
                } catch (EmptyBoardFault_Exception | InvalidNumberFault_Exception | NumberPostsFault_Exception | ReferredUserFault_Exception e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                    return;
                }

                if (!readRes.getSender().equals(servName)) 
                    throw new RuntimeException("Not my server response");
                
                if (!readRes.getDestination().equals(username))
                    throw new RuntimeException("Response not to me");

                if (readRes.getSeqNumber() != seqNumber) {
                    throw new RuntimeException("Sequence numbers don't match");
                }

                hash = decryptSignature(readRes.getSender(), readRes.getSignature());

                if (hash == null) return;
                
                toHash = new ArrayList<>();
                toHash.add(readRes.getSender());
                toHash.add(readRes.getDestination());
                toHash.add(String.valueOf(readRes.getSeqNumber()));
                toHash.addAll(listToSign(readRes.getAnnouncements())); //need similar func in the server
                toHash.add(hash);

                if (!checkHash(toHash.toArray(new String[0]))) {
                    return;
                }

                String res = "";

                for (AnnouncementMessage mess: readRes.getAnnouncements()) {
                    res += postToString(mess); // deprecated use
                }

                ret.add(res);
                
				break;
				
            case READGENERAL:
            	ReadRet readGenRet = null;
            	ReadGeneralReq readGenReq = new ReadGeneralReq();
            	readGenReq.setSender(username);
            	readGenReq.setDestination(servName);
            	readGenReq.setSeqNumber(seqNumber);
            	readGenReq.setNumber(number);
            	
            	toHash = new ArrayList<>();
                toHash.add(username);
                toHash.add(servName);
                toHash.add(String.valueOf(seqNumber));
                toHash.add(String.valueOf(number));
                
                signature = makeSignature(toHash.toArray(new String[0]));
                
                if (signature == null) return;
                
                readGenReq.setSignature(signature);
              
                try {
                    readGenRet = port.readGeneral(readGenReq);
                } catch (EmptyBoardFault_Exception | InvalidNumberFault_Exception | NumberPostsFault_Exception e2) {
                    e2.printStackTrace();
                    return;
                }
                
                if (!readGenRet.getSender().equals(servName)) 
                    throw new RuntimeException("Not my server response");
                
                if (!readGenRet.getDestination().equals(username))
                    throw new RuntimeException("Response not to me");

                if (readGenRet.getSeqNumber() != seqNumber) {
                    throw new RuntimeException("Sequence numbers don't match");
                }

                hash = decryptSignature(readGenRet.getSender(), readGenRet.getSignature());

                if (hash == null) return;
                
                toHash = new ArrayList<>();
                toHash.add(readGenRet.getSender());
                toHash.add(readGenRet.getDestination());
                toHash.add(String.valueOf(readGenRet.getSeqNumber()));
                toHash.addAll(listToSign(readGenRet.getAnnouncements()));
                toHash.add(hash);

                if (!checkHash(toHash.toArray(new String[0]))) {
                    return;
                }
                
                res = "";
                if (!verifySigns(readGenRet.getAnnouncements())) {
                    System.out.println("Posts are not valid");
                }
                
                synchronized (parent) {
                    readRets.add(readGenRet);
                    parent.notify();
                }
        }
        
        System.out.println("HELLO");
    }
    
    // --- Auxiliary ---------

    private List<String> postToSign(AnnouncementMessage post, boolean signature) {
        List<String> res = new ArrayList<>();

        res.add(post.getWriter());
        res.add(post.getMessage());
        res.add(post.getAnnouncementList().toString());
        res.add(String.valueOf(post.getWts()));
        res.add(post.getType());
        if (signature) res.add(post.getSignature());

        return res;
    }

    private List<String> listToSign(List<AnnouncementMessage> posts) {
        List<String> res = new ArrayList<>();

        for (AnnouncementMessage post: posts) {
            res.addAll(postToSign(post, true));
        }

        return res;
    }

    private String postToString(AnnouncementMessage post) {
        return String.format("Author: %s, Id: %d\n\"%s\"\nReferences: %s\n",
            post.getWriter(), post.getWts(), post.getMessage(),
            post.getAnnouncementList().toString());
    }

    private String makeSignature(String[] args) {
        String res = null;
        try {
            res = CryptoTools.makeSignature(args);
        } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                | UnrecoverableEntryException | IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String decryptSignature(String sender, String signature) {
        String res = null;
        try {
            res = CryptoTools.decryptSignature(sender, signature);
        } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                | UnrecoverableEntryException | IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private boolean checkHash(String[] args) {
        boolean res = false;
        try {
            res = CryptoTools.checkHash(args);
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException
                | CertificateException | IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private boolean verifySigns(List<AnnouncementMessage> posts) {
        String hash = null;
        List<String> toHash;
        for (AnnouncementMessage post: posts) {
            hash = decryptSignature(post.getWriter(), post.getSignature());
            if (hash == null) {
                System.out.println("Error decrypting");
                return false;
            }

            toHash = new ArrayList<>();
            toHash.addAll(postToSign(post, false));
            toHash.add(hash);

            if (!checkHash(toHash.toArray(new String[0]))) {
                System.out.println("Hash is wrong on post");
                return false;
            }
        }

        return true;
    }
}