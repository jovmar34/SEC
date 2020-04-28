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

    public WriteRet post(WriteReq request) {
        String hash = null;
        if (!request.getDestination().equals(myId)) throw new RuntimeException("Not me");

        System.out.println(request.getSender());
        System.out.println(request.getSignature());
        hash = decryptSignature(request.getSender(), request.getSignature());

        List<String> inHash = new ArrayList<>();
		inHash.add(request.getSender());
        inHash.add(request.getDestination());
        inHash.add(String.valueOf(request.getSeqNum()));
        inHash.addAll(strAnnouncement(request.getAnnouncement()));
        inHash.add(request.getAnnouncement().getSignature());
        inHash.add(hash);

        if (!checkHash(inHash.toArray(new String[0]))) 
            throw new RuntimeException("Possible Tampering in transport of post message");

        Announcement new_post = transformAnnouncement(request.getAnnouncement());
        new_post.setSeqNumber(request.getSeqNum());
        Integer sn = AnnouncementServer.getInstance().post(new_post);

        WriteRet response = new WriteRet();
        response.setSender(request.getDestination());
        response.setDestination(request.getSender());
        response.setSeqNum(sn);
        
        List<String> outHash = new ArrayList<>();
		outHash.add(response.getSender());
        outHash.add(response.getDestination());
        outHash.add(String.valueOf(response.getSeqNum()));
        
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

    private List<String> strAnnouncement(AnnouncementMessage announcement) {
        List<String> total = new ArrayList<>();
        total.add(announcement.getWriter());
        total.add(announcement.getMessage());
        total.add(announcement.getAnnouncementList().toString());
        total.add(announcement.getAnnouncementId());
        return total;
    }

    private Announcement transformAnnouncement(AnnouncementMessage announcement) {
        Announcement res = new Announcement();
        res.setAuthor(announcement.getWriter());
        res.setContent(announcement.getMessage());
        res.setReferences(announcement.getAnnouncementList());
        res.setId(announcement.getAnnouncementId());
        res.setSignature(announcement.getSignature());
        return res;
    }

}