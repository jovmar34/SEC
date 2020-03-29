package org.announcementserver.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.announcementserver.exceptions.EmptyBoardException;
import org.announcementserver.exceptions.InvalidNumberException;
import org.announcementserver.exceptions.MessageSizeException;
import org.announcementserver.exceptions.NumberPostsException;
import org.announcementserver.exceptions.PostTypeException;
import org.announcementserver.exceptions.ReferredAnnouncementException;
import org.announcementserver.exceptions.ReferredUserException;
import org.announcementserver.exceptions.UserNotRegisteredException;
import org.announcementserver.exceptions.UserAlreadyRegisteredException;

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
	}
	
	/* Register */
	public String register(String publicKey) throws UserAlreadyRegisteredException {
		
		// TODO: assign association on file (client - pk)
		
		if (!personalBoards.containsKey(publicKey)) {
			personalBoards.put(publicKey, new ArrayList<>());
			int clientID = personalBoards.size();
			clients.put(publicKey, clientID );
			pks.put(clientID, publicKey);
			return "Welcome new user!";
		
		} else {
			throw new UserAlreadyRegisteredException("User is already registered");
		}
	}
	
	/* Post */
	public String post(String publicKey, String message, List<String> announcementList) throws UserNotRegisteredException, MessageSizeException, ReferredUserException, PostTypeException, ReferredAnnouncementException {
		
		String result;
		
		/* Verify existence of publicKey */
		if (!personalBoards.containsKey(publicKey))  {
			throw new UserNotRegisteredException("User is not registered yet");
			
		}
		
		/* Verify correct size of message */
		if (message.length() > 255) {
			throw new MessageSizeException("Too many characters in the message (max:255)");
		}
		
		ArrayList<Announcement> board = personalBoards.get(publicKey);
		Announcement post = new Announcement();
		post.setContent(message);
		
		/* Verify structure of announcementList */
		for (String reference : announcementList) {			
			String[] parts = reference.split("a|c"); // [<p|g>, author_id, ctr_id]
			String pk = pks.get(Integer.parseInt(parts[1]));
			
			if (!personalBoards.containsKey(pk)) {
				throw new ReferredUserException("Referred user doesn’t exist");
			}
			
			if (parts[0] == "p") {								
				if (personalBoards.get(pk).size() < Integer.parseInt(parts[2])) {
					throw new ReferredAnnouncementException("The referred announcement doesn’t exist");
				}
				
			} else if (parts[0] == "g") {
				if (generalBoard.size() < Integer.parseInt(parts[2])) {
					throw new ReferredAnnouncementException("The referred announcement doesn’t exist");
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
	public String postGeneral(String publicKey, String message, List<String> announcementList) throws UserNotRegisteredException, MessageSizeException, ReferredUserException, ReferredAnnouncementException, PostTypeException {
		
		String result = "";
		
		/* Verify existence of publicKey */
		if (!personalBoards.containsKey(publicKey)) {
			throw new UserNotRegisteredException("User is already registered");
		}
		
		/* Verify correct size of message */
		if (message.length() > 255) {
			throw new MessageSizeException("Too many characters in the message (max:255)");
		}
		
		Announcement post = new Announcement();
		post.setContent(message);
		
		/* Verify structure of announcementList */
		for (String reference : announcementList) {
			String[] parts = reference.split("a|c"); // [<p|g>, author_id, ctr_id]
			String pk = pks.get(Integer.parseInt(parts[1]));
			
			if (!personalBoards.containsKey(pk)) {
				throw new ReferredUserException("Referred user doesn’t exist");
			}
			
			if (parts[0] == "p") {								
				if (personalBoards.get(pk).size() < Integer.parseInt(parts[2])) {
					throw new ReferredAnnouncementException("The referred announcement doesn’t exist");
				}
				
			} else if (parts[0] == "g") {
				if (generalBoard.size() < Integer.parseInt(parts[2])) {
					throw new ReferredAnnouncementException("The referred announcement doesn’t exist");
				}
			} else {
				throw new PostTypeException("The type of post in reference is incorrect");
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
	public String read(String publicKey, Long number) throws InvalidNumberException, ReferredUserException, EmptyBoardException, NumberPostsException {
		//number and PublicKey enough to find a post in PersonalBoards
		if (number < 0) throw new InvalidNumberException("Invalid number");
		
		if (!personalBoards.containsKey(publicKey)) throw new ReferredUserException("Referred user doesn’t exist");
		
		ArrayList<Announcement> board = personalBoards.get(publicKey); //get the personal board
		
		if (board.isEmpty()) throw new EmptyBoardException("The board has no posts");
		
		if (number > board.size()) throw new NumberPostsException("The board doesn't have that many posts");
	
		String res = "";      //save the posts you want to see
		int end = board.size() - 1;
		
		Long limit = (number == 0) ? board.size() : number;
		
		for(int i = 0; i < limit; i++) {
			res += board.get(end - i).toString();
		}
		
		return res;
	}
	
	/* Read General */
	public String readGeneral(Long number) throws InvalidNumberException, EmptyBoardException, NumberPostsException {
		//number and PublicKey enough to find a post in GeneralBoard
		if (number < 0) throw new InvalidNumberException("Invalid number");
		
		if (generalBoard.isEmpty()) throw new EmptyBoardException("The board has no posts");
		
		if (number > generalBoard.size()) throw new NumberPostsException("The board doesn't have that many posts");

		String res = "";      //save the posts you want to see
		int end = generalBoard.size() - 1;
		
		Long limit = (number == 0) ? generalBoard.size() : number;
		
		for(int i = 0; i < limit; i++) {
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
