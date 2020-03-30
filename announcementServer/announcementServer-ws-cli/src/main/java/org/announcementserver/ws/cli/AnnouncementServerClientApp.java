package org.announcementserver.ws.cli;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import org.announcementserver.utils.CryptoTools;
import org.announcementserver.utils.Menus;
import org.announcementserver.ws.*;

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
	private static AnnouncementServerClient client = null;
	
    public static void main(String[] args ) throws AnnouncementServerClientException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    	
    	// Check arguments.
    	if (args.length == 0) {
    		System.err.println("Argument(s) missing!");
    		System.err.println("Usage: java " + AnnouncementServerClientApp.class.getName() + " wsURL OR uddiURL wsName");
    		return;
    	}
    	
    	String wsURL = args[0];

    	System.out.printf("Creating client for server at %s%n", wsURL);
        client = new AnnouncementServerClient(wsURL);
    	
        // Start of Interaction
    	authenticationMenu();
    }
    
    private static void authenticationMenu() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    	
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
    	mainMenu();
    }
    
    /* Main Menu */
    private static void mainMenu() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    	
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
    public static void registerMenu() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    	
    	/* Get PublicKey */
    	String publicKey = CryptoTools.pubKeyAsString("src/main/resources/"+username+"pub.der");
    	
    	try {
    		printSuccess(client.register(publicKey));
    	} catch (UserAlreadyRegisteredFault_Exception e) {
    		printError(e.getMessage());
    	}
    	
    	mainMenu();
    }
    
    /* Post Menu */
    public static void postMenu() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    	
    	menu.displayPostMenu();
    	
    	/* Get PublicKey */
    	String publicKey = CryptoTools.pubKeyAsString("src/main/resources/"+username+"pub.der");
    	
    	/* Get Message */
    	System.out.print("Message to send: ");
    	String message = userStringInput();
    	
    	/* Get AnnouncementList */
    	List<String> announcementList = new ArrayList<String>();
    	boolean ok = true;
    	while (!ok) {
    		System.out.print("Do you want to make references? (Use 'y' for yes and 'n' for no): ");
        	String ans = userStringInput();
        	if (ans.equals("y") || ans.equals("n")) {
        		ok = false;
        	} else {
        		printError("Error: Either use 'y' or 'n'");
        	}
    	}
    	
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
    		printSuccess(client.post(publicKey, message, announcementList));
    	} catch ( MessageSizeFault_Exception | PostTypeFault_Exception | ReferredAnnouncementFault_Exception
    			| ReferredUserFault_Exception | UserNotRegisteredFault_Exception e) {
    		printError(e.getMessage() + "\nTry again");
    	} 
    	
    	mainMenu();
    }
    
    /* Post General Menu */
    public static void postGeneralMenu() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    	
    	menu.displayPostGeneralMenu();
    	
    	/* Get PublicKey */
    	String publicKey = CryptoTools.getPublicKeyAsString("src/main/resources/"+username+"pub.der");
    	
    	/* Get Message */
    	System.out.print("Message to send: ");
    	String message = userStringInput();
    	
    	/* Get AnnouncementList */
    	List<String> announcementList = new ArrayList<String>();
    	boolean ok = true;
    	while (!ok) {
    		System.out.print("Do you want to make references? (Use 'y' for yes and 'n' for no): ");
        	String ans = userStringInput();
        	if (ans.equals("y") || ans.equals("n")) {
        		ok = false;
        	} else {
        		printError("Error: Either use 'y' or 'n'");
        	}
    	}
    	
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
			printSuccess(client.postGeneral(publicKey, message, announcementList));
		} catch (MessageSizeFault_Exception | PostTypeFault_Exception | ReferredAnnouncementFault_Exception
				| ReferredUserFault_Exception | UserNotRegisteredFault_Exception e) {
			printError(e.getMessage() + "\nPlease repeat!");
		}
    	
    	mainMenu();
    }
    
    /* Read Menu */
    public static void readMenu() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    	menu.displayReadMenu();
    	
    	System.out.print("Client whose posts you want to see: ");
    	String clientID = userStringInput();
    	
    	/* Get PublicKey */
    	String publicKey = CryptoTools.getPublicKeyAsString("src/main/resources/"+clientID+"pub.der");
    	
    	System.out.print("Number of posts to read (use 0 for all): ");
    	int number = userIntInput();
    	
		try {
			printSuccess(client.read(publicKey, Long.valueOf(number)));
		} catch (EmptyBoardFault_Exception | InvalidNumberFault_Exception | NumberPostsFault_Exception
				| ReferredUserFault_Exception e) {
			printError(e.getMessage());
		}
    	
    	mainMenu();
    }
    
    /* Read General Menu */
    public static void readGeneralMenu() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    	menu.displayReadGeneralMenu();
    	
    	System.out.print("Number of posts to read (use 0 for all): ");
    	int number = userIntInput();
    	
		try {
			printSuccess(client.readGeneral(Long.valueOf(number)));
		} catch (EmptyBoardFault_Exception | InvalidNumberFault_Exception | NumberPostsFault_Exception e) {
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
