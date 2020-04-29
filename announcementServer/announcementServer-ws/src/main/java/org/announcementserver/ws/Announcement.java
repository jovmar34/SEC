package org.announcementserver.ws;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class Announcement implements Serializable {
	private static final long serialVersionUID = -4546920147162956800L;
	protected String author;
	protected String content;
	protected Integer id;
	protected ArrayList<String> references;
	protected String signature;
	protected String type;
	
	public Announcement() {
		this.references = new ArrayList<>();
	}
	
	public Announcement(String auth, String cont, Integer id) {
		this.author = auth;
		this.content = cont;
		this.id = id;
		this.references = new ArrayList<>();
	}
	
	public void setContent(String cont) {
		this.content = cont;
	}
	
	public void addReference(String ref) {
		this.references.add(ref);
	}
	
	public void setReferences(List<String> refs) {
		this.references = new ArrayList<String>(refs);
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setAuthor(String authid) {
		this.author = authid;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return String.format("auth: %s, id: %s, type: %s\n  text: \"%s\"\n  references: %s",
		 this.author, this.id,  this.type, this.content,
		 this.references);
	}
}
