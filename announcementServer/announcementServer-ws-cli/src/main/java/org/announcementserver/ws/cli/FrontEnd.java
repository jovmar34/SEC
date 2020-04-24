package org.announcementserver.ws.cli;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.announcementserver.common.CryptoTools;
import org.announcementserver.common.Constants;
import org.announcementserver.ws.AnnouncementServerPortType;
import org.announcementserver.ws.AnnouncementServerService;
import org.announcementserver.ws.EmptyBoardFault_Exception;
import org.announcementserver.ws.InvalidNumberFault_Exception;
import org.announcementserver.ws.MessageSizeFault_Exception;
import org.announcementserver.ws.NumberPostsFault_Exception;
import org.announcementserver.ws.PostTypeFault_Exception;
import org.announcementserver.ws.ReferredAnnouncementFault_Exception;
import org.announcementserver.ws.ReferredUserFault_Exception;
import org.announcementserver.ws.UserNotRegisteredFault_Exception;

import javax.xml.ws.BindingProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class FrontEnd {
    List<AnnouncementServerPortType> ports = null;
    List<String> wsUrls = null;
    AnnouncementServerPortType client = null;
    String username = null;
    Integer sn;
    String publicKey;
    List<String> response;
    Integer nServ;

    boolean verbose = false;

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public FrontEnd(String host, String faults) throws AnnouncementServerClientException {
        wsUrls = new ArrayList<>();
        ports = new ArrayList<>();
        Integer f = Integer.valueOf(faults);
        nServ = 3 * f + 1;
        System.out.println(String.format("NServ: %d", nServ));

        for (Integer i = 1; i <= nServ; i++) {
            wsUrls.add(String.format(Constants.WS_NAME_FORMAT, host, Constants.PORT_START + i));
        }

        createStub();

        if (nServ == 1)
            client = ports.get(0); // TODO: FOR SIMPLICTY
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
        Client cli;

        response = null;

        for (int i = 1; i <= nServ; i++) {
            cli = new Client(this, Operation.REGISTER, i, Thread.currentThread());
            cli.start();
        }

        try {
            Thread.currentThread().wait();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (response.size() == 3) {
            sn = Integer.valueOf(response.get(1));
        } else {
            sn = 0;
        }

        return response.get(0);
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

        String hash = CryptoTools.decryptSignature(Constants.SERVER_NAME, ret.get(1));

        if (CryptoTools.checkHash(Constants.SERVER_NAME, username, String.valueOf(sn), ret.get(0), hash)) {
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
            InvalidNumberFault_Exception, NumberPostsFault_Exception, ReferredUserFault_Exception, InvalidKeyException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        checkInit();

        //ADD SIGNATURE AND SN
        String response;
        String readKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey(clientID));

        List<String> toHash = new ArrayList<>();

        toHash.add(username);
        toHash.add(Constants.SERVER_NAME);
        toHash.add(sn.toString());
        toHash.add(readKey);
        toHash.add(number.toString());
    	
    	String signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
    	
        List<String> ret = client.read(readKey, publicKey, Long.valueOf(number), signature);
        
        String hash = CryptoTools.decryptSignature(Constants.SERVER_NAME, ret.get(1));

        if (CryptoTools.checkHash(Constants.SERVER_NAME, username, String.valueOf(sn), ret.get(0), hash)) {
            response = ret.get(0);
        } else {
            throw new RuntimeException("Hashes don't match");
        }

        sn++;

        return response;
    }

    public String readGeneral(Integer number) throws NoSuchAlgorithmException, UnrecoverableEntryException,
            KeyStoreException, CertificateException, IOException, EmptyBoardFault_Exception,
            InvalidNumberFault_Exception, NumberPostsFault_Exception, InvalidKeyException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {

        checkInit();

        String response;

        List<String> toHash = new ArrayList<>();

        toHash.add(username);
        toHash.add(Constants.SERVER_NAME);
        toHash.add(sn.toString());
        toHash.add(number.toString());
    	
    	String signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
    	
        List<String> ret = client.readGeneral(publicKey, Long.valueOf(number), signature);

        String hash = CryptoTools.decryptSignature(Constants.SERVER_NAME, ret.get(1));

        if (CryptoTools.checkHash(Constants.SERVER_NAME, username, String.valueOf(sn), ret.get(0), hash)) {
            response = ret.get(0);
        } else {
            throw new RuntimeException("Hashes don't match");
        }

        sn++;

        return response;
    }

    private void createStub() {
        AnnouncementServerPortType port;
        AnnouncementServerService service;
        for (String wsUrl: wsUrls) {
            if (verbose)
                System.out.println("Creating stub ...");
            service = new AnnouncementServerService();
            port = service.getAnnouncementServerPort();

            if (verbose)
                System.out.println("Setting endpoint address ...");
            BindingProvider bindingProvider = (BindingProvider) port;
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsUrl);

            ports.add(port);
            if (verbose) {
                System.out.print("Added client for: ");
                System.out.println(wsUrl);    
            }
        }
    }
}