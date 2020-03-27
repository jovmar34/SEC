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
