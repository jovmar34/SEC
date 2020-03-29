package org.announcementserver.ws;

import javax.jws.WebService;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.announcementserver.exceptions.EmptyBoardException;
import org.announcementserver.exceptions.InvalidNumberException;
import org.announcementserver.exceptions.MessageSizeException;
import org.announcementserver.exceptions.NumberPostsException;
import org.announcementserver.exceptions.PostTypeException;
import org.announcementserver.exceptions.ReferredAnnouncementException;
import org.announcementserver.exceptions.ReferredUserException;
import org.announcementserver.exceptions.UserNotRegisteredException;

import java.util.List;

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
	public String post(String publicKey, String message, List<String> announcementList) throws UserNotRegisteredException, MessageSizeException, ReferredUserException, PostTypeException, ReferredAnnouncementException {
		return AnnouncementServer.getInstance().post(publicKey, message, announcementList);
	}
	
	/* Post General */
	public String postGeneral(String publicKey, String message, List<String> announcementList) throws UserNotRegisteredException, MessageSizeException, ReferredUserException, ReferredAnnouncementException, PostTypeException {
		return AnnouncementServer.getInstance().postGeneral(publicKey, message, announcementList);
	}
	
	/* Read */
	public String read(String publicKey, Long number) throws InvalidNumberException, ReferredUserException, EmptyBoardException, NumberPostsException {
		return AnnouncementServer.getInstance().read(publicKey, number);
	}
	
	/* Read General */
	public String readGeneral(Long number) throws InvalidNumberException, EmptyBoardException, NumberPostsException {
		return AnnouncementServer.getInstance().readGeneral(number);
	}
	
}