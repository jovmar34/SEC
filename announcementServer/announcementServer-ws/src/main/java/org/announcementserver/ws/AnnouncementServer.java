package org.announcementserver.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AnnouncementServer {
	private HashMap<Integer, String> pks; // client id => public key association
	private HashMap<String, Integer> clients; // public key => client id association (OVERKILL?) FIXME
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
		this.clients = new HashMap<>();
		this.pks = new HashMap<>();
		
		try {
			File f = new File("src/main/resources/clients.txt");
			Scanner scan = new Scanner(f);
			while (scan.hasNextLine()) {
				String[] data = scan.nextLine().split(" ");
				this.pks.put(Integer.parseInt(data[0]), data[1]);
				this.clients.put(data[1], Integer.parseInt(data[0]));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
	    }
	}
	
	/* Register */
	public  String register(String publicKey) {
		
		// TODO: assign association on file (client - pk)
		
		if (!personalBoards.containsKey(publicKey)) {
			personalBoards.put(publicKey, new ArrayList<>());
			return String.format("Welcome new user (pk: %s)! ", publicKey);
		
		} else {
			return "PublicKey provided is already associated!";
		}
	}
	
	/* Post */
	public String post(String publicKey, String message, List<String> announcementList) {
		
		String result;
		
		/* Verify existence of publicKey */
		if (!personalBoards.containsKey(publicKey)) {
			result = "Error: No such association for PublicKey sent.";
			return result;
		}
		
		/* Verify correct size of message */
		if (message.length() > 255) {
			result = "Error: Too many characters in the message (max 255).";
			return result;
		}
		
		ArrayList<Announcement> board = personalBoards.get(publicKey);
		Announcement post = new Announcement();
		post.setContent(message);
		
		/* Verify structure of announcementList */
		for (String reference : announcementList) {			
			String[] parts = reference.split("a|c"); // [<p|g>, author_id, ctr_id]
			String pk = pks.get(Integer.parseInt(parts[1]));
			
			if (!personalBoards.containsKey(pk)) {
				result = "Error: post in reference doesn't exist";
				return result;
			}
			
			if (parts[0] == "p") {								
				if (personalBoards.get(pk).size() < Integer.parseInt(parts[2])) {
					result = "Error: post in reference doesn't exist";
					return result;
				}
				
			} else if (parts[0] == "g") {
				if (generalBoard.size() < Integer.parseInt(parts[2])) {
					result = "Error: post in reference doesn't exist";
					return result;
				}
			}
			
			
			post.addReference(reference);
		}
		
		post.setId(String.format("pc%da%d", clients.get(publicKey), board.size()));
		post.setAuthor(String.format("client%d", clients.get(publicKey)));
		
		board.add(post);
		
		result = "Success";
		return result;
	}
	
	/* Post General */
	public String postGeneral(String publicKey, String message, List<String> announcementList) {
		
		String result = "";
		
		/* Verify existence of publicKey */
		if (!personalBoards.containsKey(publicKey)) {
			result = "Error: No such association for PublicKey sent.";
			return result;
		}
		
		/* Verify correct size of message */
		if (message.length() > 255) {
			result = "Error: Too many characters in the message (max 255).";
			return result;
		}
		
		Announcement post = new Announcement();
		post.setContent(message);
		
		/* Verify structure of announcementList */
		for (String reference : announcementList) {
			String[] parts = reference.split("a|c"); // [<p|g>, author_id, ctr_id]
			String pk = pks.get(Integer.parseInt(parts[1]));
			
			if (!personalBoards.containsKey(pk)) {
				result = "Error: post in reference doesn't exist";
				return result;
			}
			
			if (parts[0] == "p") {								
				if (personalBoards.get(pk).size() < Integer.parseInt(parts[2])) {
					result = "Error: post in reference doesn't exist";
					return result;
				}
				
			} else if (parts[0] == "g") {
				if (generalBoard.size() < Integer.parseInt(parts[2])) {
					result = "Error: post in reference doesn't exist";
					return result;
				}
			} else {
				result = "Error: type of post in reference incorrect";
				return result;
			}
			
			post.addReference(reference);
		}
		
		post.setId(String.format("pc%da%d", clients.get(publicKey), generalBoard.size()));
		post.setAuthor(String.format("client%d", clients.get(publicKey)));
		
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
