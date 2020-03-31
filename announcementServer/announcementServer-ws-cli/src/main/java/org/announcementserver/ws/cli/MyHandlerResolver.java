package org.announcementserver.ws.cli;

import java.util.List;
import java.util.ArrayList;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import org.announcementserver.common.handlers.*;

public class MyHandlerResolver implements HandlerResolver {
		
	@SuppressWarnings("rawtypes")
	public List<Handler> getHandlerChain(PortInfo portInfo) {
		List<Handler> handlers = new ArrayList<Handler>();
    
	    handlers.add(new HashHandler());
	    handlers.add(new BadManHandler());
	
	    return handlers;
	}
}