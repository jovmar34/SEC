package org.announcementServer.ws;

import org.announcementserver.ws.AnnouncementServer;
import org.announcementserver.exceptions.EmptyBoardException;
import org.announcementserver.exceptions.InvalidNumberException;
import org.announcementserver.exceptions.NumberPostsException;
import org.announcementserver.exceptions.ReferredUserException;
import org.announcementserver.exceptions.UserAlreadyRegisteredException;
import org.announcementserver.ws.Announcement;
import org.junit.*;



public class ReadTest {
	AnnouncementServer instance;
	
	@Before
	public void start() throws UserAlreadyRegisteredException {
		instance = AnnouncementServer.getInstance();
		Announcement a1 = new Announcement("c1", "Testing Testing", "id1");
		Announcement a2 = new Announcement("c1", "New Testing", "id2");
		
		instance.register("userkey");
		instance.register("newkey");
		instance.putPersonal("userkey", a1);
		instance.putPersonal("userkey", a2);
	}
	
	@Test
	public void readNormal() throws InvalidNumberException, ReferredUserException, EmptyBoardException, NumberPostsException {
		// nothing bad should happen
		instance.read("userkey", new Long(1));
	}
	
	@Test
	public void readNormal2() throws InvalidNumberException, ReferredUserException, EmptyBoardException, NumberPostsException {
		// nothing bad should happen
		System.out.print(instance.read("userkey", new Long(2)));
	}

	
	@Test
	public void noUser() throws InvalidNumberException, ReferredUserException, EmptyBoardException, NumberPostsException {
		// user is not known
		Assert.assertEquals("Unknown user", instance.read("randkey", new Long(1)));
	}
	
	@Test
	public void noPosts() throws InvalidNumberException, ReferredUserException, EmptyBoardException, NumberPostsException {
		// user is not known
		Assert.assertEquals("No posts", instance.read("newkey", new Long(1)));
	}
	
	@Test
	public void notEnough() throws InvalidNumberException, ReferredUserException, EmptyBoardException, NumberPostsException {
		// user is not known
		Assert.assertEquals("Not enough posts", instance.read("userkey", new Long(3)));
	}
	
	@Test
	public void invalidRead() throws InvalidNumberException, ReferredUserException, EmptyBoardException, NumberPostsException {
		// user is not known
		Assert.assertEquals("Invalid number", instance.read("userkey", new Long(-1)));
	}
	
	@After
	public void cleanup() {
		instance.clean();
	}
}
