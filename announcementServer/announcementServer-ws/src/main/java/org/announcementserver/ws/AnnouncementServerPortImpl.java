package org.announcementserver.ws;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.announcementserver.common.PossibleTamperingException;
import org.announcementserver.exceptions.EmptyBoardException;
import org.announcementserver.exceptions.InvalidNumberException;
import org.announcementserver.exceptions.MessageSizeException;
import org.announcementserver.exceptions.NumberPostsException;
import org.announcementserver.exceptions.PostTypeException;
import org.announcementserver.exceptions.ReferredAnnouncementException;
import org.announcementserver.exceptions.ReferredUserException;
import org.announcementserver.exceptions.UserNotRegisteredException;

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

	private AnnouncementServerProxy proxy;

	/** Constructor receives a reference to the endpoint manager. */
	public AnnouncementServerPortImpl(AnnouncementServerEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		this.proxy = new AnnouncementServerProxy();
	}
	
	// Main operations -----------------------------------------------
	
	/* Register */
	public List<String> register(String publicKey, String signature) {
		List<String> res = null;
		
		try {
			res = AnnouncementServer.getInstance().register(publicKey, signature);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		return res;
	}
	
	/* Post */
	public List<String> post(String publicKey, String message, List<String> announcementList, String signature) 
			throws UserNotRegisteredFault_Exception, MessageSizeFault_Exception, ReferredUserFault_Exception, PostTypeFault_Exception, ReferredAnnouncementFault_Exception {
		List<String> res = null;
		try {
			res = this.proxy.post(publicKey, message, announcementList, signature);
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
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return res;
	}
	
	/* Post General */
	public List<String> postGeneral(String publicKey, String message, List<String> announcementList, String signature) 
			throws UserNotRegisteredFault_Exception, MessageSizeFault_Exception, ReferredUserFault_Exception, PostTypeFault_Exception, ReferredAnnouncementFault_Exception {
		List<String> res = null;
		try {
			res = this.proxy.postGeneral(publicKey, message, announcementList, signature);
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
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return res;
	}
	
	/* Read */
	public List<String> read(String readerKey, String publicKey, Long number, String signature) 
			throws InvalidNumberFault_Exception, ReferredUserFault_Exception, EmptyBoardFault_Exception, NumberPostsFault_Exception {
		List<String> res = null;
		try {
			res = this.proxy.read(readerKey, publicKey, number, signature);
		} catch (InvalidNumberException e) {
			throwInvalidNumberFault(e.getMessage());
		} catch (ReferredUserException e) {
			throwReferredUserFault(e.getMessage());
		} catch (EmptyBoardException e) {
			throwEmptyBoardFault(e.getMessage());
		} catch (NumberPostsException e) {
			throwNumberPostsFault(e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return res;
	}
	
	/* Read General */
	public List<String> readGeneral(String readerKey, Long number, String signature) 
			throws InvalidNumberFault_Exception, EmptyBoardFault_Exception, NumberPostsFault_Exception {
		List<String> res = null;
		try {
			res = this.proxy.readGeneral(readerKey, number, signature);
		} catch (InvalidNumberException e) {
			throwInvalidNumberFault(e.getMessage());
		} catch (EmptyBoardException e) {
			throwEmptyBoardFault(e.getMessage());
		} catch (NumberPostsException e) {
			throwNumberPostsFault(e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return res;
	}
	
	/* Exceptions handlers */
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