package org.announcementserver.ws.cli;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.announcementserver.common.Constants;
import org.announcementserver.common.CryptoTools;
import org.announcementserver.ws.AnnouncementServerPortType;
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
    private Thread pThread;

    public String message;
    public List<String> references;
    public String boardKey;
    public Long number;

    public Client(FrontEnd parent, Operation op, Integer id, Thread parentThread) {
        this.parent = parent;
        this.op = op;
        this.servId = id;
        this.pThread = parentThread;
    }

    @Override
    public void run() {
        AnnouncementServerPortType port = parent.ports.get(servId - 1);
        String servName = Constants.SERVER_NAME + servId.toString();
        String username = parent.username;
        String publicKey = parent.publicKey;
        String signature = "";

        switch (this.op) {
            case REGISTER:
                System.out.print("I'm a thread");
                List<String> toHash = new ArrayList<>();
                toHash.add(username);
                toHash.add(servName);
                toHash.add(publicKey);

                System.out.println(toHash.toString());

                try {
                    signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                        | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                        | UnrecoverableEntryException | IOException e) {
                    e.printStackTrace();
                    this.interrupt();
                }

                List<String> ret = port.register(publicKey, signature);

                toHash = new ArrayList<>();
                toHash.add(servName);
                toHash.add(username);
                toHash.add(ret.get(0));

                if (ret.size() == 3) { // Not a new user
                    toHash.add(ret.get(1));
                }

                String hash = null;
                try {
                    hash = CryptoTools.decryptSignature(Constants.SERVER_NAME + servId.toString(),
                            ret.get(ret.size() - 1));
                } catch (InvalidKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                        | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                        | UnrecoverableEntryException | IOException e) {
                    e.printStackTrace();
                    this.interrupt();
                }

                toHash.add(hash);

                try {
                    if (CryptoTools.checkHash(toHash.toArray(new String[0]))) {
                        synchronized (parent) {
                            if (parent.response == null)
                                parent.response = ret;
                            parent.notify();
                        }
                    } else {
                        throw new RuntimeException("Hashes are not equal");
                    }
                } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException
                        | CertificateException | IOException e) {
                    e.printStackTrace();
                    this.interrupt();
                }
                break;
            case POST:
                try {
                    port.post(publicKey, message, references, signature);
                } catch (MessageSizeFault_Exception | PostTypeFault_Exception | ReferredAnnouncementFault_Exception
                        | ReferredUserFault_Exception | UserNotRegisteredFault_Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case POSTGENERAL:
                try {
                    port.postGeneral(publicKey, message, references, signature);
                } catch (MessageSizeFault_Exception | PostTypeFault_Exception | ReferredAnnouncementFault_Exception
                        | ReferredUserFault_Exception | UserNotRegisteredFault_Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case READ:
                try {
                    port.read(publicKey, boardKey, number, signature);
                } catch (EmptyBoardFault_Exception | InvalidNumberFault_Exception | NumberPostsFault_Exception
                        | ReferredUserFault_Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
				break;
            case READGENERAL:
                try {
                    port.readGeneral(publicKey, number, signature);
                } catch (EmptyBoardFault_Exception | InvalidNumberFault_Exception | NumberPostsFault_Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
				break;
        }
    }
}