package org.announcementserver.utils;

import java.util.ArrayList;
import java.util.List;
import org.announcementserver.ws.AnnouncementMessage;
import org.announcementserver.ws.Announcement;

/*
* Announcement Tools
*/

public class AnnouncementTools {
	
	// Was in AnnouncementServer.java
	// Was in AnnouncementServerProxy.java
	public static Announcement transformAnnouncement(AnnouncementMessage announcement) {
		Announcement res = new Announcement();
	    res.setAuthor(announcement.getWriter());
	    res.setContent(announcement.getMessage());
	    res.setReferences(announcement.getAnnouncementList());
	    res.setId(announcement.getWts());
	    res.setType(announcement.getType());
	    res.setSignature(announcement.getSignature());
	    return res;
	}
	
	// Was in AnnouncementServerProxy.java
    public static List<String> listToSign(List<AnnouncementMessage> posts) {
        List<String> res = new ArrayList<>();

        for (AnnouncementMessage post: posts) {
            res.addAll(postToSign(post, true));
        }

        return res;
    }
    
    // Was in AnnouncementServerProxy.java
    public static List<String> postToSign(AnnouncementMessage post, boolean signature) {
        List<String> res = new ArrayList<>();

        res.add(post.getWriter());
        res.add(post.getMessage());
        res.add(post.getAnnouncementList().toString());
        res.add(String.valueOf(post.getWts()));
        res.add(post.getType());
        if (signature) res.add(post.getSignature());

        return res;
    }
    
    // Was in AnnouncementServerProxy.java
    public String postToHash(AnnouncementMessage post, boolean signature) {
        String res = String.format("%s,%s,%d,%s", post.getWriter(), post.getMessage(),
            post.getWts(), post.getAnnouncementList().toString());
        
        if (signature) res += "," + post.getSignature();

        return res + "\n";
    }

    
    // Was in AnnouncementServerProxy.java
    public String announcementListToString(List<AnnouncementMessage> posts) {
        String res = "";

        for (AnnouncementMessage post: posts) {
            res += postToHash(post, true);
        }

        return res;
    }
}