package org.announcementserver.ws;

import org.announcementserver.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class AnnouncementServer implements Serializable {
	
	private static final long serialVersionUID = 8208757326477388685L;
	private ArrayList<Announcement> generalBoard;
	private HashMap<String, ArrayList<Announcement>> personalBoards;
	private static AnnouncementServer instance = null; //Singleton
	private List<String> clients;
	private String id;
	
	public HashMap<String, Integer> sns; // sequence numbers
	public HashMap<String, Integer> wtss; // clients wts's; personal boards
	public HashMap<String, Integer> expectedWts; // (N,N) register, expected wts from a specific client
	
	public static AnnouncementServer getInstance() {
		if (instance == null) {
			instance = new AnnouncementServer();
		}
		return instance;
	}
	
	public static void setInstance(AnnouncementServer announcementServer) {
		instance = announcementServer;
	}
	
	public void setId(String serverId) {
		this.id = serverId;
	}
	
	private AnnouncementServer () {
		this.generalBoard = new ArrayList<>();
		this.personalBoards = new HashMap<>();
		this.sns = new HashMap<>();
		this.wtss = new HashMap<>();
		this.clients = new ArrayList<>();
		this.expectedWts = new HashMap<>();
		
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
	public synchronized List<Integer> register(String client) {
		if (!clients.contains(client)) 
			throw new RuntimeException("Unknown user registering");

		if (!personalBoards.containsKey(client)) {
			personalBoards.put(client, new ArrayList<>());
			sns.put(client, 0);
			wtss.put(client,0);
			expectedWts.put(client, 0);
			PersistenceUtils.serialize(instance, id);
		}

		List<Integer> ret = new ArrayList<>();
		ret.add(sns.get(client));
		ret.add(wtss.get(client));
		
		return ret;
	}
	
	/* Post */
	public synchronized List<Integer> post(Announcement announcement, Integer seqNumber) {
		if (sns.get(announcement.author) != seqNumber) 
			throw new RuntimeException("Sequence numbers don't match");

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
				if (personalBoards.get(owner).size() < Integer.parseInt(parts[2])) {
					throw new RuntimeException("The referred announcement doesn’t exist");
				}
				
			} else if (parts[0].equals("g")) {
				if (generalBoard.size() < Integer.parseInt(parts[2])) {
					throw new RuntimeException("The referred announcement doesn’t exist");
				}
			} else {
				throw new RuntimeException("The type of post in reference is incorrect");
			}
		}

		if (wtss.get(announcement.author) < announcement.id) {
			putPersonal(announcement.author, announcement);
			sns.put(announcement.author, seqNumber + 1);
			wtss.put(announcement.author, announcement.id);
			PersistenceUtils.serialize(instance, id);
		}

		List<Integer> ret = new ArrayList<>();
		ret.add(seqNumber);
		ret.add(announcement.id);

		return ret;
	}
	
	/* Post General */
	public synchronized Integer postGeneral(Announcement announcement, Integer seqNumber) {
		if (sns.get(announcement.author) != seqNumber)
			throw new RuntimeException("Non matching sequence numbers");

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
				if (personalBoards.get(owner).size() < Integer.parseInt(parts[2])) {
					throw new RuntimeException("The referred announcement doesn’t exist");
				}
				
			} else if (parts[0].equals("g")) {
				if (generalBoard.size() < Integer.parseInt(parts[2])) {
					throw new RuntimeException("The referred announcement doesn’t exist");
				}
			} else {
				throw new RuntimeException("The type of post in reference is incorrect");
			}
		}
		
		if (announcement.id >= expectedWts.get(announcement.author)) {
			putGeneral(announcement);
			sns.put(announcement.author, seqNumber + 1);
			PersistenceUtils.serialize(instance, id);
		}
		
		return seqNumber;
	}
	
	/* Read */
	public synchronized List<Announcement> read(String reader, String owner, Integer number, Integer sn) {
		if (!personalBoards.containsKey(owner))
			return new ArrayList<>();

		List<Announcement> board = personalBoards.get(owner);

		if (sn != sns.get(reader)) throw new RuntimeException("Sequence numbers not in synch");

		sns.put(reader, sn + 1);
		PersistenceUtils.serialize(instance, id);

		return board.subList(0, board.size());
	}

	
	/* Read General */
	public synchronized List<Announcement> readGeneral(String reader, Integer number, Integer sn) {		
		if (sn != sns.get(reader)) 
			throw new RuntimeException("Sequence numbers not in synch");

		Integer end = generalBoard.size();
		Integer start = 
			(number > generalBoard.size() || number == 0) ? 0 : end - number;

		Integer wts = (end == 0) ? 1 : generalBoard.get(end - 1).id + 1;

		sns.put(reader, sn + 1);
		expectedWts.put(reader, wts);
		PersistenceUtils.serialize(instance, id);
		
		return generalBoard.subList(start, end);
	}
	
	/* Write Back */
	public synchronized Integer writeBack(String sender, List<AnnouncementMessage> announcements, Integer seqNumber) {
		if (sns.get(sender) != seqNumber) {
			throw new RuntimeException("Wrong sequence number");
		}

		// Convert all to Announcements
		List<Announcement> announcementsList = new ArrayList<>();
		String writer = announcements.get(0).getWriter();
		int wts = wtss.get(writer);

		for (AnnouncementMessage am : announcements) {
			if (!writer.equals(am.writer)) 
				throw new RuntimeException("Writer in one of the writeback posts not okay");
			if (wts < am.wts) {
				wts++;
				announcementsList.add(AnnouncementTools.transformAnnouncement(am));
			}
		}

		personalBoards.get(writer).addAll(announcementsList);
		wtss.put(writer, wts);
		sns.put(sender, seqNumber + 1);
		PersistenceUtils.serialize(instance, id);
		
		return seqNumber;
	}
	
	/* For testing purposes */
	public void putGeneral(Announcement ann) {
		generalBoard.add(ann);
		generalBoard.sort(null);
	}
	
	public void putPersonal(String author, Announcement ann) {
		personalBoards.get(author).add(ann);
		personalBoards.get(author).sort(null);
	}
	
	public void clean() {
		personalBoards.clear();
		generalBoard.clear();
		sns.clear();
	}
	
}
