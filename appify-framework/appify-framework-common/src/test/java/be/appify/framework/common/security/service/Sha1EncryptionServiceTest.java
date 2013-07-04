package be.appify.framework.common.security.service;

public class Sha1EncryptionServiceTest {

	public static void main(String[] args) {
		Sha1EncryptionService encryptionService = new Sha1EncryptionService("salt", "UTF-8");
		System.out.println(encryptionService.encrypt("password"));
	}

}
