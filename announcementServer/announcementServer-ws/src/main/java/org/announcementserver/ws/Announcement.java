package org.announcementserver.ws;

public class Announcement {
	private String author;
	private String content;
	private Integer id;
	private String references; 
	
	
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
	public void setReferences(String refs) {
		this.references=refs;
	}
	
	@Override
	public String toString() {
		return String.format("auth: %s, id: %d\n  text: \"%s\"\n  references: %s\n\n", this.author, this.id, this.content, this.references);
	}
}
