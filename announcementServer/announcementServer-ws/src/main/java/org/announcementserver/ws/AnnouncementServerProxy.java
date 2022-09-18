package org.announcementserver.ws;

import java.util.ArrayList;
import java.util.List;
import org.announcementserver.common.*;
import org.announcementserver.utils.AnnouncementTools;

public class AnnouncementServerProxy {
    protected String myId;
    private static AnnouncementServerProxy instance = null; //Singleton

    public AnnouncementServerProxy() {
    	
    }
    
    public void setId (String id) {
    	this.myId = id;
    }
    
	public static AnnouncementServerProxy getInstance() {
		if (instance == null) {
			instance = new AnnouncementServerProxy();
		}
		return instance;
	}

    // --- Register ---------
    public RegisterRet register(RegisterReq request) {
        String hash = null;
        if (!request.getDestination().equals(myId)) throw new RuntimeException(myId);

        hash = decryptSignature(request.getSender(), request.getSignature());

		List<String> inHash = new ArrayList<>();
		inHash.add(request.getSender());
		inHash.add(request.getDestination());
		inHash.add(hash);
		
        if (!checkHash(inHash.toArray(new String[0]))) 
            throw new RuntimeException("Error: Possible tampering detected on Hash");

        List<Integer> nums = AnnouncementServer.getInstance().register(request.getSender());

        RegisterRet response = new RegisterRet();
        response.setSender(myId);
        response.setDestination(request.getSender());
        response.setSeqNumber(nums.get(0));
        response.setWts(nums.get(1));
        response.setRid(0);

        List<String> outHash = new ArrayList<>();
        outHash.add(response.getSender());
        outHash.add(response.getDestination());
        outHash.add(String.valueOf(response.getSeqNumber()));
        outHash.add(String.valueOf(response.getWts()));
        outHash.add(String.valueOf(response.getRid()));

        response.setSignature(makeSignature(outHash.toArray(new String[0])));

        return response;
    }
    
    // --- POST ---------
    public WriteRet post(WriteReq request) {
        String hash = null;
        if (!request.getDestination().equals(myId)) throw new RuntimeException("Not me");

        hash = decryptSignature(request.getSender(), request.getSignature());

        List<String> inHash = new ArrayList<>();
		inHash.add(request.getSender());
        inHash.add(request.getDestination());
        inHash.add(String.valueOf(request.getSeqNumber()));
        inHash.addAll(AnnouncementTools.postToSign(request.getAnnouncement(), true));
        inHash.add(hash);

        if (!checkHash(inHash.toArray(new String[0]))) 
            throw new RuntimeException("Possible Tampering in transport of post message");

        if (!request.getSender().equals(request.getAnnouncement().getWriter()))
            throw new RuntimeException("The poster is different than the writer");

        Announcement new_post = AnnouncementTools.transformAnnouncement(request.getAnnouncement());
        List<Integer> numsPost = AnnouncementServer.getInstance().post(new_post, request.getSeqNumber());

        WriteRet response = new WriteRet();
        response.setSender(request.getDestination());
        response.setDestination(request.getSender());
        response.setSeqNumber(numsPost.get(0));
        response.setWts(numsPost.get(1));
        
        List<String> outHash = new ArrayList<>();

		outHash.add(response.getSender());
        outHash.add(response.getDestination());
        outHash.add(String.valueOf(response.getSeqNumber()));
        
        response.setSignature(makeSignature(outHash.toArray(new String[0])));

        return response;
    }
    
    // --- POST GENERAL ---------
    public WriteRet postGeneral(WriteReq request) {
    	String hash = null;
    	if (!request.getDestination().equals(myId)) throw new RuntimeException("Not me");

        hash = decryptSignature(request.getSender(), request.getSignature());
        
        List<String> inHash = new ArrayList<>();
		inHash.add(request.getSender());
        inHash.add(request.getDestination());
        inHash.add(String.valueOf(request.getSeqNumber()));
        inHash.addAll(AnnouncementTools.postToSign(request.getAnnouncement(), true));
        inHash.add(hash);
        
        if (!checkHash(inHash.toArray(new String[0]))) 
            throw new RuntimeException("Possible Tampering in transport of post message");
        
        Announcement new_post = AnnouncementTools.transformAnnouncement(request.getAnnouncement());
        Integer sn = AnnouncementServer.getInstance().postGeneral(new_post, request.getSeqNumber());
        
        WriteRet response = new WriteRet();
        response.setSender(request.getDestination());
        response.setDestination(request.getSender());
        response.setSeqNumber(sn);
        
        List<String> outHash = new ArrayList<>();

		outHash.add(response.getSender());
        outHash.add(response.getDestination());
        outHash.add(String.valueOf(response.getSeqNumber()));
        
        response.setSignature(makeSignature(outHash.toArray(new String[0])));
    	
    	return response;
    }
    
