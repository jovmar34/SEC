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
	public List<String> register(String publicKey, String signature) throws UserAlreadyRegisteredFault_Exception {
		List<String> res = null;
		try {
			res =  AnnouncementServer.getInstance().register(publicKey, signature);
		} catch (UserAlreadyRegisteredException e) {
			throwUserAlreadyRegisteredFault(e.getMessage());
		}
		return res;
	}
	
	/* Post */
	public List<String> post(String publicKey, String message, List<String> announcementList, String signature) 
			throws UserNotRegisteredFault_Exception, MessageSizeFault_Exception, ReferredUserFault_Exception, PostTypeFault_Exception, ReferredAnnouncementFault_Exception {
		List<String> res = null;
		try {
			res = AnnouncementServer.getInstance().post(publicKey, message, announcementList, signature);
		} catch (UserNotRegisteredException e) {
			throwUserNotRegisteredFault(e.getMessage());
		} catch (MessageSizeException e) {
			throwMessageSizeFault(e.getMessage());
		} catch (ReferredUserException e) {
			throwReferredUserFault(e.getMessage());
		} catch (PostTypeException e) {
			throwPostTypeFault(e.getMessage());
		} catch (ReferredAnnouncementException e) {
			throwReferredAnnouncementFault(e.getMessage());
		}
		return res;
	}
	
	/* Post General */
	public List<String> postGeneral(String publicKey, String message, List<String> announcementList, String signature) 
			throws UserNotRegisteredFault_Exception, MessageSizeFault_Exception, ReferredUserFault_Exception, PostTypeFault_Exception, ReferredAnnouncementFault_Exception {
		List<String> res = null;
		try {
			res = AnnouncementServer.getInstance().postGeneral(publicKey, message, announcementList, signature);
		} catch (UserNotRegisteredException e) {
			throwUserNotRegisteredFault(e.getMessage());
		} catch (MessageSizeException e) {
			throwMessageSizeFault(e.getMessage());
		} catch (ReferredUserException e) {
			throwReferredUserFault(e.getMessage());
		} catch (PostTypeException e) {
			throwPostTypeFault(e.getMessage());
		} catch (ReferredAnnouncementException e) {
			throwReferredAnnouncementFault(e.getMessage());
		}
		return res;
	}
	
	/* Read */
	public List<String> read(String publicKey, Long number, String signature) 
			throws InvalidNumberFault_Exception, ReferredUserFault_Exception, EmptyBoardFault_Exception, NumberPostsFault_Exception {
		List<String> res = null;
		try {
			res = AnnouncementServer.getInstance().read(publicKey, number, signature);
		} catch (InvalidNumberException e) {
			throwInvalidNumberFault(e.getMessage());
		} catch (ReferredUserException e) {
			throwReferredUserFault(e.getMessage());
		} catch (EmptyBoardException e) {
			throwEmptyBoardFault(e.getMessage());
		} catch (NumberPostsException e) {
			throwNumberPostsFault(e.getMessage());
		}
		return res;
	}
	
	/* Read General */
	public List<String> readGeneral(Long number, String signature) 
			throws InvalidNumberFault_Exception, EmptyBoardFault_Exception, NumberPostsFault_Exception {
		List<String> res = null;
		try {
			res = AnnouncementServer.getInstance().readGeneral(number, signature);
		} catch (InvalidNumberException e) {
			throwInvalidNumberFault(e.getMessage());
		} catch (EmptyBoardException e) {
			throwEmptyBoardFault(e.getMessage());
		} catch (NumberPostsException e) {
			throwNumberPostsFault(e.getMessage());
		}
		return res;
	}
	
	/* Exceptions handlers */
	private void throwUserAlreadyRegisteredFault(final String message) throws UserAlreadyRegisteredFault_Exception {
		UserAlreadyRegisteredFault faultInfo = new UserAlreadyRegisteredFault();
		faultInfo.message = message;
		throw new UserAlreadyRegisteredFault_Exception(message, faultInfo);
	}
	private void throwUserNotRegisteredFault(final String message) throws UserNotRegisteredFault_Exception {
		UserNotRegisteredFault faultInfo = new UserNotRegisteredFault();
		faultInfo.message = message;
		throw new UserNotRegisteredFault_Exception(message, faultInfo);
	}
	private void throwMessageSizeFault(final String message) throws MessageSizeFault_Exception {
		MessageSizeFault faultInfo = new MessageSizeFault();
		faultInfo.message = message;
		throw new MessageSizeFault_Exception(message, faultInfo);
	}
	private void throwReferredUserFault(final String message) throws ReferredUserFault_Exception {
		ReferredUserFault faultInfo = new ReferredUserFault();
		faultInfo.message = message;
		throw new ReferredUserFault_Exception(message, faultInfo);
	}
	private void throwPostTypeFault(final String message) throws PostTypeFault_Exception {
		PostTypeFault faultInfo = new PostTypeFault();
		faultInfo.message = message;
		throw new PostTypeFault_Exception(message, faultInfo);
	}
	private void throwReferredAnnouncementFault(final String message) throws ReferredAnnouncementFault_Exception {
		ReferredAnnouncementFault faultInfo = new ReferredAnnouncementFault();
		faultInfo.message = message;
		throw new ReferredAnnouncementFault_Exception(message, faultInfo);
	}
	private void throwInvalidNumberFault(final String message) throws InvalidNumberFault_Exception {
		InvalidNumberFault faultInfo = new InvalidNumberFault();
		faultInfo.message = message;
		throw new InvalidNumberFault_Exception(message, faultInfo);
	}
	private void throwEmptyBoardFault(final String message) throws EmptyBoardFault_Exception {
		EmptyBoardFault faultInfo = new EmptyBoardFault();
		faultInfo.message = message;
		throw new EmptyBoardFault_Exception(message, faultInfo);
	}
	private void throwNumberPostsFault(final String message) throws NumberPostsFault_Exception {
		NumberPostsFault faultInfo = new NumberPostsFault();
		faultInfo.message = message;
		throw new NumberPostsFault_Exception(message, faultInfo);
	}

}