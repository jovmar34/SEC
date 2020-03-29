package org.announcementserver.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/*
* Cryptographic tools
*/

public class CryptoTools {
	
	public static String getPublicKeyAsString(String filepath) throws IOException {
    	byte[] keyBytes = Files.readAllBytes(Paths.get(filepath));
    	X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    	//KeyFactory kf = KeyFactory.getInstance("RSA");
    	//PublicKey pk = kf.generatePublic(spec);
		
		return Base64.getEncoder().encodeToString(spec.getEncoded());
	}
	
	public static PrivateKey getPrivKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException  {
	    byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    return kf.generatePrivate(spec);
	}
	
	public static PublicKey getPubKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
		
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
	
	public static String privKeyAsString(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		return Base64.getEncoder().encodeToString(getPrivKey(filename).getEncoded());
	}
	
	public static String pubKeyAsString(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		return Base64.getEncoder().encodeToString(getPubKey(filename).getEncoded());
	}
}