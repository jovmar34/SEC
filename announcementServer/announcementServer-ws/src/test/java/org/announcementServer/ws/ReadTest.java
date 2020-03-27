package org.announcementServer.ws;

import org.announcementserver.ws.AnnouncementServer;
import org.announcementserver.ws.Announcement;
import org.junit.*;



public class ReadTest {
	AnnouncementServer instance;
	
	@Before
	public void start() {
		instance = AnnouncementServer.getInstance();
		Announcement a1 = new Announcement("c1", "Testing Testing", 0, "");
		Announcement a2 = new Announcement("c1", "New Testing", 1, "");
		
		instance.register("userkey");
		instance.register("newkey");
		instance.putPersonal("userkey", a1);
		instance.putPersonal("userkey", a2);
	}
	
	@Test
	public void readNormal() {
		// nothing bad should happen
		instance.read("userkey", new Long(1));
	}
	
	@Test
	public void readNormal2() {
		// nothing bad should happen
		System.out.print(instance.read("userkey", new Long(2)));
	}

	
	@Test
	public void noUser() {
		// user is not known
		Assert.assertEquals("Unknown user", instance.read("randkey", new Long(1)));
	}
	
	@Test
	public void noPosts() {
		// user is not known
		Assert.assertEquals("No posts", instance.read("newkey", new Long(1)));
	}
	
	@Test
	public void notEnough() {
		// user is not known
		Assert.assertEquals("Not enough posts", instance.read("userkey", new Long(3)));
	}
	
	@Test
	public void invalidRead() {
		// user is not known
		Assert.assertEquals("Invalid number", instance.read("userkey", new Long(-1)));
	}
	
	@After
	public void cleanup() {
		instance.clean();
	}
}
