package org.announcementserver.ws.cli;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
	
	private static Menus menu = new Menus();
	private static AnnouncementServerClient client = null;
	
    public static void main(String[] args ) throws AnnouncementServerClientException {
    	
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
    private static void mainMenu() {
    	
    	/* Authentication */
    	System.out.print("Provide your username: ");
    	String username = userStringInput();
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
    public static void registerMenu() {
    	menu.displayRegisterMenu();
    	String returned = client.register();
    	System.out.println(returned);
    }
    
    /* Post Menu */
    public static void postMenu() {
    	menu.displayPostMenu();
    }
    
    /* Post General Menu */
    public static void postGeneralMenu() {
    	menu.displayPostGeneralMenu();
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
