package org.announcementServer.ws;

import org.announcementserver.ws.AnnouncementServer;
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

import javax.crypto.NoSuchPaddingException;


public class CryptoTest {
	AnnouncementServer instance;
	
	@Before
	public void start() {
		instance = AnnouncementServer.getInstance();
	}
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	/**
	 * -- Test Description -- 
	 * This test aims to prove that our system can
	 * detect a tampered message. It works by intentionally
	 * passing wrong arguments to hash (in order to break the hash)
	 * and expects to get a RunTimeException
	 */
	
	/*
	@Test
	public void testWithBadHashGoodSignature() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
		
		exceptionRule.expect(RuntimeException.class);
		exceptionRule.expectMessage("Error: Possible tampering detected on Hash");
		
		// Get publicKey of client1 from keystore
		String publicKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey("client1"));
		
		// Get a correct signature with a wrong hash
		List<String> toHash = new ArrayList<>();
		toHash.add("client1"); // Needed to allow retrieval of privateKey from keystore to sign
		toHash.add("a wrong hash");
		String signature = CryptoTools.makeSignature(toHash.toArray(new String[0]));
		
		instance.register(publicKey, signature);
	}

	*/
	
	/**
	 * -- Test Description -- 
	 * This test aims to prove that our system can
	 * detect a tampered message. It works by intentionally
	 * passing a message with a wrong signature (in order to break the signature)
	 * and expects to get a RunTimeException
	 */
	
	/*
	@Test
	public void testWithGoodHashBadSignature() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
		
		exceptionRule.expect(RuntimeException.class);
		exceptionRule.expectMessage("Error: Possible tampering detected on Signature");
		
		// Get publicKey of client1 from keystore
		String publicKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey("client1"));
		
		// Get a bad signature
		String signature = "a bad bad signature";
		
		instance.register(publicKey, signature);
	}
	
	*/

	/**
	 * -- Test Description -- 
	 * This test aims to prove that our system is
	 * working, hash and signature wise, when
	 * we pass correct arguments.
	 */
	

	/*
	@Test
	public void testWithGoodHashGoodSignature() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
		
		// Get publicKey of client1 from keystore
		String publicKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey("client1"));
		
		// Get a correct signature with a good hash
		List<String> toHash1 = new ArrayList<>();
		toHash1.add("client1");
		toHash1.add("server");
		toHash1.add(publicKey);
		String signature = CryptoTools.makeSignature(toHash1.toArray(new String[0]));
		
		// Getting the response given in the correct case
		List<String> toHash2 = new ArrayList<>();
		toHash2.add("server");
		toHash2.add("client1");
		toHash2.add("Welcome new user!");

		List<String> response = new ArrayList<>();
		response.add("Welcome new user!");
		response.add(CryptoTools.makeSignature(toHash2.toArray(new String[0])));
		
		Assert.assertEquals(response, instance.register(publicKey, signature));
	}
	*/

	/**
	 * -- Test Description -- 
	 * This test aims to prove that our system is
	 * working, sequence number wise, when
	 * we pass correct arguments.
	 */
	
	/*
	@Test
	public void testGoodSequenceNumber() 
		throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, UserNotRegisteredException, MessageSizeException, ReferredUserException, PostTypeException, ReferredAnnouncementException {
		
		// Get publicKey of client1 from keystore
		String publicKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey("client1"));
		
		// Get a correct signature with a good hash
		List<String> toHash1 = new ArrayList<>();
		toHash1.add("client1");
		toHash1.add("server");
		toHash1.add(publicKey);
		String signature = CryptoTools.makeSignature(toHash1.toArray(new String[0]));
		
		instance.register(publicKey, signature);
		
		// The hash for the post request
		List<String> toHash3 = new ArrayList<>();
		toHash3.add("client1");
		toHash3.add("server");
		toHash3.add(String.valueOf(0));
		toHash3.add(publicKey);
		toHash3.add("Hello World");
		toHash3.addAll(new ArrayList<>());
		signature = CryptoTools.makeSignature(toHash3.toArray(new String[0]));
		
		// The hash in the response		
		List<String> toHash4 = new ArrayList<>();
		toHash4.add("server");
		toHash4.add("client1");
		toHash4.add(String.valueOf(0));
		toHash4.add("Success your post was posted!");
		
		List<String> response = new ArrayList<>();
		response.add("Success your post was posted!");
		response.add(CryptoTools.makeSignature(toHash4.toArray(new String[0])));
		
		Assert.assertEquals(response, instance.post(publicKey, "Hello World", new ArrayList<>(), signature));
	}
	*/

	/**
	 * -- Test Description -- 
	 * This test aims to prove that our system can
	 * detect a replayed message. It works by intentionally
	 * passing the same message twice (with the same sequence number)
	 * and expects to get a RunTimeException
	 */
	
	/*
	@Test
	public void testBadSequenceNumber() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, UserNotRegisteredException, MessageSizeException, ReferredUserException, PostTypeException, ReferredAnnouncementException {
		
		exceptionRule.expect(RuntimeException.class);
		exceptionRule.expectMessage("Error: Possible drop/replay detected");
		
		// Get publicKey of client1 from keystore
		String publicKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey("client1"));
		
		// Get a correct signature with a good hash
		List<String> toHash1 = new ArrayList<>();
		toHash1.add("client1");
		toHash1.add("server");
		toHash1.add(publicKey);
		String signature = CryptoTools.makeSignature(toHash1.toArray(new String[0]));
		
		instance.register(publicKey, signature);
		
		// The hash for the post request
		List<String> toHash3 = new ArrayList<>();
		toHash3.add("client1");
		toHash3.add("server");
		toHash3.add(String.valueOf(0));
		toHash3.add(publicKey);
		toHash3.add("Hello World");
		toHash3.addAll(new ArrayList<>());
		signature = CryptoTools.makeSignature(toHash3.toArray(new String[0]));
		
		// Intentionally passing the same message
		instance.post(publicKey, "Hello World", new ArrayList<>(), signature);
		instance.post(publicKey, "Hello World", new ArrayList<>(), signature);
	}
	*/
	
	@After
	public void cleanup() {
		instance.clean();
	}
	
	
}