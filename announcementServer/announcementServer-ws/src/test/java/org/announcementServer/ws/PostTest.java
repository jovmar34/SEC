package org.announcementServer.ws;

import org.announcementserver.ws.AnnouncementServer;
import org.announcementserver.ws.Announcement;
import org.junit.*;



public class PostTest {
	AnnouncementServer instance;
	
	@Before
	public void start() {
		instance = AnnouncementServer.getInstance();
	}
	
	@Test
	public void readNormal() {
		
	}
	
	@After
	public void cleanup() {
		instance.clean();
	}
}
