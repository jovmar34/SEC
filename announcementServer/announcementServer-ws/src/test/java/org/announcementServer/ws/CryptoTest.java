package org.announcementServer.ws;

import org.announcementserver.ws.AnnouncementServer;
import org.announcementserver.ws.Announcement;
import org.announcementserver.common.*;
import org.junit.*;
import java.util.List;
import java.util.ArrayList;

import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.security.KeyStoreException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import org.announcementserver.exceptions.UserAlreadyRegisteredException;
import javax.crypto.NoSuchPaddingException;


public class CryptoTest {
	AnnouncementServer instance;
	
	@Before
	public void start() {
		instance = AnnouncementServer.getInstance();
	}
	
	@Test
	public void testWithBadHash() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, UserAlreadyRegisteredException {
		// Get publicKey of client1 from keystore
		String publicKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey("client1"));
		
		// Get a correct signature with a wrong hash
		String signature = CryptoTools.makeSignature("client1", "server", "a wrong hash");
		
		// Getting the response given in the wrong hash case
		List<String> response = new ArrayList<>();
		response.add("Error: Wrong Hash!");
		response.add(CryptoTools.makeHash("Error: Wrong Hash!"));
				
		Assert.assertEquals(response, instance.register(publicKey, signature));
	}
	
	@Test
	public void testWithGoodHash() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, UserAlreadyRegisteredException {
		// Get publicKey of client1 from keystore
		String publicKey = CryptoTools.publicKeyAsString(CryptoTools.getPublicKey("client1"));
		
		// Get a correct signature with a good hash
		String signature = CryptoTools.makeSignature("client1", "server",  CryptoTools.makeHash("client1", "server", publicKey));
		
		// Getting the response given in the correct case
		List<String> response = new ArrayList<>();
		response.add("Welcome new user!");
		response.add(CryptoTools.makeSignature("server", "client1", CryptoTools.makeHash("server", "client1", response.get(0))));
		
		Assert.assertEquals(response, instance.register(publicKey, signature));
	}
	
	@After
	public void cleanup() {
		instance.clean();
	}
	
	
}