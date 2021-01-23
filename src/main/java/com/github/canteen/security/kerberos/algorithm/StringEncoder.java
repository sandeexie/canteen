package com.github.canteen.security.kerberos.algorithm;

public class StringEncoder {

	/**
	 * 明文加密
	 * @param plainText 明文
	 * @param algorithm 加密算法
	 * @return 密文
	 */
	public static String encode(String plainText,EncodeAlgorithm algorithm){
		String cypherText = null;
		switch (algorithm){
			case AES:
				cypherText=encodeWithAES(plainText);
			break;

			case DES:
				cypherText=encodeWithDES(plainText);
			break;

			case DSA:
				cypherText=encodeWithDSA(plainText);
			break;

			case MD5:
				cypherText=encodeWithMD5(plainText);
			break;

			case RSA:
				cypherText=encodeWithRSA(plainText);
			break;

			case DES3:
				cypherText=encodeWithDES3(plainText);
			break;

			case SHA1:
				cypherText=encodeWithSHA1(plainText);
			break;
		}
		return cypherText;
	}

	// TODO 实现MD5加密算法
	private static String encodeWithMD5(String plainText) {
		return null;
	}

	// TODO 实现DSA加密算法
	private static String encodeWithDSA(String plainText) {
		return null;
	}

	// TODO 实现RSA加密算法
	private static String encodeWithRSA(String plainText) {
		return null;
	}

	// TODO 实现DES3加密算法
	private static String encodeWithDES3(String plainText) {
		return null;
	}

	// TODO 实现SHA1加密算法
	private static String encodeWithSHA1(String plainText) {
		return null;
	}

	// TODO 实现DES加密
	private static String encodeWithDES(String plainText) {
		return null;
	}

	// TODO 实现AES加密
	private static String encodeWithAES(String plainText) {
		return null;
	}

}
