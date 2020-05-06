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
import org.announcementserver.ws.WriteBackReq;
import org.announcementserver.ws.WriteBackRet;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;

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
    REGISTER, POST, POSTGENERAL, READ, READGENERAL, WRITEBACK
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
    public List<WriteBackRet> writeBackRets;
    public ReadRet writeBack;
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
        LocalDateTime end, start;

        switch (this.op) {
            case REGISTER:
                RegisterRet response = null;
                RegisterReq request = new RegisterReq();
                request.setSender(username);
                request.setDestination(servName);

                toHash.add(request.getSender());
                toHash.add(request.getDestination());

                signature = makeSignature(toHash.toArray(new String[0]));

                if (signature == null) return;

                request.setSignature(signature);

                end = LocalDateTime.now().plusSeconds(40);

                while (LocalDateTime.now().isBefore(end)) {
                    try {
                        response = port.register(request);
                        break;
                    } catch (WebServiceException e) {
                        System.out.println("Hey, dead");
                        System.out.println(LocalDateTime.now());
                    }
                }

                if (response == null) return;

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
                post.setWriter(username);
                post.setMessage(message);
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
                
                if (signature == null) return;
                
                postReq.setSignature(signature);

                end = LocalDateTime.now().plusSeconds(40);

                while (LocalDateTime.now().isBefore(end)) {
                    try {
                        postRet = port.post(postReq);
                        break;
                    } catch (MessageSizeFault_Exception | PostTypeFault_Exception | ReferredAnnouncementFault_Exception
                            | ReferredUserFault_Exception | UserNotRegisteredFault_Exception e2) {
                        e2.printStackTrace();
                        return;
                    }
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

                end = LocalDateTime.now().plusSeconds(40);

                while (LocalDateTime.now().isBefore(end)) {
                    try {
                        postGenRet = port.postGeneral(postGenReq);
                        break;
                    } catch (MessageSizeFault_Exception | PostTypeFault_Exception | ReferredAnnouncementFault_Exception
                            | ReferredUserFault_Exception | UserNotRegisteredFault_Exception e2) {
                        e2.printStackTrace();
                        return;
                    }
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
                ReadRet readRet = null;
                ReadReq readReq = new ReadReq();
                readReq.setSender(username);
                readReq.setDestination(servName);
                readReq.setSeqNumber(seqNumber);
                readReq.setOwner(clientID);
                readReq.setRid(rid);
                readReq.setNumber(number);
                
                toHash = new ArrayList<>();
                toHash.add(username);
                toHash.add(servName);
                toHash.add(String.valueOf(seqNumber));
                toHash.add(String.valueOf(clientID));
                toHash.add(String.valueOf(rid));
                toHash.add(String.valueOf(number));

                signature = makeSignature(toHash.toArray(new String[0]));

                if (signature == null) return;

                readReq.setSignature(signature);
                
                end = LocalDateTime.now().plusSeconds(40);
                

                while (LocalDateTime.now().isBefore(end)) {
                    try {
                        readRet = port.read(readReq);
                        break;
                    } catch (EmptyBoardFault_Exception | InvalidNumberFault_Exception | NumberPostsFault_Exception | ReferredUserFault_Exception e2) {
                    	System.out.println("timeout");
                    }
                }
                
                if (readRet == null) return;

                if (!readRet.getSender().equals(servName)) 
                    throw new RuntimeException("Not my server response");
                
                if (!readRet.getDestination().equals(username))
                    throw new RuntimeException("Response not to me");

                if (readRet.getSeqNumber() != seqNumber) {
                    throw new RuntimeException("Sequence numbers don't match");
                }

                hash = decryptSignature(readRet.getSender(), readRet.getSignature());

                if (hash == null) return;
                
                toHash = new ArrayList<>();
                toHash.add(readRet.getSender());
                toHash.add(readRet.getDestination());
                toHash.add(String.valueOf(readRet.getSeqNumber()));
                toHash.add(String.valueOf(rid));
                toHash.addAll(listToSign(readRet.getAnnouncements()));
                toHash.add(hash);

                if (!checkHash(toHash.toArray(new String[0]))) {
                	System.out.println("Hashes do not match");
                    return;
                }

                if (!verifySigns(readRet.getAnnouncements())) {
                    System.out.println("Posts are not valid");
                    return;
                }
                
                synchronized (parent) {
                	readRets.add(readRet);
                	parent.notify();
                }
                
				return;				
            case READGENERAL:
            	ReadRet readGenRet = null;
            	ReadGeneralReq readGenReq = new ReadGeneralReq();
            	readGenReq.setSender(username);
            	readGenReq.setDestination(servName);
                readGenReq.setSeqNumber(seqNumber);
                readGenReq.setRid(rid);
                readGenReq.setNumber(number);
                
                System.out.println("Sequence number: " + String.valueOf(seqNumber));
            	
            	toHash = new ArrayList<>();
                toHash.add(username);
                toHash.add(servName);
                toHash.add(String.valueOf(seqNumber));
                toHash.add(String.valueOf(rid));
                toHash.add(String.valueOf(number));
                
                signature = makeSignature(toHash.toArray(new String[0]));
                
                if (signature == null) return;
                
                readGenReq.setSignature(signature);
              
                end = LocalDateTime.now().plusSeconds(40);

                while (LocalDateTime.now().isBefore(end)) {
                    try {
                        readGenRet = port.readGeneral(readGenReq);
                        break;
                    } catch (EmptyBoardFault_Exception | InvalidNumberFault_Exception | NumberPostsFault_Exception e2) {
                        System.out.println("timeout");
                    }
                }

                if (readGenRet == null) return;
                
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
                toHash.add(String.valueOf(rid));
                toHash.addAll(listToSign(readGenRet.getAnnouncements()));
                toHash.add(hash);

                if (!checkHash(toHash.toArray(new String[0]))) {
                    return;
                }
                
                if (!verifySigns(readGenRet.getAnnouncements())) {
                    System.out.println("Posts are not valid");
                }
                
                synchronized (parent) {
                    readRets.add(readGenRet);
                    parent.notify();
                }

                return;
             case WRITEBACK:
            	 WriteBackRet writeBackRet = null;
            	 WriteBackReq writeBackReq = new WriteBackReq();
            	 
            	 writeBackReq.setSender(username);
            	 writeBackReq.setDestination(servName);
            	 writeBackReq.setSeqNumber(seqNumber);
            	 writeBackReq.getAnnouncements().addAll(writeBack.getAnnouncements());
            	 
            	 toHash = new ArrayList<>();
            	 toHash.add(writeBackReq.getSender());
            	 toHash.add(writeBackReq.getDestination());
            	 toHash.add(String.valueOf(writeBackReq.getSeqNumber()));
            	 toHash.addAll(listToSign(writeBackReq.getAnnouncements()));
            	 
            	 
            	 signature = makeSignature(toHash.toArray(new String[0]));
            	 
            	 if (signature == null) return;
            	 
            	 writeBackReq.setSignature(signature);
            	 
            	 end = LocalDateTime.now().plusSeconds(40);
            	 
            	 while (LocalDateTime.now().isBefore(end)) {
            		 try {
            			 writeBackRet = port.writeBack(writeBackReq);
            			 break;
            		 } catch (Exception e) {
            			 System.out.println("timeout");
            		 }
            	 }
            	 
            	 hash = decryptSignature(writeBackRet.getSender(), writeBackRet.getSignature());
            	 
                 if (hash == null) {
                     System.out.println("Issue on hash");
                     return;
                 }
                 
                 toHash = new ArrayList<>();

                 toHash.add(servName);
                 toHash.add(username);
                 toHash.add(String.valueOf(seqNumber));
                 toHash.add(hash);
                 
                 if (!checkHash(toHash.toArray(new String[0]))) {
                     return;
                 }

                 synchronized(parent) {
                     writeBackRets.add(writeBackRet);
                     parent.notify();
                 }
                 
                 return;   	 
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
    
    private String postToHash(AnnouncementMessage post, boolean signature) {
        String res = String.format("%s,%s,%d,%s", post.getWriter(), post.getMessage(),
            post.getWts(), post.getAnnouncementList().toString());
        
        if (signature) res += "," + post.getSignature();

        return res + "\n";
    }
    
    private String announcementListToString(List<AnnouncementMessage> posts) {
        String res = "";

        for (AnnouncementMessage post: posts) {
            res += postToHash(post, true);
        }

        return res;
    }
}