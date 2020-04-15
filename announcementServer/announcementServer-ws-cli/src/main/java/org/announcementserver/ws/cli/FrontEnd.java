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

import org.announcementserver.common.CryptoTools;
import org.announcementserver.ws.EmptyBoardFault_Exception;
import org.announcementserver.ws.InvalidNumberFault_Exception;
import org.announcementserver.ws.MessageSizeFault_Exception;
import org.announcementserver.ws.NumberPostsFault_Exception;
import org.announcementserver.ws.PostTypeFault_Exception;
import org.announcementserver.ws.ReferredAnnouncementFault_Exception;
import org.announcementserver.ws.ReferredUserFault_Exception;
import org.announcementserver.ws.UserNotRegisteredFault_Exception;

public class FrontEnd {
    AnnouncementServerClient client = null;
    String username = null;
    Integer sn;
    String publicKey;

    public FrontEnd(String wsUrl) throws AnnouncementServerClientException {
        client = new AnnouncementServerClient(wsUrl);
    }

    public void init(String username) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
            CertificateException, IOException {
        this.username = username;
        this.publicKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey(username));
    }

    public void checkInit() {
        if (this.username == null)
            throw new RuntimeException("Username not Initialized");
    }

    public String register() throws InvalidKeyException, CertificateException, KeyStoreException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
            UnrecoverableEntryException, IOException {

        checkInit();

        String response = null;

        String signature = "";
        List<String> toHash = new ArrayList<>();
        toHash.add(username);
        toHash.add("server");
        toHash.add(publicKey);

        signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));

        List<String> ret = client.register(publicKey, signature);
        toHash = new ArrayList<>();
        toHash.add("server");
        toHash.add(username);
        toHash.add(ret.get(0));

        if (ret.size() == 3) { // Not a new user
            toHash.add(ret.get(1));
        }

        String hash = CryptoTools.decryptSignature(Constants.SERVER_NAME, ret.get(ret.size() - 1));

        toHash.add(hash);

        if (CryptoTools.checkHash(toHash.toArray(new String[0]))) {
            response = ret.get(0);
        } else {
            throw new RuntimeException("Hashes are not equal");
        }

        if (ret.size() == 3) {
            sn = Integer.valueOf(ret.get(1));
        }

        return response;
    }

    public String post(String message, List<String> announcementList)
            throws InvalidKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnrecoverableEntryException,
            IOException, MessageSizeFault_Exception, PostTypeFault_Exception, ReferredAnnouncementFault_Exception,
            ReferredUserFault_Exception, UserNotRegisteredFault_Exception {

        checkInit();

        String response;

        List<String> toHash = new ArrayList<>();
        toHash.add(username);
        toHash.add(Constants.SERVER_NAME);
        toHash.add(sn.toString());
        toHash.add(publicKey);
        toHash.add(message);
        toHash.addAll(announcementList);

        String signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));

        List<String> ret = client.post(publicKey, message, announcementList, signature);

        String hash = CryptoTools.decryptSignature("server", ret.get(1));

        if (CryptoTools.checkHash("server", username, String.valueOf(sn), ret.get(0), hash)) {
            response = ret.get(0);
        } else {
            throw new RuntimeException("Hashes are not equal");
        }

        sn++;

        return response;
    }

    public String postGeneral(String message, List<String> announcementList)
            throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, CertificateException,
            IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
            MessageSizeFault_Exception, PostTypeFault_Exception, ReferredAnnouncementFault_Exception,
            ReferredUserFault_Exception, UserNotRegisteredFault_Exception {

        checkInit();

        List<String> toHash = new ArrayList<>();
        toHash.add(username); // src
        toHash.add(Constants.SERVER_NAME); // dest
        toHash.add(sn.toString());
        toHash.add(publicKey);
        toHash.add(message);
        toHash.addAll(announcementList);

        String signature = "";
        signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));

        List<String> ret = client.postGeneral(publicKey, message, announcementList, signature);

        String hash = CryptoTools.decryptSignature(Constants.SERVER_NAME, ret.get(1));

        String response;

        if (CryptoTools.checkHash(Constants.SERVER_NAME, username, String.valueOf(sn), ret.get(0), hash)) {
            response = ret.get(0);
        } else {
            throw new RuntimeException("Hashes are not equal");
        }

        sn++;

        return response;
    }

    public String read(String clientID, Integer number) throws NoSuchAlgorithmException, UnrecoverableEntryException,
            KeyStoreException, CertificateException, IOException, EmptyBoardFault_Exception,
            InvalidNumberFault_Exception, NumberPostsFault_Exception, ReferredUserFault_Exception {

        checkInit();

        //ADD SIGNATURE AND SN
        String response;
        String readKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey(clientID));
    	
    	String signature = CryptoTools.makeHash(publicKey, String.valueOf(number));
    	
        List<String> ret = client.read(readKey, Long.valueOf(number), signature);
        
        if (CryptoTools.checkHash(ret.toArray(new String[0]))) {
            response = ret.get(0);
        } else {
            throw new RuntimeException("Hashes don't match");
        }

        return response;
    }

    public String readGeneral(Integer number) throws NoSuchAlgorithmException, UnrecoverableEntryException,
            KeyStoreException, CertificateException, IOException, EmptyBoardFault_Exception,
            InvalidNumberFault_Exception, NumberPostsFault_Exception {

        checkInit();

        String response;
        String signature = CryptoTools.makeHash(String.valueOf(number));
    	
        List<String> ret = client.readGeneral(Long.valueOf(number), signature);

        if (CryptoTools.checkHash(ret.toArray(new String[0]))) {
            response = ret.get(0);
        } else {
            throw new RuntimeException("Hashes don't match");
        }

        return response;
    }
}