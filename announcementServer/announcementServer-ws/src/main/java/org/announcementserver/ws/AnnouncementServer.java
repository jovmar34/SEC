package org.announcementserver.ws;

import java.util.ArrayList;
import java.util.HashMap;

public class AnnouncementServer {
	private ArrayList<Post> generalBoard;
	private HashMap<String, ArrayList<Post>> personalBoards;
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
	public String post(String publicKey, String message, String announcement) {
		//create a Post to personalBoard with message and parse the announcements I want to reference
		return "not implemented yet";
	}
	
	/* Post General */
	public String postGeneral(String publicKey, String message, String announcement) {
		//create a Post with message and parse announcements I want to reference 
		return "not implemented yet";
	}
	
	/* Read */
	public String read(String publicKey, String number) {
		//number and PublicKey enough to find a post in PersonalBoards
		return "not implemented yet";
	}
	
	/* Read General */
	public String readGeneral(String number) {
		//number and PublicKey enough to find a post in GeneralBoard
		return "not implemented yet";
	}
}
