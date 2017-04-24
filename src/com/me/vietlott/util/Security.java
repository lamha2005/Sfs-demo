package com.me.vietlott.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * @author LamHa
 *
 */
public class Security {

	public static String encryptMD5(String key) throws NoSuchAlgorithmException {
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.reset();
		m.update(key.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1, digest);
		String hashtext = bigInt.toString(16);

		// Now we need to zero pad it if you actually want the full 32 chars.
		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}

		return hashtext;
	}


	public static String genKeyByWebDefinition(String accessToken, long moboId) throws NoSuchAlgorithmException {
		String baseStr = Base64.encodeBase64String((accessToken + moboId).getBytes());
		String hashAccessToken = encryptMD5(baseStr);
		return hashAccessToken.substring(0, 10);
	}
}