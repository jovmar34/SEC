package org.announcementserver.utils;

public class Menus {
	
	public static final String RESET = "\033[0m"; // Text Reset
	public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // Text in white
	
	public void displayMainMenu() {
		System.out.println(WHITE_BOLD_BRIGHT);
		System.out.println("+-----------------------------------------------+");
		System.out.println("|               ANNOUNCEMENT SERVER             |");
		System.out.println("|                    MAIN MENU                  |");
		System.out.println("|                                               |");
		System.out.println("+-----------------------------------------------+");
		System.out.println("| 1 - Register                                  |");
		System.out.println("| 2 - Post                                      |");
		System.out.println("| 3 - Post General                              |");
		System.out.println("| 4 - Read                                      |");
		System.out.println("| 5 - Read General                              |");
		System.out.println("| 6 - Exit                                      |");
		System.out.println("+-----------------------------------------------+");
		System.out.println(RESET);
	}
	
	public void displayRegisterMenu() {
		System.out.println(WHITE_BOLD_BRIGHT);
		System.out.println("+-----------------------------------------------+");
		System.out.println("|                                               |");
		System.out.println("|                 REGISTER PAGE                 |");
		System.out.println("|                                               |");
		System.out.println("+-----------------------------------------------+");
		System.out.println(RESET);
	}
	
	public void displayPostMenu() {
		System.out.println(WHITE_BOLD_BRIGHT);
		System.out.println("+-----------------------------------------------+");
		System.out.println("|                                               |");
		System.out.println("|                    POST PAGE                  |");
		System.out.println("|                                               |");
		System.out.println("+-----------------------------------------------+");
		System.out.println(RESET);
	}
	
	public void displayPostGeneralMenu() {
		System.out.println(WHITE_BOLD_BRIGHT);
		System.out.println("+-----------------------------------------------+");
		System.out.println("|                                               |");
		System.out.println("|               POST GENERAL PAGE               |");
		System.out.println("|                                               |");
		System.out.println("+-----------------------------------------------+");
		System.out.println(RESET);
	}
	
	public void displayReadMenu() {
		System.out.println(WHITE_BOLD_BRIGHT);
		System.out.println("+-----------------------------------------------+");
		System.out.println("|                                               |");
		System.out.println("|                    READ PAGE                  |");
		System.out.println("|                                               |");
		System.out.println("+-----------------------------------------------+");
		System.out.println(RESET);
	}
	
	public void displayReadGeneralMenu() {
		System.out.println(WHITE_BOLD_BRIGHT);
		System.out.println("+-----------------------------------------------+");
		System.out.println("|                                               |");
		System.out.println("|               READ GENERAL PAGE               |");
		System.out.println("|                                               |");
		System.out.println("+-----------------------------------------------+");
		System.out.println(RESET);
	}
	
	public void displayExitMenu() {
		System.out.println(WHITE_BOLD_BRIGHT);
		System.out.println("+-----------------------------------------------+");
		System.out.println("|                                               |");
		System.out.println("|                    EXITING...                 |");
		System.out.println("|                                               |");
		System.out.println("+-----------------------------------------------+");
		System.out.println(RESET);
	}

}