package org.announcementserver.ws.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import org.announcementserver.common.CryptoTools;
import org.announcementserver.utils.*;
import org.announcementserver.ws.*;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import com.google.common.hash.Hashing;

/**
 * Client Side Application
 *
 */
public class AnnouncementServerClientApp {
	
	public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // Text in green
	public static final String RED_BOLD_BRIGHT = "\033[1;91m"; // Text in red
	public static final String RESET = "\033[0m"; // Text reset
	public static final String client1sha = "9bd915291749076d56d4198b4ea35003249be5c88acebce51fcf559d52bde24e";
	public static final String client2sha = "4416f05dcc94e63edddd1e7459caefc6eb3137932ea64d446a08b2301aaefac6";
	public static final String client3sha = "27d728e7c5ed0f593fce0b49518a9d470826cac65778c5b5d2e14e2302db7636";
	public static String username = "";
	
	private static Menus menu = new Menus();
	private static FrontEnd client = null;
	private static Integer sn = 0;
	
    public static void main(String[] args ) throws AnnouncementServerClientException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, UnrecoverableEntryException, KeyStoreException, CertificateException {
    	
    	// Check arguments.
    	if (args.length == 0) {
    		System.err.println("Argument(s) missing!");
    		System.err.println("Usage: java " + AnnouncementServerClientApp.class.getName() + " wsURL OR uddiURL wsName");
    		return;
    	}
    	
    	String wsURL = args[0];
    	
    	System.out.printf("Creating client for server at %s%n", wsURL);
        client = new FrontEnd(wsURL);
    	
        // Start of Interaction
    	authenticationMenu();
    }
    
    private static void authenticationMenu() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, UnrecoverableEntryException, KeyStoreException, CertificateException {
    	
    	/* Authentication */
    	System.out.print("Provide your username: ");
    	username = userStringInput();
    	System.out.print("Password: ");
    	String pass = userStringInput();
    	
    	if (username.equals("client1")) {
    		
    		String hash = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();
    		if (!hash.equals(client1sha)) {
    			System.out.println(RED_BOLD_BRIGHT);
    			System.err.println("Wrong password! Try again.");
    			System.out.println(RESET);
    			authenticationMenu();
    		}
    	}
    	else if (username.equals("client2")) {
    		
    		String hash = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();
    		if (!hash.equals(client2sha)) {
    			System.out.println(RED_BOLD_BRIGHT);
    			System.err.println("Wrong password! Try again.");
    			System.out.println(RESET);
    			authenticationMenu();
    		}
    	}
    	else if (username.equals("client3")) {
    		
    		String hash = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();
    		if (!hash.equals(client3sha)) {
    			System.out.println(RED_BOLD_BRIGHT);
    			System.err.println("Wrong password! Try again.");
    			System.out.println(RESET);
    			authenticationMenu();
    		}
    	} else {
    		System.out.println(RED_BOLD_BRIGHT);
    		System.err.println("Wrong username!");
    		System.out.println(RESET);
    		authenticationMenu();
    	}
    	
    	System.out.println(GREEN_BOLD_BRIGHT);
    	System.out.println("Sucessfull authentication! Welcome!");
		System.out.println(RESET);
		
		client.init(username);
    	    	
    	mainMenu();
    }
    
    /* Main Menu */
    private static void mainMenu() {
    	
    	final int NCHOICES = 6;
    	int menuItem = -1;
    	
    	menu.displayMainMenu();
    	System.out.print("Choose menu item: ");
    	
    	try {
    		menuItem = userIntInput();
    	} catch (Exception e) {
    		printError("Must be a number!");
    		mainMenu();
    	}
    	
    	switch (menuItem) {
    	case 1:
    		// Register Menu
    		registerMenu();
    		System.exit(0);
    	case 2:
    		// Post Menu
    		postMenu();
    		System.exit(0);
    	case 3:
    		// Post General Menu
    		postGeneralMenu();
    		System.exit(0);
    	case 4:
    		// Read Menu
    		readMenu();
    		System.exit(0);
    	case 5:
    		// Read General Menu
    		readGeneralMenu();
    		System.exit(0);
    	case 6:
    		// Exit
    		menu.displayExitMenu();
    		System.exit(0);
    	default:
    		printError("Invalid choice.\nMust be a number between 1 and " + NCHOICES);
    		mainMenu();
    	}
    }
    
