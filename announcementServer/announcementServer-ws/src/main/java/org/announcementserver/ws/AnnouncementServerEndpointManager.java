package org.announcementserver.ws;

import java.io.IOException;
import javax.xml.ws.Endpoint;

/** The endpoint manager starts and registers the service. */
public class AnnouncementServerEndpointManager {
	
	/* Variables */
	
	/** Web Service Name */
	private String wsName = null;
	
	/** Web Service location to publish */
	private String wsURL = null;
	
	/** Port Implementation */
	public AnnouncementServerPortImpl portImpl = new AnnouncementServerPortImpl(this);
	
	/** Web Service Endpoint */
	private Endpoint endpoint = null;
	
	/** Verbose output? */
	private boolean verbose = true;
	
	
	/* Methods */
	
	/** Get Web Service UDDI publication name */
	public String getWsName() {
		return wsName;
	}
	
	public boolean isVerbose() {
		return verbose;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	/** Obtain Port Implementation */
	@SuppressWarnings("unused")
	private AnnouncementServerPortImpl getPort() {
		return portImpl;
	}
	
	/** Constructor  */
	public AnnouncementServerEndpointManager(String wsURL) {
		if (wsURL == null) {
			throw new NullPointerException("Null pointer exception: URL cannot be null!");
		}
		this.wsURL = wsURL;
	}
	
	/** Start endpoint  */
	public void start() throws Exception {
		try {
			endpoint = Endpoint.create(this.portImpl);
			if (verbose) {
				System.out.printf("Starting %s%n", wsURL);
			}
			endpoint.publish(wsURL);
		} catch (Exception e) {
			endpoint = null;
			if (verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
	}
	
	/** Await for connections */
	public void awaitConnections() {
		if (verbose) {
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
		}
		try {
			System.in.read();
		} catch(IOException e) {
			if(verbose) {
				System.out.printf("IOExceptuon caught when awaiting requests: %s%n", e);
			}
		}
	}
	
	/** Stop endpoint */
	public void stop() throws Exception {
		try {
			if(endpoint != null) {
				endpoint.stop();
				if (verbose) {
					System.out.printf("Stopped %s%n", wsURL);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Exception caught when stopping: %s%n", e);
			}
		}
		this.portImpl = null;
	}
}