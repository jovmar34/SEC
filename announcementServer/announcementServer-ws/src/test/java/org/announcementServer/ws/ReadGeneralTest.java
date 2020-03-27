package org.announcementServer.ws;

import org.announcementserver.ws.AnnouncementServer;
import org.announcementserver.ws.Announcement;
import org.junit.*;

public class ReadGeneralTest {
	AnnouncementServer instance;
	Announcement a1, a2;
	
	@Before
	public void start() {
		instance = AnnouncementServer.getInstance();
		a1 = new Announcement("c1", "Testing Testing", 0, "");
		a2 = new Announcement("c1", "New Testing", 1, "");
	}
	
	@Test
	public void readNormal() {
		// nothing bad should happen
		instance.putGeneral(a1);
		
		instance.readGeneral(new Long(1));
	}
	
	@Test
	public void readNormal2() {
		// nothing bad should happen
		instance.putGeneral(a1);
		instance.putGeneral(a2);
		
		System.out.print(instance.readGeneral(new Long(2)));
	}
	
	@Test
	public void noPosts() {
		// user is not known
		Assert.assertEquals("No posts", instance.readGeneral(new Long(1)));
	}
	
	@Test
	public void notEnough() {
		// user is not known
		instance.putGeneral(a1);
		instance.putGeneral(a2);
		
		Assert.assertEquals("Not enough posts", instance.readGeneral(new Long(3)));
	}
	
	@Test
	public void invalidRead() {
		// user is not known
		Assert.assertEquals("Invalid number", instance.readGeneral(new Long(-1)));
	}
	
	@After
	public void cleanup() {
		instance.clean();
	}
}