    // --- READ ---------
    public ReadRet read(ReadReq request) {
        String hash = null;
        if(!request.getDestination().equals(myId)) throw new RuntimeException("Not me");

        hash = decryptSignature(request.getSender(), request.getSignature());

        List<String> inHash = new ArrayList<>();
		inHash.add(request.getSender());
        inHash.add(request.getDestination());
        inHash.add(String.valueOf(request.getSeqNumber()));
        inHash.add(request.getOwner());
        inHash.add(String.valueOf(request.getRid()));
        inHash.add(String.valueOf(request.getNumber()));
        inHash.add(hash);

        if (!checkHash(inHash.toArray(new String[0]))) 
            throw new RuntimeException("Possible Tampering in transport of post message");

        List<Announcement> posts = AnnouncementServer.getInstance().read(
            request.getSender(), request.getOwner(), request.getNumber(), request.getSeqNumber());

        ReadRet response = new ReadRet();
        response.setSender(request.getDestination());
        response.setDestination(request.getSender());
        response.setSeqNumber(request.getSeqNumber());
        response.setRid(request.getRid());
        response.getAnnouncements().addAll(transformAnnouncementList(posts));

        List<String> outHash = new ArrayList<>();
		outHash.add(response.getSender());
        outHash.add(response.getDestination());
        outHash.add(String.valueOf(response.getSeqNumber()));
        outHash.add(String.valueOf(response.getRid()));
        outHash.addAll(AnnouncementTools.listToSign(response.getAnnouncements()));

        response.setSignature(makeSignature(outHash.toArray(new String[0])));

        return response;
    }
    
    // --- READ GENERAL ---------
    public ReadRet readGeneral(ReadGeneralReq request) {
    	String hash = null;
    	if(!request.getDestination().equals(myId)) throw new RuntimeException("Not me");
    	
    	hash = decryptSignature(request.getSender(), request.getSignature());
    	
        List<String> inHash = new ArrayList<>();
		inHash.add(request.getSender());
        inHash.add(request.getDestination());
        inHash.add(String.valueOf(request.getSeqNumber()));
        inHash.add(String.valueOf(request.getRid()));
        inHash.add(String.valueOf(request.getNumber()));
        inHash.add(hash);

        if (!checkHash(inHash.toArray(new String[0]))) 
            throw new RuntimeException("Possible Tampering in transport of post message");


        List<Announcement> posts = AnnouncementServer.getInstance().readGeneral(request.getSender(), request.getNumber(), request.getSeqNumber());

        ReadRet response = new ReadRet();
        response.setSender(request.getDestination());
        response.setDestination(request.getSender());
        response.setSeqNumber(request.getSeqNumber());
        response.setRid(request.getRid());
        response.getAnnouncements().addAll(transformAnnouncementList(posts));

        List<String> outHash = new ArrayList<>();
		outHash.add(response.getSender());
        outHash.add(response.getDestination());
        outHash.add(String.valueOf(response.getSeqNumber()));
        outHash.add(String.valueOf(response.getRid()));
        outHash.addAll(AnnouncementTools.listToSign(response.getAnnouncements()));

        response.setSignature(makeSignature(outHash.toArray(new String[0])));
    	
    	return response;
    }
    
    // --- WRITE BACK --------
    public WriteBackRet writeBack(WriteBackReq request) {
    	String hash = null;
    	if (!request.getDestination().equals(myId)) throw new RuntimeException("Not me");
    	
    	hash = decryptSignature(request.getSender(), request.getSignature());
    	
    	List<String> inHash = new ArrayList<>();
    	inHash.add(request.getSender());
    	inHash.add(request.getDestination());
    	inHash.add(String.valueOf(request.getSeqNumber()));
    	inHash.addAll(AnnouncementTools.listToSign(request.getAnnouncements()));
        inHash.add(hash);
        
        // verifySigs (make sure the messages are valid in the context of the system)
    	
        if (!checkHash(inHash.toArray(new String[0]))) 
            throw new RuntimeException("Possible Tampering in transport of post message");
    	
        Integer ts = AnnouncementServer.getInstance().writeBack(request.getSender(), request.getAnnouncements(), request.getSeqNumber());
        
    	WriteBackRet response = new WriteBackRet();
    	response.setSender(request.getDestination());
    	response.setDestination(request.getSender());
    	response.setSeqNumber(ts);
    			
    	List<String> outHash = new ArrayList<>();
    	
    	outHash.add(response.getSender());
    	outHash.add(response.getDestination());
    	outHash.add(String.valueOf(response.getSeqNumber()));
    	
    	response.setSignature(makeSignature(outHash.toArray(new String[0])));
    	
    	return response;
    }
    
    
    // -- Auxiliary Functions -------------------

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
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private List<AnnouncementMessage> transformAnnouncementList(List<Announcement> posts) {
        List<AnnouncementMessage> res = new ArrayList<>();
        AnnouncementMessage mess;

        for (Announcement post: posts) {
            mess = new AnnouncementMessage();
            mess.setWriter(post.author);
            mess.setMessage(post.content);
            mess.getAnnouncementList().addAll(post.references);
            mess.setWts(post.id);
            mess.setType(post.type);
            mess.setSignature(post.signature);
            res.add(mess);
        }

        return res;
    }
    
}