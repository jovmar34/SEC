package org.announcementserver.common.handlers;

import java.io.StringWriter;

import javax.xml.soap.Node;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public abstract class CommonHandler implements
	javax.xml.ws.handler.soap.SOAPHandler<SOAPMessageContext>{

	protected boolean onPossibleTamperingException(Exception e)
	{
		System.out.println("Possible tampering of message:" + e.getMessage());
		return false;
	}
	
	protected void onProgrammingError(Exception e)
	{
		System.out.println("Programming error");
		e.printStackTrace();
	}
	
	protected  String nodeToString(Node node) {
		  StringWriter sw = new StringWriter();
		  try {
		    Transformer t = TransformerFactory.newInstance().newTransformer();
		    t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		    t.transform(new DOMSource(node), new StreamResult(sw));
		  } catch (TransformerException te) {
		    System.out.println("nodeToString Transformer Exception");
		  }
		  return sw.toString();
	}
}
