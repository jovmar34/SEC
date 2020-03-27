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
import java.util.Scanner;
import org.announcementserver.utils.*;
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
	public static String username;
	
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
    	mainMenu();
    }
    
    /* Main Menu */
    private static void mainMenu() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    	
    	/* Authentication */
    	System.out.print("Provide your username: ");
    	username = userStringInput();
    	System.out.print("Password: ");
    	String pass = userStringInput();
    	
    	if (username.equals("client1")) {
    		
    		String hash = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();
    		if (!hash.equals(client1sha)) {
    			System.err.println("Wrong password! Try again.");
    			mainMenu();
    		}
    	}
    	else if (username.equals("client2")) {
    		
    		String hash = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();
    		if (!hash.equals(client2sha)) {
    			System.err.println("Wrong password! Try again.");
    			mainMenu();
    		}
    	}
    	else if (username.equals("client3")) {
    		
    		String hash = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();
    		if (!hash.equals(client3sha)) {
    			System.err.println("Wrong password! Try again.");
    			mainMenu();
    		}
    	} else {
    		System.err.println("Wrong username!");
    		mainMenu();
    	}
    	
    	System.out.println("Sucessfull authentication! Welcome!");
    	
    	final int NCHOICES = 6;
    	int menuItem = -1;
    	
    	menu.displayMainMenu();
    	System.out.print("Choose menu item: ");
    	
    	try {
    		menuItem = userIntInput();
    	} catch (Exception e) {
    		System.err.println("Must be a number!");
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
    		System.err.println("Invalid choice.\nMust be a number between 1 and " + NCHOICES);
    		mainMenu();
    	}
    }
    
    /* Register Menu */
    public static void registerMenu() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    	
    	/* Get PublicKey */
    	String publicKey = CryptoTools.getPublicKeyAsString("src/main/resources/"+username+"pub.key");
 
    	String returned = client.register(publicKey);
    	System.out.println(returned);
    	
    	mainMenu();
    }
    
    /* Post Menu */
    public static void postMenu() throws IOException {
    	
    	menu.displayPostMenu();
    	
    	/* Get PublicKey */
    	String publicKey = CryptoTools.getPublicKeyAsString("src/main/resources/"+username+"pub.key");
    	
    	/* Get Message */
    	System.out.print("Message to send: ");
    	String message = userStringInput();
    	
    	/* Get AnnouncementList */
    	List<String> announcementList = new ArrayList<String>();
    	int ok=1;
    	while (!ok) {
    		System.out.print("Do you want to make references? (Use 'y' for yes and 'n' for no): ");
        	String ans = userStringInput();
        	if (ans.equals("y") || ans.equals("n")) {
        		ok = 0;
        	} else {
        		System.err.println("Error: Either use 'y' or 'n'");
        	}
    	}
    	
    	System.out.print("How many references would you like to make? (Use 0 for none): ");
    	int nrefs = userIntInput();
    	
    	for (nrefs; nrefs>0; nrefs--) {
    		System.out.print("Board Type (Use 'p' for public and 'g' for general): ");
    		String boardType = userStringInput();
    		System.out.print("UserId: ");
    		String userId = userStringInput();
    		System.out.print("AnnouncementId: ");
    		String announcementId = userStringInput();
    		
    		String reference = "<"+boardType+">"+"<"+userId+">"+"<"+announcementId+">";
    		announcementList.add(reference);
    	}
    	
    	String returned = client.post(publicKey, message, announcementList);
    	
    	/* Verifying returned values */
    	if (!returned.equals("Success")) { // In case something failed
    		System.err.println(returned);
    		System.err.println("Please repeat!");
    		postMenu();
    	} else { // In case of success
    		System.out.println("Success, your post was posted!");
    	}
    }
    
    /* Post General Menu */
    public static void postGeneralMenu() throws IOException {
    	
    	menu.displayPostGeneralMenu();
    	
    	/* Get PublicKey */
    	String publicKey = CryptoTools.getPublicKeyAsString("src/main/resources"+username+"pub.key");
    	
    	/* Get Message */
    	System.out.print("Message to send: ");
    	String message = userStringInput();
    	
    	/* Get AnnouncementList */
    	List<String> announcementList = new ArrayList<String>();
    	int ok=1;
    	while (!ok) {
    		System.out.print("Do you want to make references? (Use 'y' for yes and 'n' for no): ");
        	String ans = userStringInput();
        	if (ans.equals("y") || ans.equals("n")) {
        		ok = 0;
        	} else {
        		System.out.println("Error: Either use 'y' or 'n'");
        	}
    	}
    	
    	System.out.print("How many references would you like to make? (Use 0 for none): ");
    	int nrefs = userIntInput();
    	
    	for (nrefs; nrefs>0; nrefs--) {
    		System.out.print("Board Type (Use 'p' for public and 'g' for general): ");
    		String boardType = userStringInput();
    		System.out.print("UserId: ");
    		String userId = userStringInput();
    		System.out.print("AnnouncementId: ");
    		String announcementId = userStringInput();
    		
    		String reference = boardType+";"+userId+";"+announcementId;
    		announcementList.add(reference);
    	}
    	
    	String returned = client.postGeneral(publicKey, message, announcementList);
    	
    	/* Verifying returned values */
    	if (!returned.equals("Success")) { // In case something failed
    		System.err.println(returned);
    		System.err.println("Please repeat!");
    		postGeneralMenu();
    	} else { // In case of success
    		System.out.println("Success, your post was posted");
    	}
    }
    
    /* Read Menu */
    public static void readMenu() {
    	menu.displayReadMenu();
    }
    
    /* Read General Menu */
    public static void readGeneralMenu() {
    	menu.displayReadGeneralMenu();
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
    
}
