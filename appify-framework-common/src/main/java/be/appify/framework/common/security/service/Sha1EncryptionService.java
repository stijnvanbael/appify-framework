package be.appify.framework.common.security.service;

import java.nio.charset.Charset;
import java.security.MessageDigest;

import be.appify.framework.security.service.EncryptionService;

import com.google.api.client.util.Base64;

public class Sha1EncryptionService implements EncryptionService {

	private final String salt;
	private MessageDigest digest;
	private Charset encoding;

	public Sha1EncryptionService(String salt, String encoding) {
		this.salt = salt;
		try {
			this.encoding = Charset.forName(encoding);
			this.digest = MessageDigest.getInstance("SHA-1");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String encrypt(String toEncrypt) {
		byte[] bytesToEncrypt;
		try {
			bytesToEncrypt = (toEncrypt + salt).getBytes(encoding);
			byte[] encryptedBytes = digest.digest(bytesToEncrypt);
			return Base64.encodeBase64String(encryptedBytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
