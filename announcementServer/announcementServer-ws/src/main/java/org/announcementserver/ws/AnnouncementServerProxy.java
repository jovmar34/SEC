package org.announcementserver.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.announcementserver.common.CryptoTools;
import org.announcementserver.exceptions.EmptyBoardException;
import org.announcementserver.exceptions.InvalidNumberException;
import org.announcementserver.exceptions.MessageSizeException;
import org.announcementserver.exceptions.NumberPostsException;
import org.announcementserver.exceptions.PostTypeException;
import org.announcementserver.exceptions.ReferredAnnouncementException;
import org.announcementserver.exceptions.ReferredUserException;
import org.announcementserver.exceptions.UserNotRegisteredException;

public class AnnouncementServerProxy {
    protected String myId;
        
    private AnnouncementServer announcementServer;

    public AnnouncementServerProxy() {

    }

    public RegisterRet register(RegisterReq request) {
        String hash = null;
        if (!request.getDestination().equals(myId)) throw new RuntimeException("Not me");

        hash = decryptSignature(request.getSender(), request.getSignature());

		List<String> inHash = new ArrayList<>();
		inHash.add(request.getSender());
		inHash.add(request.getDestination());
		inHash.add(hash);
		
        if (!checkHash(inHash.toArray(new String[0]))) 
            throw new RuntimeException("Possible Tampering Detected");

        Integer sn = AnnouncementServer.getInstance().register(request.getSender());

        RegisterRet response = new RegisterRet();
        response.setSender(myId);
        response.setDestination(request.getSender());
        response.setSeqNumber(sn);
        response.setWts(0);
        response.setRid(0);

        List<String> outHash = new ArrayList<>();
        outHash.add(response.getSender());
        outHash.add(response.getDestination());
        outHash.add(String.valueOf(response.getSeqNumber()));
        outHash.add(String.valueOf(response.getWts())); //FIXME wts
        outHash.add(String.valueOf(response.getRid())); //FIXME rid

        response.setSignature(makeSignature(outHash.toArray(new String[0])));

        return response;
    }

    private String decryptSignature(String author, String signature) {
        try {
			return CryptoTools.decryptSignature(author, signature);
		} catch (Exception e) {
			throw new RuntimeException("Error: Possible tampering detected on Signature");
        }
    }

    private boolean checkHash(String[] args) {
        try {
			return CryptoTools.checkHash(args);
	    } catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
    }

    private String makeSignature(String[] args) {
        try {
            return CryptoTools.makeSignature(args);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}