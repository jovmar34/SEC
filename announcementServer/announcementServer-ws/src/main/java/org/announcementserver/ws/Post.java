package org.announcementserver.ws;

import java.util.ArrayList;

public class Post {
	private String author;
	private String content;
	private Integer id;
	private ArrayList<Post> references; 
	
	public Post(String auth, String cont, Integer id, ArrayList refs) {
		this.author = auth;
		this.content = cont;
		this.id = id;
		this.references = refs;
	}
}
