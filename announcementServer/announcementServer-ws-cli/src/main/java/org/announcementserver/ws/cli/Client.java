package org.announcementserver.ws.cli;

import java.util.ArrayList;
import java.util.List;
import org.announcementserver.common.Constants;
import org.announcementserver.common.CryptoTools;
import org.announcementserver.ws.AnnouncementServerPortType;

import org.announcementserver.ws.RegisterReq;
import org.announcementserver.ws.RegisterRet;

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
import org.announcementserver.ws.WriteReq;
import org.announcementserver.ws.WriteRet;

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
    public Integer seqNum;
    public List<?> responses;

    public Client(FrontEnd parent, Operation op, Integer id, List<?> responses) {
        this.parent = parent;
        this.op = op;
        this.servId = id;
        this.responses = responses;
    }

    @Override
    public void run() {
        AnnouncementServerPortType port = parent.ports.get(servId - 1);
        String servName = Constants.SERVER_NAME + servId.toString();
        String username = parent.username;
        String publicKey = parent.publicKey;
        String signature = "";
        String hash = null;
        List<String> toHash = new ArrayList<>();
        List<String> ret = new ArrayList<>();

        switch (this.op) {
            case REGISTER:
                RegisterRet response;
                RegisterReq request = new RegisterReq();
                request.setSender(username);
                request.setDestination(servName);

                toHash.add(request.getSender());
                toHash.add(request.getDestination());

                try {
                    signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                        | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                        | UnrecoverableEntryException | IOException e) {
                    e.printStackTrace();
                    return;
                }

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

                hash = null;
                try {
                    hash = CryptoTools.decryptSignature(response.getSender(), response.getSignature());
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                        | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                        | UnrecoverableEntryException | IOException e) {
                    e.printStackTrace();
                    return;
                }

                toHash.add(hash);

                try {
                    if (!CryptoTools.checkHash(toHash.toArray(new String[0]))) {
                        throw new RuntimeException("Hashes are not equal");
                    }                
                } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException
                        | CertificateException | IOException e) {
                    e.printStackTrace();
                    return;
                }

                ret.add("All good");
                ret.add(String.valueOf(response.getSeqNumber()));
                
                break;
                
            case POST:
                WriteRet postRet = null;
                WriteReq postReq = new WriteReq();
                postReq.setSender(username);
                postReq.setDestination(servName);
                postReq.setSeqNum(seqNum);

                AnnouncementMessage post = new AnnouncementMessage();
                post.setMessage(message);
                post.setWriter(username);
                post.getAnnouncementList().addAll(this.references);
                post.setAnnouncementId(String.format("pc%sa%d", username.replaceAll("client", ""), seqNum));

                List<String> messHash = new ArrayList<>();
                messHash.add(username);
                messHash.add(message);
                messHash.add(references.toString());
                messHash.add(post.getAnnouncementId());

                String messSig = null;

                try {
                    messSig = CryptoTools.makeSignature(messHash.toArray(new String[0]));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                        | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                        | UnrecoverableEntryException | IOException e) {
                    e.printStackTrace();
                    return;
                }

                post.setSignature(messSig);

                postReq.setAnnouncement(post);

                toHash = new ArrayList<>();
                toHash.add(username);
                toHash.add(servName);
                toHash.add(String.valueOf(seqNum));
                toHash.add(username);
                toHash.add(message);
                toHash.add(references.toString());
                toHash.add(post.getAnnouncementId());
                toHash.add(messSig);

                try {
                	signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                		| NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                		| UnrecoverableEntryException | IOException e2) {
                	// TODO Auto-generated catch block
                	e2.printStackTrace();
                	return;
                }

                postReq.setSignature(signature);

                System.out.println(post.getSignature());

                try {
                	postRet = port.post(postReq);
                } catch (MessageSizeFault_Exception | PostTypeFault_Exception | ReferredAnnouncementFault_Exception
                		| ReferredUserFault_Exception | UserNotRegisteredFault_Exception e2) {
                	e2.printStackTrace();
                	return;
                }

                try {
                	hash = CryptoTools.decryptSignature(servName, postRet.getSignature());
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                		| NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                		| UnrecoverableEntryException | IOException e2) {
                	e2.printStackTrace();
                	return;
                }
                
                toHash = new ArrayList<>();

                toHash.add(servName);
                toHash.add(username);
                toHash.add(String.valueOf(postRet.getSeqNum()));
                toHash.add(hash);
                
                try {
                	if (!CryptoTools.checkHash(toHash.toArray(new String[0]))) {
                		throw new RuntimeException("Hashes are not equal");
                	}
                } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | CertificateException
                		| IOException e2) {
                	e2.printStackTrace();
                	return;
                }

                ret.add("post all good");
                
                break;
                
            case POSTGENERAL:
                toHash.add(username);
                toHash.add(servName);
                toHash.add(parent.sn.toString());
                toHash.add(publicKey);
                toHash.add(message);
                toHash.addAll(references);

                try {
                	signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                		| NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                		| UnrecoverableEntryException | IOException e1) {
                	e1.printStackTrace();
                	return;
                }
                
                /* FIXME: ADAPT TO NEW REALITY
                try {
                	ret = port.postGeneral(publicKey, message, references, signature);
                } catch (WebServiceException | MessageSizeFault_Exception | PostTypeFault_Exception | ReferredAnnouncementFault_Exception | ReferredUserFault_Exception | UserNotRegisteredFault_Exception e1) {
                	System.out.println("Hey, dead");
                	e1.printStackTrace();
                	return;
                }
                */

                try {
                	hash = CryptoTools.decryptSignature(servName, ret.get(1));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                		| NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                		| UnrecoverableEntryException | IOException e1) {
                	e1.printStackTrace();
                	return;
                }
                
                //String response;

                try {
                	if (CryptoTools.checkHash(servName, username, String.valueOf(parent.sn), ret.get(0), hash)) {
                		//response = ret.get(0);
                	} else {
                		throw new RuntimeException("Hashes are not equal");
                	}
                } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | CertificateException
                		| IOException e1) {
                	// TODO Auto-generated catch block
                	e1.printStackTrace();
                	return;
                }
            	
                break;
                
            case READ:
            	try {
            		readKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey(clientID));
            	} catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | CertificateException
            			| IOException e4) {
            		// TODO Auto-generated catch block
            		e4.printStackTrace();
            	}
			
                toHash.add(username);
                toHash.add(servName);
                toHash.add(parent.sn.toString());
                toHash.add(readKey);
                toHash.add(number.toString());

                try {
                    signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                        | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                        | UnrecoverableEntryException | IOException e3) {
                    // TODO Auto-generated catch block
                    e3.printStackTrace();
                    return;
                }
                
                /* FIXME: ADAPT TO NEW REALITY
                try {
                    ret = port.read(publicKey, readKey, Long.valueOf(number), signature);
                } catch (EmptyBoardFault_Exception | InvalidNumberFault_Exception | NumberPostsFault_Exception | ReferredUserFault_Exception e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                    return;
                }
                */

                try {
                    hash = CryptoTools.decryptSignature(servName, ret.get(1));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                        | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                        | UnrecoverableEntryException | IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return;
                }
                
                toHash = new ArrayList<>();
                toHash.add(servName);
                toHash.add(username);
                toHash.add(parent.sn.toString());
                toHash.add(ret.get(0));
                toHash.add(hash);

                try {
                    if (!CryptoTools.checkHash(toHash.toArray(new String[0]))) {
                        throw new RuntimeException("Hashes don't match");
                    }
                } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException
                        | CertificateException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }
                
				break;
				
            case READGENERAL:
                toHash.add(username);
                toHash.add(servName);
                toHash.add(parent.sn.toString());
                toHash.add(number.toString());
                
                try {
                    signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                        | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                        | UnrecoverableEntryException | IOException e3) {
                    // TODO Auto-generated catch block
                    e3.printStackTrace();
                    return;
                }

                /* FIXME: ADAPT TO NEW REALITY                
                try {
                    ret = port.readGeneral(publicKey, Long.valueOf(number), signature);
                } catch (EmptyBoardFault_Exception | InvalidNumberFault_Exception | NumberPostsFault_Exception e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                    return;
                }
                */

                try {
                    hash = CryptoTools.decryptSignature(servName, ret.get(1));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                        | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                        | UnrecoverableEntryException | IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return;
                }
                
                toHash = new ArrayList<>();
                toHash.add(servName);
                toHash.add(username);
                toHash.add(String.valueOf(parent.sn));
                toHash.add(ret.get(0));
                toHash.add(hash);

                try {
                    if (!CryptoTools.checkHash(toHash.toArray(new String[0]))) {
                        throw new RuntimeException("Hashes don't match");
                    }
                } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException
                        | CertificateException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }
                
				break;
        }
        
        System.out.println("HELLO");

        synchronized(parent) {
            if (parent.response == null) {
                parent.response = ret;
            } 
            //else System.out.println("\n");
            parent.notify();
        }
    }
}