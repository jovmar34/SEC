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
	//private HashMap<String, String> pks; // client id => public key association
	//private HashMap<String, Integer> clients; // public key => client id association (OVERKILL?) FIXME
	private ArrayList<Announcement> generalBoard;
	private HashMap<String, ArrayList<Announcement>> personalBoards;
	private static AnnouncementServer instance = null; //Singleton, maybe unnecessary
	
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
		//this.clients = new HashMap<>();
		//this.pks = new HashMap<>();
		this.sns = new HashMap<>();
		
		/*
		try {
			Scanner reader = new Scanner(new File("src/main/resources/clients.txt"));
			while (reader.hasNextLine()) {
				String[] data = reader.nextLine().split(" ");
				String clientID = data[0];
				String publicKey = data[1];
				clients.put(publicKey, clientID );
				pks.put(clientID, publicKey);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	/* Register */
	public Integer register(String client) {
		/*
		if (!clients.containsKey(publicKey)) {
			throw new RuntimeException("Untrusted user registering");
		}
		*/
		if (!personalBoards.containsKey(client)) {
			personalBoards.put(client, new ArrayList<>());
			sns.put(client, 0);
			PersistenceUtils.serialize(instance);
		}
		
		return sns.get(client);
		/*if (new_user) {
			
			toHash.add(myId);
			toHash.add(clientID);
			toHash.add("Welcome new user!");
			
			try {
				response.add("Welcome new user!");
				response.add(CryptoTools.makeSignature(toHash.toArray(new String[0])));
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		} else {
			toHash.add(myId);
			toHash.add(clientID);
			toHash.add(String.format("Welcome back %s", clientID));
			toHash.add(sns.get(clientN).toString());

			try {
				response.add(String.format("Welcome back %s", clientID));
				response.add(sns.get(clientN).toString());
				response.add(CryptoTools.makeSignature(toHash.toArray(new String[0])));
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		
		
		return response;*/
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
		sns.clear();
	}
		
}
