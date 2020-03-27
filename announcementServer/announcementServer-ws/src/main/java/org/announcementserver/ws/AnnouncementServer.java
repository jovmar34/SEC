package org.announcementserver.ws;

import java.util.ArrayList;
import java.util.HashMap;

public class AnnouncementServer {
	private HashMap<String, Announcement> generalBoard;
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
		
		// TODO: assign association on file (client - pk)
		
		if (!personalBoards.containsKey(publicKey)) {
			personalBoards.put(publicKey, new ArrayList<>());
			return String.format("Welcome new user (pk: %s)! ", publicKey);;
		
		} else {
			return "PublicKey provided is already associated! ";
		}
	}
	
	/* Post */
	public String post(String publicKey, String message, List<String> announcementList) {
		
		String result = "";
		
		/* Verify existence of publicKey */
		if (!personalBoards.containsKey(publicKey)) {
			result = "Error: No such association for PublicKey sent.";
			return result;
		}
		
		/* Verify correct size of message */
		if (!message.size()<=255) {
			result = "Error: Too many characters in the message (max 255).";
			return result;
		}
		
		ArrayList<Announcement> board = personalBoards.get(publicKey);
		Announcement post = new Announcement();
		post.setContent(message);
		
		/* Verify structure of announcementList */
		for (String reference : announcementList) {
			
			// TODO: Make sure everything given exists and makes sense
			//String[] parts = reference.split(";");
			
			//if (parts[0].equals("p")) { //personal board
				
				
			//} else { //general board
				
			//}
			
			post.addReference(reference);
		}
		board.add(post);
		
		result = "Success";
		return result;
	}
	
	/* Post General */
	public String postGeneral(String publicKey, String message, List<String> announcementList) {
		
		String result = "";
		
		/* Verify existence of publicKey */
		if (!generalBoard.containsKey(publicKey)) {
			result = "Error: No such association for PublicKey sent.";
			return result;
		}
		
		/* Verify correct size of message */
		if (!message.size()<=255) {
			result = "Error: Too many characters in the message (max 255).";
			return result;
		}
		
		Announcement post = new Announcement();
		post.setContent(message);
		
		/* Verify structure of announcementList */
		for (String reference : announcementList) {
			
			// TODO: Make sure everything given exists and makes sense
			
			//String[] parts = reference.split(";");
			
			//if (parts[0].equals("p")) { //personal board
				
				
			//} else { //general board
				
			//}
			
			post.addReference(reference);
		}
		generalBoard.add(post);
				
		result= "Success";
		return result;
	}
	
	/* Read */
	public String read(String publicKey, Long number) {
		//number and PublicKey enough to find a post in PersonalBoards
		
		ArrayList<Announcement> board = personalBoards.get(publicKey); //get the personal board
		int nposts = number.intValue();
		if(number==0) {
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
	public String readGeneral(Long number) {
		//number and PublicKey enough to find a post in GeneralBoard
		ArrayList<Announcement> board = generalBoard; //get the personal board
		int nposts = number.intValue();
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
