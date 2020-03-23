package org.announcementserver.ws;

import java.util.ArrayList;

public class Announcement {
	private String author;
	private String content;
	private Integer id;
	private ArrayList<Announcement> references; 
	
	public Announcement(String auth, String cont, Integer id, ArrayList refs) {
		this.author = auth;
		this.content = cont;
		this.id = id;
		this.references = refs;
	}
}
