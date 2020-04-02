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
import org.announcementserver.exceptions.UserAlreadyRegisteredException;
import javax.crypto.NoSuchPaddingException;


public class CryptoTest {
	AnnouncementServer instance;
	
	@Before
	public void start() {
		instance = AnnouncementServer.getInstance();
	}
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Test
	public void testWithBadHash() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, UserAlreadyRegisteredException {
		
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
	
	@Test
	public void testWithGoodHash() throws NoSuchPaddingException, BadPaddingException, CertificateException, IllegalBlockSizeException, InvalidKeyException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, UserAlreadyRegisteredException {
		
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
	
	@After
	public void cleanup() {
		instance.clean();
	}
	
	
}