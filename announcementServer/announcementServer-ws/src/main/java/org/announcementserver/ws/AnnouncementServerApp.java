package org.announcementserver.ws;

import java.util.Scanner;
import javax.xml.ws.Endpoint;
import org.announcementserver.utils.*;
import org.announcementserver.common.Constants;

/**
 * Server Side Application
 */
public class AnnouncementServerApp {
	
    public static void main(String[] args ) throws Exception {
    	// Check arguments
    	if (args.length == 0) {
    		System.err.println("Argument(s) missing!");
    		System.err.println("Usage: java " + AnnouncementServerApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
    		return;
    	}

		String host = null;
		String id;

    	// Create server implementation object, according to options
    	AnnouncementServerEndpointManager endpoint = null;
		host = args[0];
		id = args[1];
		endpoint = new AnnouncementServerEndpointManager(String.format(Constants.WS_NAME_FORMAT, host, Constants.PORT_START + Integer.valueOf(id)));
		
		endpoint.portImpl.proxy.myId = Constants.SERVER_NAME + id;
		AnnouncementServer.getInstance().setId(Constants.SERVER_NAME + id);
		
    	String answer = "";

    	try {
			endpoint.start();

    		// Verifies if needed to recover announcement server state
    		boolean incorrectAnswer = true;
    		while (incorrectAnswer) {
    			System.out.print("Want to recover server state? (Use 'y' for Yes and 'n' for No): ");
    			answer = userStringInput();
    			if (answer.equals("y") || answer.equals("n")) {
    				incorrectAnswer = false;
    			} else {
    				System.out.println("ERROR: Use either 'y' or 'n'");
    			}
    		}

    		if (answer.equals("y")) {
    			PersistenceUtils.recover(Constants.SERVER_NAME + id);
			}
    		
    		endpoint.awaitConnections();
    	} finally {
    		endpoint.stop();
    	}
    }
    
    // --- Checkings -------------------------------------

	@SuppressWarnings("resource")
	private static String userStringInput() {
		Scanner in = new Scanner(System.in);
		String i = in.nextLine();
		return i;
	}
}