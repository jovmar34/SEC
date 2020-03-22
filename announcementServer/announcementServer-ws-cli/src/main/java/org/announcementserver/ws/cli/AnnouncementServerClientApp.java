package org.announcementserver.ws.cli;

import java.util.Scanner;
import org.announcementserver.utils.*;

/**
 * Client Side Application
 *
 */
public class AnnouncementServerClientApp {
	
	public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // Text in green
	public static final String RED_BOLD_BRIGHT = "\033[1;91m"; // Text in red
	public static final String RESET = "\033[0m"; // Text reset
	
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
    	String wsI = args[1];
    	
    	if (Integer.valueOf(wsI)>=1 && Integer.valueOf(wsI)<=5) {
    		System.out.printf("Creating client for server at %s%n", wsURL);
        	client = new AnnouncementServerClient(wsURL);
    	} else {
    		System.err.println("UserId must be between 1 and 5.");
    		return;
    	}
    	
        // Start of Interaction
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
