package org.announcementserver.ws;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.announcementserver.exceptions.*;

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
	public  String register(String publicKey) throws UserAlreadyRegisteredFault_Exception {
		String res = "";
		try {
			res =  AnnouncementServer.getInstance().register(publicKey);
		} catch (UserAlreadyRegisteredException e) {
			throwUserAlreadyRegisteredFault(e.getMessage());
		}
		return res;
	}
	
	/* Post */
	public String post(String publicKey, String message, List<String> announcementList) {
		String res = "";
		try {
			res = AnnouncementServer.getInstance().post(publicKey, message, announcementList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/* Post General */
	public String postGeneral(String publicKey, String message, List<String> announcementList) {
		String res = "";
		try {
			res = AnnouncementServer.getInstance().postGeneral(publicKey, message, announcementList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/* Read */
	public String read(String publicKey, Long number) {
		String res = "";
		try {
			res = AnnouncementServer.getInstance().read(publicKey, number);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/* Read General */
	public String readGeneral(Long number) {
		String res = "";
		try {
			res = AnnouncementServer.getInstance().readGeneral(number);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private void throwUserAlreadyRegisteredFault(final String message) throws UserAlreadyRegisteredFault_Exception {
		UserAlreadyRegisteredFault faultInfo = new UserAlreadyRegisteredFault();
		faultInfo.message = message;
		throw new UserAlreadyRegisteredFault_Exception(message, faultInfo);
	}
}