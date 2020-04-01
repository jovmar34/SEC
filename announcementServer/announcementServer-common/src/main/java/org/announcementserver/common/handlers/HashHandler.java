package org.announcementserver.common.handlers;

import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.announcementserver.common.PossibleTamperingException;

import org.announcementserver.common.HandlerOpInterface;
//import org.announcementserver.common.PossibleTamperingException;

public class HashHandler extends CommonHandler {
	public static String clientId;

	public void close(MessageContext messagecontext) {
    }

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleFault(SOAPMessageContext messagecontext) {
        return true;
    }

    public boolean handleMessage(SOAPMessageContext messagecontext) {
        Boolean outbound = (Boolean) messagecontext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        SOAPMessage message = messagecontext.getMessage();

		try {
	        if (outbound) {
				SOAPElement body = message.getSOAPBody();
			
				String text = this.nodeToString(body);
							
				MessageDigest digest = null;
				try {
					digest = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException("The hashing algorithm does not exist");					
				}
							
				digest.update(text.getBytes());
				
				byte[] bytes = digest.digest();
        	
				String encodedHash = Base64.getEncoder().encodeToString(bytes);
				
				SOAPElement elem = message.getSOAPBody().addChildElement(new QName("Hash"));
				
				elem.setTextContent(encodedHash);
				
				System.out.println(clientId);
	        }
	        else
	        {
	        	System.out.println("Dling Dlong!");
				SOAPElement body = message.getSOAPBody();
				
				Iterator<?> iter = body.getChildElements(new QName("Hash"));
				
				if(iter == null || !iter.hasNext())
				{
					throw new PossibleTamperingException("SOAP body doesn't have the Hash child");
				}
				
				SOAPElement hashNode = (SOAPElement) iter.next();
				
				String receivedEncodedHash = hashNode.getTextContent();
				body.removeChild(hashNode);
				
				String receivedHash = new String(Base64.getDecoder().decode(receivedEncodedHash.getBytes()));
				
				String text = this.nodeToString(body);
				
				MessageDigest digest = null;
				try {
					digest = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException("The hashing algorithm does not exist");					
				}
				
				digest.update(text.getBytes());
				
				byte[] bytes = digest.digest();
	    	
				String encodedHash = new String(bytes);				
				
				if(!encodedHash.equals(receivedHash))
				{
					throw new PossibleTamperingException("The computed Hash is different from the message Hash");
				}
				
				System.out.println(clientId);
        	}
		} catch (SOAPException | PossibleTamperingException e) {
			return this.onPossibleTamperingException(e);
		}
		
		/* FIXME necessary?
    	if(writeOut)
    	{
    		try {
        		message.saveChanges();
				message.writeTo(System.out);
				System.out.println();
			} catch (SOAPException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	*/
		
        return true;
    }
}
