package org.announcementserver.ws;

import javax.jws.WebService;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import java.io.IOException;

@WebService(endpointInterface = "org.announcementserver.ws.AnnouncementServerPortType",
		wsdlLocation = "",
		name = "AnnouncementServerWebService",
		portName = "AnnouncementServerPort",
		targetNamespace= "http://ws.announcementserver.org",
		serviceName = "AnnouncementServerService"
		)

public class AnnouncementServerPortImpl implements AnnouncementServerPortType {
	
	@Resource
	private WebServiceContext wsContext;
	
	/** The Endpoint manager controls the Web Service instance during its whole lifecycle */
	@SuppressWarnings("unused")
	private final AnnouncementServerEndpointManager endpointManager;

	/** Constructor receives a reference to the endpoint manager. */
	public AnnouncementServerPortImpl(AnnouncementServerEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}
	
	// Main operations -----------------------------------------------
	
	/* Register */
	public  String register(String publicKey) {
		return AnnouncementServer.getInstance().register(publicKey);
	}
	
	/* Post */
	public String post(String publicKey, String message, String announcementList) {
		return AnnouncementServer.getInstance().post(publicKey, message, announcementList);
	}
	
	/* Post General */
	public String postGeneral(String publicKey, String message, String announcementList) {
		return AnnouncementServer.getInstance().postGeneral(publicKey, message, announcementList);
	}
	
	/* Read */
	public String read(String publicKey, Long number) {
		return AnnouncementServer.getInstance().read(publicKey, number);
	}
	
	/* Read General */
	public String readGeneral(Long number) {
		return AnnouncementServer.getInstance().readGeneral(number);
	}
	
}