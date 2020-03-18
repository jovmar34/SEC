package org.announcementserver.ws;

import javax.xml.ws.Endpoint;


/**
 * Server Side Application
 */
public class AnnouncementServerApp {
	
    public static void main(String[] args ) throws Exception {
    	// Check arguments
    	if (args.length == 0 || args.length == 2) {
    		System.err.println("Argument(s) missing!");
    		System.err.println("Usage: java " + AnnouncementServerApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
    		return;
    	}

    	String uddiURL = null;
    	String wsName = null;
    	String wsURL = null;

    	// Create server implementation object, according to options
    	AnnouncementServerEndpointManager endpoint = null;
    	if (args.length == 1) {
    		wsURL = args[0];
    		endpoint = new AnnouncementServerEndpointManager(wsURL);

    	} else if (args.length >= 3) {
    		uddiURL = args[0];
    		wsName = args[1];
    		wsURL = args[2];
    		endpoint = new AnnouncementServerEndpointManager(uddiURL, wsName, wsURL);

    	}

    	try {
    		endpoint.start();
    		endpoint.awaitConnections();
    	} finally {
    		endpoint.stop();
    	}
    }
}