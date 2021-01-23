package com.github.canteen.security.kerberos.algorithm;

public class StringDecoder {

	public static String decode(String cypherText,DecodeAlgorithm algorithm){
		String plainText=null;
		switch (algorithm){
			case DES:
				plainText=decodeWithDES(cypherText);
			break;
			case DES3:
				plainText=decodeWithDES3(cypherText);
			break;
			case AES:
				plainText=decodeWithAES(cypherText);
			break;
			case DSA:
				plainText=decodeWithDSA(cypherText);
			break;
			case RSA:
				plainText=decodeWithRSA(cypherText);
			break;
		}
		return plainText;
	}

	// TODO DES解密
	private static String decodeWithDES(String cypherText) {
		return null;
	}

	// TODO DES3解密
	private static String decodeWithDES3(String cypherText) {
		return null;
	}

	// TODO AES解密
	private static String decodeWithAES(String cypherText) {
		return null;
	}

	// TODO DSA解密
	private static String decodeWithDSA(String cypherText) {
		return null;
	}

	// TODO RSA解密
	private static String decodeWithRSA(String cypherText) {
		return null;
	}
}