    /* Register Menu */
    public static void registerMenu() {
    	
    	try {
			printSuccess(client.register());
		} catch (Exception e) {
			printError(e.getMessage());
		}
    	
    	mainMenu();
    }
    
    /* Post Menu */
    public static void postMenu() {
    	
    	menu.displayPostMenu();
    	
    	/* Get Message */
    	System.out.print("Message to send: ");
    	String message = userStringInput();
    	
    	/* Get AnnouncementList */
    	List<String> announcementList = new ArrayList<String>();
    	    	
    	System.out.print("How many references would you like to make? (Use 0 for none): ");
    	int nrefs = userIntInput();
		
		/* Collectiong announcement ids */ 
    	for (; nrefs>0; nrefs--) {
    		System.out.print("Board Type (Use 'p' for personal and 'g' for general): ");
    		String boardType = userStringInput();
    		System.out.print("UserId: ");
    		String userId = userStringInput();
    		System.out.print("AnnouncementId: ");
    		String announcementId = userStringInput();
    		
    		String reference = String.format("%sc%sa%s", boardType, userId, announcementId);
    		announcementList.add(reference);
		}
		
		try {
			printSuccess(client.post(message, announcementList));
		} catch (Exception e) {
			printError(e.getMessage());
		}
    	
    	mainMenu();
    }
    
    /* Post General Menu */
    public static void postGeneralMenu() {
    	
    	menu.displayPostGeneralMenu();
    	
    	/* Get Message */
    	System.out.print("Message to send: ");
    	String message = userStringInput();
    	
    	/* Get AnnouncementList */
    	List<String> announcementList = new ArrayList<String>();
    	
    	System.out.print("How many references would you like to make? (Use 0 for none): ");
    	int nrefs = userIntInput();
    	
    	for (; nrefs>0; nrefs--) {
    		System.out.print("Board Type (Use 'p' for personal and 'g' for general): ");
    		String boardType = userStringInput();
    		System.out.print("UserId: ");
    		String userId = userStringInput();
    		System.out.print("AnnouncementId: ");
    		String announcementId = userStringInput();
    		
    		String reference = String.format("%sc%sa%s", boardType, userId, announcementId);
    		announcementList.add(reference);
    	}
    	
		try {
			printSuccess(client.postGeneral(message, announcementList));
		} catch (Exception e) {
			printError(e.getMessage());
		}

    	mainMenu();
    }
    
    /* Read Menu */
    public static void readMenu() {
    	menu.displayReadMenu();
    	
    	System.out.print("Client whose posts you want to see: ");
    	String clientID = userStringInput();
    	
    	System.out.print("Number of posts to read (use 0 for all): ");
    	int number = userIntInput();
    	/* Get PublicKey */
		
		try {
			printSuccess(client.read(clientID, number));
		} catch (Exception e) {
			printError(e.getMessage());
		}
    	
    	mainMenu();
    }
    
    /* Read General Menu */
    public static void readGeneralMenu() {
    	menu.displayReadGeneralMenu();
    	
    	System.out.print("Number of posts to read (use 0 for all): ");
    	int number = userIntInput();
    	
    	try {
			printSuccess(client.readGeneral(number));
		} catch (Exception e) {
			printError(e.getMessage());
		}
    	    	
    	mainMenu();
    }
    
    // --- Checkings -------------------------------------

	@SuppressWarnings("resource")
	private static int userIntInput() {
		Scanner in = new Scanner(System.in);
		int i = in.nextInt();
		return i;
	}

	@SuppressWarnings("resource")
	private static String userStringInput() {
		Scanner in = new Scanner(System.in);
		String i = in.nextLine();
		return i;
	}
	
	// Auxiliary functions --------------------------------
	
	private static void printSuccess(String message) {
		System.out.println(GREEN_BOLD_BRIGHT);
		System.out.println(message);
		System.out.println(RESET);
	}
	
	private static void printError(String message) {
		System.out.println(RED_BOLD_BRIGHT);
		System.out.println(message);
		System.out.println(RESET);
	}
	
	private static void updateSn() {
		try {
			FileWriter writer = new FileWriter(new File(String.format("src/main/resources/%s.sn", username)));
			writer.write(String.valueOf(sn));
			writer.close();
		} catch (IOException e) {
			System.out.println("Could not persist sequence number");
		}
	}
}


