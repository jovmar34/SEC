package org.announcementserver.ws;

import javax.xml.ws.Endpoint;


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

    	String wsURL = null;

    	// Create server implementation object, according to options
    	AnnouncementServerEndpointManager endpoint = null;
    	wsURL = args[0];
    	endpoint = new AnnouncementServerEndpointManager(wsURL);

    	try {
    		endpoint.start();
    		endpoint.awaitConnections();
    	} finally {
    		endpoint.stop();
    	}
    }
}