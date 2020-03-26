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
	public String post(String publicKey, String message, ArrayList refs) {
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
	public String postGeneral(String publicKey, String message, String announcement) {
		//create a Post with message and parse announcements I want to reference 
		
		//create a Post to personalBoard with message and parse the announcements I want to reference
	
		Announcement post = new Announcement();
		post.setContent(message);
		post.setReferences(refs);
		generalBoard.add(post);
				
		String res= "New post submitted";
				
		return res;
	}
	
	/* Read */
	public String read(String publicKey, String number) {
		//number and PublicKey enough to find a post in PersonalBoards
		
		ArrayList<Announcement> board = personalBoards.get(publicKey); //get the personal board
		int nposts = Integer.parseInt(number);
		if(nposts==0) {
			return board;
		}
		
		else {
			ArrayList<Announcement> posts = new ArrayList<Announcement>(nposts);       //save the posts you want to see
			
			int stop= board.size()-nposts;						
			
			for(int i = board.size()-1; i>stop-1; i--) {
				posts.add(board.get(i));
			}
			return posts;
			
		
		}
		
		}
	
	/* Read General */
	public String readGeneral(String number) {
		//number and PublicKey enough to find a post in GeneralBoard
		ArrayList<Announcement> board = generalBoard; //get the personal board
		int nposts = Integer.parseInt(number);
		if(nposts==0) {
			return board;
		}
		
		else {
			ArrayList<Announcement> posts = new ArrayList<Announcement>(nposts);       //save the posts you want to see
			
			int stop= board.size()-nposts;						
			
			for(int i = board.size()-1; i>stop-1; i--) {
				posts.add(board.get(i));
			}
			return posts;
			
		
		}
	}
}
