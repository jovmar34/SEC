package org.announcementserver.ws;

import java.util.ArrayList;
import java.util.HashMap;

public class AnnouncementServer {
	private ArrayList<Announcement> generalBoard;
	private HashMap<String, ArrayList<Announcement>> personalBoards;
	private static AnnouncementServer instance = null; //Singleton, maybe unnecessary
	
	public static AnnouncementServer getInstance() {
		if (instance == null) {
			instance = new AnnouncementServer();
		}
		return instance;
	}
	
	private AnnouncementServer () {
		this.generalBoard = new ArrayList<>();
		this.personalBoards = new HashMap<>();
	}
	
	/* Register */
	public  String register(String publicKey) {
		//create a personalBoard for this user
		String res = "";
		if (!personalBoards.containsKey(publicKey)) {
			res += String.format("Welcome new user (pk: %s)! ", publicKey);
			personalBoards.put(publicKey, new ArrayList<>());
		}
		return res + "ok";
	}
	
	/* Post */
	public String post(String publicKey, String message, String refs) {
		//create a Post to personalBoard with message and parse the announcements I want to reference
		ArrayList<Announcement> board= personalBoards.get(publicKey);
		Announcement post = new Announcement();
		post.setContent(message);
		post.setReferences(refs);
		board.add(post);
		String res= "New post submitted";
		
		return res;
	}
	
	/* Post General */
	public String postGeneral(String publicKey, String message, String refs) {
		//create a Post with message and parse announcements I want to reference 
	
		Announcement post = new Announcement();
		post.setContent(message);
		post.setReferences(refs);
		generalBoard.add(post);
				
		String res= "New post submitted";
				
		return res;
	}
	
	/* Read */
	public String read(String publicKey, Long number) {
		//number and PublicKey enough to find a post in PersonalBoards
		if (number < 0) return "Invalid number";
		
		if (!personalBoards.containsKey(publicKey)) return "Unknown user";
		
		ArrayList<Announcement> board = personalBoards.get(publicKey); //get the personal board
		
		if (board.isEmpty()) return "No posts";
		
		if (number > board.size()) return "Not enough posts";
	
		String res = "";      //save the posts you want to see
		int end = board.size() - 1;
		
		for(int i = 0; i < number; i++) {
			res += board.get(end - i).toString();
		}
		
		return res;
	}
	
	/* Read General */
	public String readGeneral(Long number) {
		//number and PublicKey enough to find a post in GeneralBoard
		if (number < 0) return "Invalid number";
		
		if (generalBoard.isEmpty()) return "No posts";
		
		if (number > generalBoard.size()) return "Not enough posts";

		String res = "";      //save the posts you want to see
		int end = generalBoard.size() - 1;
		
		for(int i = 0; i < number; i++) {
			res += generalBoard.get(end - i).toString();
		}
		
		return res;
	}
	
	/* For testing purposes */
	public void putGeneral(Announcement ann) {
		generalBoard.add(ann);
	}
	
	public void putPersonal(String publicKey, Announcement ann) {
		personalBoards.get(publicKey).add(ann);
	}
	
	public void clean() {
		personalBoards.clear();
		generalBoard.clear();
	}
}
