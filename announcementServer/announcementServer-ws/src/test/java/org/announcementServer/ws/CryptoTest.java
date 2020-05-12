package org.announcementServer.ws;

import org.announcementserver.ws.AnnouncementServer;
import org.announcementserver.ws.AnnouncementServerProxy;
import org.announcementserver.ws.Announcement;
import org.announcementserver.common.*;
import org.junit.*;
import java.util.List;
import java.util.ArrayList;
import org.junit.rules.ExpectedException;

import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.security.KeyStoreException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;

import org.announcementserver.exceptions.MessageSizeException;
import org.announcementserver.exceptions.PostTypeException;
import org.announcementserver.exceptions.ReferredAnnouncementException;
import org.announcementserver.exceptions.ReferredUserException;
import org.announcementserver.exceptions.UserNotRegisteredException;
import org.announcementserver.utils.AnnouncementTools;

import javax.crypto.NoSuchPaddingException;

import org.announcementserver.ws.RegisterReq;
import org.announcementserver.ws.WriteReq;
import org.announcementserver.ws.AnnouncementMessage;


public class CryptoTest {
	AnnouncementServerProxy instance;
	
	@Before
	public void start() {
		instance = AnnouncementServerProxy.getInstance();
	}
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	/**
	 * -- Test1 Description -- 
	 * This test aims to prove that our system can
	 * detect a tampered message. It works by intentionally
	 * passing wrong arguments to hash (in order to break the hash)
	 * and expects to get a RunTimeException
	 */
	
	@Test
	public void testWithBadHashGoodSignature() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
		
		exceptionRule.expect(RuntimeException.class);
		exceptionRule.expectMessage("Error: Possible tampering detected on Hash");
		
		instance.setId("server1");
		List<String> toHash = new ArrayList<>();
		
		RegisterReq request = new RegisterReq();
		request.setSender("client1");
		request.setDestination("server1");
		
		toHash.add(request.getSender());
		toHash.add("this is a broken hash");
		
		String signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
		request.setSignature(signature);
		
		instance.register(request);
	}

	
	/**
	 * -- Test2 Description -- 
	 * This test aims to prove that our system can
	 * detect a tampered message. It works by intentionally
	 * passing a message with a wrong signature (in order to break the signature)
	 * and expects to get a RunTimeException
	 */
	
	@Test
	public void testWithGoodHashBadSignature() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
		
		exceptionRule.expect(RuntimeException.class);
		exceptionRule.expectMessage("Error: Possible tampering detected on Signature");
		
		instance.setId("server1");
		
		RegisterReq request = new RegisterReq();
		request.setSender("client1");
		request.setDestination("server1");
		request.setSignature("this is a broken signature");
		
		instance.register(request);
	}

	/**
	 * -- Test Description -- 
	 * This test aims to prove that our system can
	 * detect a replayed message. It works by intentionally
	 * passing the same message twice (with the same sequence number)
	 * and expects to get a RunTimeException
	 */
	
	@Test
	public void testBadSequenceNumber() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, UserNotRegisteredException, MessageSizeException, ReferredUserException, PostTypeException, ReferredAnnouncementException {
		
		exceptionRule.expect(RuntimeException.class);
		exceptionRule.expectMessage("Error: Possible drop/replay detected");
		
		instance.setId("server1");
		List<String> toHash = new ArrayList<>();
		
		RegisterReq request = new RegisterReq();
		request.setSender("client1");
		request.setDestination("server1");
		
		toHash.add(request.getSender());
		toHash.add(request.getDestination());
		
		String signature = CryptoTools.makeSignature(toHash.toArray(new String[0])); 
		request.setSignature(signature);
		
		instance.register(request);
		
		WriteReq writeRequest = new WriteReq();
		writeRequest.setSender("client1");
		writeRequest.setDestination("server1");
		writeRequest.setSeqNumber(0);
		
		List<String> announcementList = new ArrayList<String>();
		
		AnnouncementMessage post = new AnnouncementMessage();
		post.setWriter("client1");
		post.setMessage("test");
		post.getAnnouncementList().addAll(announcementList);
		post.setWts(0);
		post.setType("Personal");
		
		List<String> messHash = AnnouncementTools.postToSign(post, false);
		
		String messSig = CryptoTools.makeSignature(messHash.toArray(new String[0]));
		
		post.setSignature(messSig);
		writeRequest.setAnnouncement(post);
		
		toHash = new ArrayList<>();
		toHash.add("client1");
		toHash.add("server1");
		toHash.add(String.valueOf(0));
		toHash.addAll(AnnouncementTools.postToSign(post, true));
		
		signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
		
		writeRequest.setSignature(signature);
		
		// Intentionally passing the same message
		instance.post(writeRequest);
		instance.post(writeRequest);
	}
	
	@After
	public void cleanup() {
		AnnouncementServer.getInstance().clean();
	}
	
	
}