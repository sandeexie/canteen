package com.github.canteen.security.kerberos.algorithm;

/**
 * 加密算法
 * @author xyf
 */
public enum EncodeAlgorithm {
	// 对称加密算法
	DES,
	DES3,
	AES,
	// 非对称加密算法
	RSA,
	DSA,
	// 签名算法
	SHA1,
	MD5
}
