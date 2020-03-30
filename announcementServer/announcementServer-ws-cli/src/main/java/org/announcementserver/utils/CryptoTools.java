package org.announcementserver.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Properties;

/*
* Cryptographic tools
*/

public class CryptoTools {
	
	private static final String KEYSTORE_FILE_PATH = "src/main/resources/";
	private static final String KEYSTORE_FILENAME = "announcement.jks";
	private static final String PASSWORD_FILENAME = "announcement.properties";
	
	public static Keystore getKeystore(String password) {
		ClassPathResource keystoreResource = new ClassPathResource(KEYSTORE_FILENAME);
		InputStream keystoreIS = keystoreResource.getInputStream();
		KeyStore keyStore = keyStore.getInstance("PKCS12");
		keyStore.load(keyStoreIS, password.toCharArray());
		return keyStore;
	}
	
	private static String getPassword() {
		Properties passwordProps = new Properties();
		ClassPathResource passwordResource = new ClassPathResource(PASSWORD_FILENAME);
		InputStream passwordIS = passwordResource.getInputStream();
		passwordProps.load(passwordIS);
		String password = passwordProps.getProperty("keystore-password");
		return password;
	}
	
	public static PublicKey getPublicKey(String clientId) {
		
		String password = getPassword();
		KeyStore keyStore = getKeystore(getPassword());
		KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(id, new KeyStore.PasswordProtection(password.toCharArray()));
		
		RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
		return publicKey;
	}
	
	public static PrivateKey getPrivateKey(String clientId) {
		
		String password = getPassword();
		KeyStore keyStore = getKeystore(getPassword());
		KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(id, new KeyStore.PasswordProtection(password.toCharArray()));
		
		RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
		return privateKey;
	}
	
	public static String publicKeyAsString(PublicKey publicKey) {
		return Base64.getEncoder().encodeToString(publicKey.getEncoded());
	}
	
	public static String privateKeyAsString(PrivateKey privateKey) {
		return Base64.getEncoder().encodeToString(privateKey.getEncoded())
	}
	
	// ---- Old Stuff --------------------------------------------------------------------------------------
	
	//public static String getPublicKeyAsString(String filepath) throws IOException {
    //	byte[] keyBytes = Files.readAllBytes(Paths.get(filepath));
    //	X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    //  KeyFactory kf = KeyFactory.getInstance("RSA");
    //  PublicKey pk = kf.generatePublic(spec);
		
	//	return Base64.getEncoder().encodeToString(spec.getEncoded());
	//}
	
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