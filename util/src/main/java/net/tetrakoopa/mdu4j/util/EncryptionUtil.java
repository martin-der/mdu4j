package net.tetrakoopa.mdu4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

public class EncryptionUtil {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		// Security.addProvider(new BouncyCastleProvider());

		KeyPair keyPair = createRSAKeys();
		String text = "this is the input text";
		byte[] encripted;
		System.out.println("input:\n" + text);
		encripted = encrypt(keyPair.getPublic(), text);
		System.out.println("cipher:\n" + encripted);
		System.out.println("decrypt:\n" + decrypt(keyPair.getPrivate(), encripted));
	}


	public static byte[] encrypt(Key publickey, String text) {
		Cipher rsa;
		try {
			rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.ENCRYPT_MODE, publickey);
			return rsa.doFinal(text.getBytes());
		} catch (Exception e) {
			throw new IllegalStateException("encrypt failed !!!");

		}
	}

	private static byte[] decrypt(Key privateKey, byte[] buffer) {
		Cipher rsa;
		try {
			rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.DECRYPT_MODE, privateKey);
			return rsa.doFinal(buffer);
		} catch (Exception e) {
			throw new IllegalStateException("decrypt failed !!!");
		}
	}

	public static KeyPair createRSAKeys() throws NoSuchAlgorithmException, InvalidKeySpecException {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		KeyPair kp = kpg.genKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();

		return new KeyPair(publicKey, privateKey);
	}

	public static Certificate load(InputStream inputPublicKey, InputStream inputKeys) throws CertificateException {
		CertificateFactory factory = null;
		Certificate certificate = null;
		factory = CertificateFactory.getInstance("X.509");
		certificate = factory.generateCertificate(inputPublicKey);
		return certificate;
	}

	/**
	 * if <code>java.security.spec.InvalidKeySpecException</code> is thrown<br/>
	 * try to convert the private key to PKCS8 format using following command<br/>
	 * <code>
	 * openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key_file  -nocrypt > pkcs8_key
	 * </code>
	 */
	public static KeyPair loadKeyPair(InputStream input) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		byte[] privKeyBytes = IOUtil.read(input);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		KeySpec ks = new PKCS8EncodedKeySpec(privKeyBytes);

		RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(ks);
		RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(ks);

		return new KeyPair(publicKey, privateKey);

	}
}
