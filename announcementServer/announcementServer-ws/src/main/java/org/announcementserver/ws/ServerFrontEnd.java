package org.announcementserver.ws;

import java.util.List;

import org.announcementserver.ws.AnnouncementServerPortType;
import org.announcementserver.ws.AnnouncementServerProxy;

public class ServerFrontEnd {
    private AnnouncementServerProxy proxy;
    private List<AnnouncementServerPortType> ports;

    public ServerFrontEnd(AnnouncementServerProxy proxy) {
        this.proxy = proxy;
        //createStub();
    }

    public void writeEcho() {

    }
    
}