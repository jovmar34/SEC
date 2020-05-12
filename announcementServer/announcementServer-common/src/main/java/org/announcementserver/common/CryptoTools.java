package org.announcementserver.common;

import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;

/*
* Cryptographic Tools
*/

public class CryptoTools {
	
	private static final String KEYSTORE_FILE_PATH = "src/main/resources/";
	private static final String PASSWORD_FILENAME = "announcement.properties";
	
	public static KeyStore getKeystore(String id, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		File keystoreResource = new File(KEYSTORE_FILE_PATH + "keystores/" + id + ".p12");
		InputStream keyStoreIS = new FileInputStream(keystoreResource);
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(keyStoreIS, password.toCharArray());
		return keyStore;
	}

	private static String getPassword(String id) throws IOException {
		Properties passwordProps = new Properties();
		File passwordResource = new File(KEYSTORE_FILE_PATH + PASSWORD_FILENAME);
		InputStream passwordIS = new FileInputStream(passwordResource);
		passwordProps.load(passwordIS);
		String password = passwordProps.getProperty(id + "-password");
		return password;
	}
	
	public static PublicKey getPublicKey(String clientId) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, IOException, CertificateException {
		
		String password = getPassword(clientId);
		KeyStore keyStore = getKeystore(clientId, password);
		KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(clientId, new KeyStore.PasswordProtection(password.toCharArray()));
		
		RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
		return publicKey;
	}
	
	public static PrivateKey getPrivateKey(String clientId) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, IOException, CertificateException {
		
		String password = getPassword(clientId);
		KeyStore keyStore = getKeystore(clientId, password);
		KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(clientId, new KeyStore.PasswordProtection(password.toCharArray()));
		
		RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
		return privateKey;
	}
	
	public static String publicKeyAsString(PublicKey publicKey) {
		return Base64.getEncoder().encodeToString(publicKey.getEncoded());
	}
	
	public static String privateKeyAsString(PrivateKey privateKey) {
		return Base64.getEncoder().encodeToString(privateKey.getEncoded());
	}
	
	// --- Auxiliary functions -------------
	
	public static String makeHash(String... args) 
			throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, CertificateException, IOException {
		MessageDigest hashFunc = MessageDigest.getInstance("SHA-256");
		
		for (String arg: args) {
			hashFunc.update(arg.getBytes());
		}
		
		byte[] hash = hashFunc.digest();
		
		return byteToString(hash);
	}
	
	public static boolean checkHash(String... ret) 
			throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, CertificateException, IOException {
		String[] response = Arrays.copyOfRange(ret, 0, ret.length - 1);
		String hash = ret[ret.length - 1];
		
		String test = makeHash(response);
		
		if (test.equals(hash)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String makeSignature(String... args) 
			throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, 
				IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnrecoverableEntryException {
		
		PrivateKey privKey = getPrivateKey(args[0]);
		
		String hash = makeHash(args);
		
		byte[] bytes = stringToByte(hash);
			    
	    Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privKey);
		
		cipher.update(bytes);
		
		return byteToString(cipher.doFinal());		
	}
	
	public static String decryptSignature(String src, String signature) 
			throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, 
				IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnrecoverableEntryException {
		PublicKey pubKey = getPublicKey(src);
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, pubKey);
		
		cipher.update(stringToByte(signature));
		
		byte[] clearText = cipher.doFinal();		
		
		return byteToString(clearText);
	}
	
	private static String byteToString(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	private static byte[] stringToByte(String str) {
		return Base64.getDecoder().decode(str);
	}
}