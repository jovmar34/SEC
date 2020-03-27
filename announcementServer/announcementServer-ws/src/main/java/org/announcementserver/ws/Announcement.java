package org.announcementserver.ws;

import java.util.List;
import java.util.ArrayList;

public class Announcement {
	private String author;
	private String content;
	private String id;
	private ArrayList<String> references; 
	
	
	public Announcement() {
		this.references = new ArrayList<>();
	}
	
	public Announcement(String auth, String cont, String id) {
		this.author = auth;
		this.content = cont;
		this.id = id;
		this.references = new ArrayList<>();
	}
	
	public void setContent(String cont) {
		this.content=cont;
	}
	
	public void addReference(String ref) {
		this.references.add(ref);
	}
	
	public void setReferences(List<String> refs) {
		this.references = new ArrayList<String>(refs);
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setAuthor(String authid) {
		this.author = authid;
	}
	
	@Override
	public String toString() {
		return String.format("auth: %s, id: %s\n  text: \"%s\"\n  references: %s\n\n", this.author, this.id, this.content, this.references);
	}
}
