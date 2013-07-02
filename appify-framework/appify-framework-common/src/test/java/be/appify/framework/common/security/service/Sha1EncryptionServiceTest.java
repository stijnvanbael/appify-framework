package be.appify.framework.common.security.service;

public class Sha1EncryptionServiceTest {

	public static void main(String[] args) {
		Sha1EncryptionService encryptionService = new Sha1EncryptionService("imagine512", "UTF-8");
		System.out.println(encryptionService.encrypt("tim.coremans"));
		System.out.println(encryptionService.encrypt("stijn.van.bael"));
		System.out.println(encryptionService.encrypt("pieter.van.bael"));
		System.out.println(encryptionService.encrypt("geert.van.linden"));
	}

}
