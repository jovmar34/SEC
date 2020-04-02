package org.announcementserver.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Scanner;
import org.announcementserver.utils.*;
import org.announcementserver.common.CryptoTools;
import org.announcementserver.common.PossibleTamperingException;
import org.announcementserver.exceptions.EmptyBoardException;
import org.announcementserver.exceptions.InvalidNumberException;
import org.announcementserver.exceptions.MessageSizeException;
import org.announcementserver.exceptions.NumberPostsException;
import org.announcementserver.exceptions.PostTypeException;
import org.announcementserver.exceptions.ReferredAnnouncementException;
import org.announcementserver.exceptions.ReferredUserException;
import org.announcementserver.exceptions.UserNotRegisteredException;
import org.announcementserver.exceptions.UserAlreadyRegisteredException;

public class AnnouncementServer implements Serializable {
	private HashMap<Integer, String> pks; // client id => public key association
	private HashMap<String, Integer> clients; // public key => client id association (OVERKILL?) FIXME
	private ArrayList<Announcement> generalBoard;
	private HashMap<String, ArrayList<Announcement>> personalBoards;
	private static AnnouncementServer instance = null; //Singleton, maybe unnecessary
	
	private HashMap<Integer, Integer> sns; // sequence numbers
	
	public static AnnouncementServer getInstance() {
		if (instance == null) {
			instance = new AnnouncementServer();
		}
		return instance;
	}
	
	public static void setInstance(AnnouncementServer announcementServer) {
		instance = announcementServer;
	}
	
