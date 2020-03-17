package org.announcementserver.ws.cli;

/** Exception to be thrown when something goes wrong with the client */
public class AnnouncementServerClientException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public AnnouncementServerClientException() {
		super();
	}
	
	public AnnouncementServerClientException(String message) {
		super(message);
	}
	
	public AnnouncementServerClientException(Throwable cause) {
		super(cause);
	}

	public AnnouncementServerClientException(String message, Throwable cause) {
		super(message, cause);
	}
}