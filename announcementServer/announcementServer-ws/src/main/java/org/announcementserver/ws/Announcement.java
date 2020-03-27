package org.announcementserver.ws;

import java.util.ArrayList;

public class Announcement {
	private String author;
	private String content;
	private Integer id;
	private ArrayList<String> references; 
	
	
	public Announcement() {
		
	}
	
	public Announcement(String auth, String cont, Integer id, String refs) {
		this.author = auth;
		this.content = cont;
		this.id = id;
		this.references = refs;
	}
	
	public void setContent(String cont) {
		this.content=cont;
	}
	
	public void addReference(String ref) {
		this.references.add(ref);
	}
	
	public void setReferences(String refs) {
		this.references=refs;
	}
}