	private AnnouncementServer () {
		this.generalBoard = new ArrayList<>();
		this.personalBoards = new HashMap<>();
		this.clients = new HashMap<>();
		this.pks = new HashMap<>();
		this.sns = new HashMap<>();
		
		try {
			Scanner reader = new Scanner(new File("src/main/resources/clients.txt"));
			while (reader.hasNextLine()) {
				String[] data = reader.nextLine().split(" ");
				int clientID = Integer.parseInt(data[0]);
				String publicKey = data[1];
				clients.put(publicKey, clientID );
				pks.put(clientID, publicKey);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Register */
	public List<String> register(String publicKey, String signature) throws UserAlreadyRegisteredException {
		if (!clients.containsKey(publicKey)) {
			throw new RuntimeException("Untrusted user registering");
		}
		
		List<String> response = new ArrayList<>();
		String hash = null;
		String clientID = String.format("client%d", clients.get(publicKey));

		try {
			hash = CryptoTools.decryptSignature(clientID, signature);
		} catch (Exception e) {
			throw new RuntimeException("Error: Possible tampering detected on Signature");
		}
		
		try {
			if (!CryptoTools.checkHash(clientID, "server", publicKey, hash)) { 
				throw new RuntimeException("Error: Possible tampering detected on Hash");
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		
		if (!personalBoards.containsKey(publicKey)) {
			personalBoards.put(publicKey, new ArrayList<>());
			
			sns.put(clients.get(publicKey), 0);
			PersistenceUtils.serialize(instance);
			
			List<String> toHash = new ArrayList<>();
			toHash.add("server");
			toHash.add(clientID);
			toHash.add("Welcome new user!");
			
			try {
				response.add("Welcome new user!");
				response.add(CryptoTools.makeSignature(toHash.toArray(new String[0])));
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		} else {
			throw new UserAlreadyRegisteredException("User is already registered");
		}
		
		
		return response;
	}
	
	/* Post */
	public List<String> post(String publicKey, String message, List<String> announcementList, String signature) 
			throws UserNotRegisteredException, MessageSizeException, ReferredUserException, PostTypeException, ReferredAnnouncementException {
		
		List<String> response = new ArrayList<>();
		
		/* Verify existence of publicKey */
		if (!personalBoards.containsKey(publicKey))  {
			throw new UserNotRegisteredException("User is not registered yet");
			
		}
		
		Integer sn = sns.get(clients.get(publicKey));
		
		String clientID = String.format("client%d", clients.get(publicKey));
		String hash = null;
		
		try {
			hash = CryptoTools.decryptSignature(clientID, signature);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		List<String> inHash = new ArrayList<>();
		inHash.add(clientID);
		inHash.add("server");
		inHash.add(String.valueOf(sn));
		inHash.add(publicKey);
		inHash.add(message);
		inHash.addAll(announcementList);
		inHash.add(hash);
		
		try{
			if (!CryptoTools.checkHash(inHash.toArray(new String[0]))) { 
				throw new RuntimeException("Error: Possible tampering detected on Hash");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
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
				throw new ReferredUserException("Referred user doesn't exist");
			}
			
			if (parts[0].equals("p")) {								
				if (personalBoards.get(pk).size() < Integer.parseInt(parts[2])) {
					throw new ReferredAnnouncementException("The referred announcement doesn’t exist");
				}
				
			} else if (parts[0].equals("g")) {
				if (generalBoard.size() < Integer.parseInt(parts[2])) {
					throw new ReferredAnnouncementException("The referred announcement doesn’t exist");
				}
			} else {
				throw new PostTypeException("The type of post in reference is incorrect");
			}
			
			
			post.addReference(reference);
		}
		
		post.setId(String.format("pc%da%d", clients.get(publicKey), board.size()));
		post.setAuthor(String.format("client%d", clients.get(publicKey)));
		
		board.add(post);
		
		sns.put(clients.get(publicKey), sn + 1);
		PersistenceUtils.serialize(instance);
		
		List<String> outHash = new ArrayList<>();
		outHash.add("server");
		outHash.add(clientID);
		outHash.add(String.valueOf(sn));
		outHash.add("Success your post was posted!");
		
		try {
			response.add(outHash.get(3));
			response.add(CryptoTools.makeSignature(outHash.toArray(new String[0])));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		return response;
	}
	
	/* Post General */
	public List<String> postGeneral(String publicKey, String message, List<String> announcementList, String signature) 
			throws UserNotRegisteredException, MessageSizeException, ReferredUserException, ReferredAnnouncementException, PostTypeException {
		
		List<String> response = new ArrayList<>();
		Integer sn = sns.get(clients.get(publicKey));
		
		/* Verify existence of publicKey */
		if (!personalBoards.containsKey(publicKey))  {
			throw new UserNotRegisteredException("User is not registered yet");
			
		}
		
		String clientID = String.format("client%d", clients.get(publicKey));
		String hash = null;
		
		try {
			hash = CryptoTools.decryptSignature(clientID, signature);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		List<String> inHash = new ArrayList<>();
		inHash.add(clientID);
		inHash.add("server");
		inHash.add(String.valueOf(sn));
		inHash.add(publicKey);
		inHash.add(message);
		inHash.addAll(announcementList);
		inHash.add(hash);
		
		try{
			if (!CryptoTools.checkHash(inHash.toArray(new String[0]))) { 
				throw new RuntimeException("Error: Possible tampering detected on Hash");
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				throw new ReferredUserException("Referred user doesn't exist");
			}
			
			if (parts[0].equals("p")) {								
				if (personalBoards.get(pk).size() < Integer.parseInt(parts[2])) {
					throw new ReferredAnnouncementException("The referred announcement doesn’t exist");
				}
				
			} else if (parts[0].equals("g")) {
				if (generalBoard.size() < Integer.parseInt(parts[2])) {
					throw new ReferredAnnouncementException("The referred announcement doesn’t exist");
				}
			} else {
				throw new PostTypeException("The type of post in reference is incorrect");
			}
			
			post.addReference(reference);
		}
		
		post.setId(String.format("gc%da%d", clients.get(publicKey), generalBoard.size()));
		post.setAuthor(String.format("client%d", clients.get(publicKey)));
		
		generalBoard.add(post);
		
		PersistenceUtils.serialize(instance);
		
		List<String> outHash = new ArrayList<>();
		outHash.add("server");
		outHash.add(clientID);
		outHash.add(String.valueOf(sn));
		outHash.add("Success your post was posted!");
		
		try {
			response.add(outHash.get(3));
			response.add(CryptoTools.makeSignature(outHash.toArray(new String[0])));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		return response;
	}
	
	/* Read */
	public List<String> read(String publicKey, Long number, String signature) 
			throws InvalidNumberException, ReferredUserException, EmptyBoardException, NumberPostsException {
		//number and PublicKey enough to find a post in PersonalBoards
		List<String> response = new ArrayList<>();
		
		try{
			if (!CryptoTools.checkHash(publicKey, String.valueOf(number), signature)) { 
				throw new RuntimeException("Error: Possible tampering from differing hashes");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		if (number < 0) throw new InvalidNumberException("Invalid number");
		
		if (!personalBoards.containsKey(publicKey)) throw new ReferredUserException("Referred user doesn't exist");
		
		ArrayList<Announcement> board = personalBoards.get(publicKey); //get the personal board
		
		if (board.isEmpty()) throw new EmptyBoardException("The board has no posts");
		
		if (number > board.size()) throw new NumberPostsException("The board doesn't have that many posts");
	
		String res = "";      //save the posts you want to see
		int end = board.size() - 1;
		
		Long limit = (number == 0) ? board.size() : number;
		
		for(int i = 0; i < limit; i++) {
			res += board.get(end - i).toString();
		}
		
		try {
			response.add(res);
			response.add(CryptoTools.makeHash(res));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		return response;
	}
	
	/* Read General */
	public List<String> readGeneral(Long number, String signature) 
			throws InvalidNumberException, EmptyBoardException, NumberPostsException {
		//number and PublicKey enough to find a post in GeneralBoard
		List<String> response = new ArrayList<>();
		
		try{
			if (!CryptoTools.checkHash(String.valueOf(number), signature)) { 
				throw new RuntimeException("Error: Possible tampering from differing hashes");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		if (number < 0) throw new InvalidNumberException("Invalid number");
		
		if (generalBoard.isEmpty()) throw new EmptyBoardException("The board has no posts");
		
		if (number > generalBoard.size()) throw new NumberPostsException("The board doesn't have that many posts");

		String res = "";      //save the posts you want to see
		int end = generalBoard.size() - 1;
		
		Long limit = (number == 0) ? generalBoard.size() : number;
		
		for(int i = 0; i < limit; i++) {
			res += generalBoard.get(end - i).toString();
		}
		
		try {
			response.add(res);
			response.add(CryptoTools.makeHash(res));
		} catch (IOException | NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | CertificateException e) {
			e.printStackTrace();
		}		
		
		return response;
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
