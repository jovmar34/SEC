package org.announcementserver.ws.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Properties;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.announcementserver.utils.*;
import org.announcementserver.ws.*;

import javax.xml.ws.Service;

/**
 * Client Side Application
 *
 */
public class AnnouncementServerClientApp {
	
	private static final String KEYSTORE_FILE_PATH = "src/main/resources/";
	private static final String PASSWORD_FILENAME = "announcement.properties";
	
	public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // Text in green
	public static final String RED_BOLD_BRIGHT = "\033[1;91m"; // Text in red
	public static final String RESET = "\033[0m"; // Text reset
	public static String username = "";
	
	private static Menus menu = new Menus();
	private static FrontEnd client = null;
	
    public static void main(String[] args ) throws AnnouncementServerClientException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, UnrecoverableEntryException, KeyStoreException, CertificateException {
    	
    	// Check arguments.
    	if (args.length != 2) {
    		System.err.println("Argument(s) missing!");
    		System.err.println("Usage: java " + AnnouncementServerClientApp.class.getName() + " wsURL OR uddiURL wsName");
    		return;
    	}
		
		String host = args[0];
		String faults = args[1];
    	
		client = new FrontEnd(host, faults);
		
		// SIMPLY AN ECHO TEST
		WriteMessage test = new WriteMessage();
		test.setSender("Me");
		test.setDestination("You");

		test.setSignature("signature");

		client.ports.get(0).echo(Messages.toByteArray(test));
    	
        // Start of Interaction
    	//authenticationMenu();
    }
    
    private static void authenticationMenu() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, UnrecoverableEntryException, KeyStoreException, CertificateException {
    	
    	/* Authentication */
    	System.out.print("Provide your username: ");
    	username = userStringInput();
    	System.out.print("Password: ");
    	String pass = userStringInput();
    	
    	Properties passwordProps = new Properties();
    	File passwordResource = new File(KEYSTORE_FILE_PATH + PASSWORD_FILENAME);
    	InputStream passwordIS = new FileInputStream(passwordResource);
    	passwordProps.load(passwordIS);

    	if (username.equals("client1") || username.equals("client2") || username.equals("client3")) {
    		if (!pass.equals(passwordProps.getProperty(username + "-password"))) {
    			printError("Wrong password! Try again.");
    			authenticationMenu();
    		}
    	} else {
    		printError("Wrong username!");
    		authenticationMenu();
    	}
    	
		client.init(username);
		
    	try {
			printSuccess(client.register());
		} catch (Exception e) {
			printError(e.getMessage());
		}
    	    	
    	mainMenu();
    }
    
    /* Main Menu */
    private static void mainMenu() {
    	
    	final int NCHOICES = 5;
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
    		// Post Menu
    		postMenu();
    		System.exit(0);
    	case 2:
    		// Post General Menu
    		postGeneralMenu();
    		System.exit(0);
    	case 3:
    		// Read Menu
    		readMenu();
    		System.exit(0);
    	case 4:
    		// Read General Menu
    		readGeneralMenu();
    		System.exit(0);
    	case 5:
    		// Exit
    		menu.displayExitMenu();
    		System.exit(0);
    	default:
    		printError("Invalid choice.\nMust be a number between 1 and " + NCHOICES);
    		mainMenu();
    	}
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
		
		/* Collecting announcement IDs */ 
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
			e.printStackTrace();
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
			e.printStackTrace();
			printError(e.getMessage());
		}
    	    	
    	mainMenu();
    }
    
    // --- Input Scanners -------------------------------------

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
	
	// --- Auxiliary functions --------------------------------
	
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
	
}