package org.announcementserver.ws;

import java.io.IOException;
import javax.xml.ws.Endpoint;
import org.uddi.naming.UDDINaming;

/** The endpoint manager starts and registers the service. */
public class AnnouncementServerEndpointManager {
	
	/* Variables */
	
	/** UDDI Naming Server Location */
	private String uddiURL = null;
	
	/** Web Service Name */
	private String wsName = null;
	
	/** Web Service location to publish */
	private String wsURL = null;
	
	/** Port Implementation */
	public AnnouncementServerPortImpl portImpl = new AnnouncementServerPortImpl(this);
	
	/** Web Service Endpoint */
	private Endpoint endpoint = null;
	
	/** UDDI Naming instance for containing UDDI server */
	private UDDINaming uddiNaming = null;
	
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
	
	/** Constructor 1 */
	public AnnouncementServerEndpointManager(String uddiURL, String wsName, String wsURL) {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		this.wsURL = wsURL;
	}
	
	/** Constructor 2 */
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
		publishToUDDI();
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
		unpublishFromUDDI();
	}
	
	/** Publish to UDDI */
	void publishToUDDI() throws Exception {
		try {
			if(uddiURL != null) {
				if (verbose) {
					System.out.printf("Publishing '%s' to UDDI at %s%n", wsName, uddiURL);
				}
			}
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(wsName, wsURL);
		} catch (Exception e) {
			uddiNaming = null;
			if (verbose) {
				System.out.printf("Exception caught when binding to UDDI: %s%n", e);
			}
			throw e;
		}
	}
	
	/** Unpublish from UDDI */
	void unpublishFromUDDI() {
		try {
			if (uddiNaming != null) {
				uddiNaming.unbind(wsName);
				if (verbose) {
					System.out.printf("Unpublished '%s' from UDDI", wsName);
				}
				uddiNaming = null;
			}
		} catch (Exception e){
			if (verbose) {
				System.out.printf("Exception caught when unbinding: %s%n", e);
			}
		}
	}
}