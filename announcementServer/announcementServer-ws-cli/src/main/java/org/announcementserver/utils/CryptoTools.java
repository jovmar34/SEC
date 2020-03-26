package org.announcementserver.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.X509EncodedKeySpec;
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
	
	
}