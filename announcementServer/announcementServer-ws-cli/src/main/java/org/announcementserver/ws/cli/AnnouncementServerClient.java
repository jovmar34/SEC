package org.announcementserver.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import java.util.Map;
import java.util.List;
import javax.xml.ws.BindingProvider;
import org.announcementserver.ws.*;

/** */
public class AnnouncementServerClient {
	
	/* Variables */
	
	/** WS Service */
	AnnouncementServerService service = null;
	
	/** WS port */
	AnnouncementServerPortType port = null;
	
	/** Web Service URL */
	private String wsURL = null;
	
	/** Verbose output? */
	private boolean verbose = false;
	
	/* Methods */
	
	public boolean isVerbose() { return verbose; }
	
	public void setVerbose(boolean verbose) { this.verbose = verbose; }
	
	public String getWsURL() { return wsURL; }

	public AnnouncementServerClient(String wsURL) throws AnnouncementServerClientException {
		this.wsURL = wsURL;
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

	public String register(String publicKey) {
		return port.register(publicKey);
	}
	
	public String post(String publicKey, String message, List<String> announcementList) {
		return port.post(publicKey, message, announcementList);
	}
	
	public String postGeneral(String publicKey, String message, List<String> announcementList) {
		return port.postGeneral(publicKey, message, announcementList);
	}
	
	public String read(String publicKey, Long number) {
		return port.read(publicKey, number);
	}
	
	public String readGeneral(Long number) {
		return port.readGeneral(number);
	}
	
}