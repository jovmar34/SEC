package org.announcementserver.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Map;

import javax.xml.ws.BindingProvider;

/** */
public class AnnouncementServerClient {
	
	/* Variables */
	
	/** WS Service */
	AnnouncementServerService service = null;
	
	/** WS port */
	AnnouncementServerPortType port = null;
	
	/** Web Service Name */
	private String wsName = null;
	
	/** Web Service URL */
	private String wsURL = null;
	
	/** UDDI Server URL */
	private String uddiURL = null;
	
	/** Verbose output? */
	private boolean verbose = false;
	
	/* Methods */
	
	public boolean isVerbose() { return verbose; }
	
	public void setVerbose(boolean verbose) { this.verbose = verbose; }
	
	public String getWsURL() { return wsURL; }
	
	/** Constructor 1 */
	public AnnouncementServerClient(String wsURL) throws AnnouncementServerClientException {
		this.wsURL = wsURL;
		createStub();
	}
	
	/** Constructor 2 */
	public AnnouncementServerClient(String uddiURL, String wsName) throws AnnouncementServerClientException {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		createStub();
	}
	
	/** Stub creation and configuration */
	private void createStub() {
		if (verbose)
			System.out.println("Creating stub ...");
		service = new AnnouncementServerService();
		port = service.getAnnouncementServerPort();

		if (wsURL != null) {
			if (verbose)
				System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsURL);
		}
	}
	
	// remote invocation methods ------------------------------------------------------------

	public String register() {
		return port.register();
	}
	
	public String post() {
		return port.post();
	}
	
	public String postGeneral() {
		return port.postGeneral();
	}
	
	public String read() {
		return port.read();
	}
	
	public String readGeneral() {
		return port.readGeneral();
	}
	
}