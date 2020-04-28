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

import javax.management.RuntimeErrorException;

import org.announcementserver.utils.*;
import org.announcementserver.common.Constants;
import org.announcementserver.common.CryptoTools;
import org.announcementserver.exceptions.EmptyBoardException;
import org.announcementserver.exceptions.InvalidNumberException;
import org.announcementserver.exceptions.MessageSizeException;
import org.announcementserver.exceptions.NumberPostsException;
import org.announcementserver.exceptions.PostTypeException;
import org.announcementserver.exceptions.ReferredAnnouncementException;
import org.announcementserver.exceptions.ReferredUserException;
import org.announcementserver.exceptions.UserNotRegisteredException;

public class AnnouncementServer implements Serializable {
	
	private static final long serialVersionUID = 8208757326477388685L;
	private ArrayList<Announcement> generalBoard;
	private HashMap<String, ArrayList<Announcement>> personalBoards;
	private static AnnouncementServer instance = null; //Singleton, maybe unnecessary
	private List<String> clients;
	
	public HashMap<String, Integer> sns; // sequence numbers
	
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
		this.sns = new HashMap<>();
		this.clients = new ArrayList<>();
		
		try {
			Scanner reader = new Scanner(new File("src/main/resources/clients.txt"));
			while (reader.hasNextLine()) {
				String data = reader.nextLine();
				clients.add(data);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Register */
	public Integer register(String client) {
		if (!clients.contains(client)) 
			throw new RuntimeException("Unknown user registering");

		if (!personalBoards.containsKey(client)) {
			personalBoards.put(client, new ArrayList<>());
			sns.put(client, 0);
			PersistenceUtils.serialize(instance);
		}
		
		return sns.get(client);
	}
	
	/* Post */
	public Integer post(Announcement announcement) {
		if (!personalBoards.containsKey(announcement.author)) 
			throw new RuntimeException("The user who wants to post doesn't exist");
			
		if (announcement.content.length() > 255)
			throw new RuntimeException("The message is too long");

		for (String reference : announcement.references) {			
			String[] parts = reference.split("a|c"); // [<p|g>, author_id, ctr_id]
			String owner = String.format("client%s", parts[1]);
			
			if (!personalBoards.containsKey(owner)) {
				throw new RuntimeException("Referred user doesn't exist");
			}
			
			if (parts[0].equals("p")) {								
				if (personalBoards.get(owner).size() < Integer.parseInt(parts[2])) { // FIXME size is not best comparison (wts instead?)
					throw new RuntimeException("The referred announcement doesn’t exist");
				}
				
			} else if (parts[0].equals("g")) {
				if (generalBoard.size() < Integer.parseInt(parts[2])) { // FIXME size is not best comparison (wts instead?)
					throw new RuntimeException("The referred announcement doesn’t exist");
				}
			} else {
				throw new RuntimeException("The type of post in reference is incorrect");
			}
		}

		if (sns.get(announcement.author) == announcement.seqNumber) {
			putPersonal(announcement.author, announcement);
			sns.put(announcement.author, announcement.seqNumber + 1);
			PersistenceUtils.serialize(this);
		}

		return announcement.seqNumber;
	}
	
	/* Post General */
	public Integer postGeneral(Announcement announcement) {
		if (!personalBoards.containsKey(announcement.author)) 
			throw new RuntimeException("The user who wants to post doesn't exist");
		
		if (announcement.content.length() > 255)
			throw new RuntimeException("The message is too long");
		
		for (String reference : announcement.references) {
			String[] parts = reference.split("a|c"); // [<p|g>, author_id, ctr_id]
			String owner = String.format("client%s", parts[1]);
			
			if (!personalBoards.containsKey(owner)) {
				throw new RuntimeException("Referred user doesn't exist");
			}
			
			if (parts[0].equals("p")) {								
				if (personalBoards.get(owner).size() < Integer.parseInt(parts[2])) { // FIXME size is not best comparison (wts instead?)
					throw new RuntimeException("The referred announcement doesn’t exist");
				}
				
			} else if (parts[0].equals("g")) {
				if (generalBoard.size() < Integer.parseInt(parts[2])) { // FIXME size is not best comparison (wts instead?)
					throw new RuntimeException("The referred announcement doesn’t exist");
				}
			} else {
				throw new RuntimeException("The type of post in reference is incorrect");
			}
		}
		
		if (sns.get(announcement.author) == announcement.seqNumber) {
			putGeneral(announcement);
			sns.put(announcement.author, announcement.seqNumber + 1);
			PersistenceUtils.serialize(this);
		}
		
		return announcement.seqNumber;
	}
	
	/* Read */
	public List<Announcement> read(String owner, Integer number, Integer sn) {
		if (!personalBoards.containsKey(owner))
			throw new RuntimeException("Referred user doesn't exist");

		List<Announcement> board = personalBoards.get(owner);
		if (board.isEmpty()) throw new RuntimeException("Empty Board");

		if (number > board.size()) throw new RuntimeException("Not Enough Messages");

		List<Announcement> res = new ArrayList<>();
	}

	
	/* Read General */
	public List<Announcement> readGeneral(Integer number, Integer sn) {
		if (!personalBoards.containsKey(owner))
			throw new RuntimeException("Referred user doesn't exist");

		List<Announcement> board = personalBoards.get(owner);
		if (board.isEmpty()) throw new RuntimeException("Empty Board");

		if (number > board.size()) throw new RuntimeException("Not Enough Messages");

		List<Announcement> res = new ArrayList<>();
	}
	
	/* For testing purposes */
	public void putGeneral(Announcement ann) {
		generalBoard.add(ann);
	}
	
	public void putPersonal(String author, Announcement ann) {
		personalBoards.get(author).add(ann);
	}
	
	public void clean() {
		personalBoards.clear();
		generalBoard.clear();
		sns.clear();
	}
		
}
