package common.util.tools;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Base64Utils;

/**
 * 加密工具类(MD5, AES, RSA)
 * 
 * @author jieli
 *
 */
public class EncryptUtils {
	/**
	 * MD5加密字符串(Base64)
	 * 
	 * @param content
	 *            待加密的字符串
	 * @return
	 */
	public static String md5Base64(String content) {
		CheckUtils.checkNotNull(content, "str can NOT be empty or null");
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digest = md5.digest(content.getBytes("UTF-8"));
			return Base64Utils.encodeToString(digest);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * MD5加密字符串(Hex)
	 * 
	 * @param content
	 *            待加密的字符串
	 * @return
	 * @throws CommonException
	 */
	public static String md5Hex(String content) {
		return DigestUtils.md5Hex(content);
	}

	/**
	 * AES加密(Base64)防止数据泄露
	 * 
	 * @param content
	 *            待加密的字符串
	 * @param key
	 *            AES密钥
	 * @return
	 * @throws Exception
	 */
	public static String encryptAES(String content, String key) throws Exception {
		// FIX: Cannot find any provider supporting AES/ECB/PKCS5Padding
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, initSecretKey(key));
		byte[] encryptedBytes = cipher.doFinal(content.getBytes("UTF-8"));
		return Base64Utils.encodeToString(encryptedBytes);
	}

	private static SecretKeySpec initSecretKey(String key) throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(key.getBytes());
		keyGenerator.init(128, secureRandom); // AES密钥长度为128
		SecretKey secretKey = keyGenerator.generateKey();
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
		return secretKeySpec;
	}

	/**
	 * AES解密
	 * 
	 * @param content
	 *            加密的字符串(Base64)
	 * @param key
	 *            AES密钥
	 * @return
	 * @throws Exception
	 */
	public static String decryptAES(String content, String key) throws Exception {
		// FIX: Cannot find any provider supporting AES/ECB/PKCS5Padding
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, initSecretKey(key));
		byte[] encryptedBytes = cipher.doFinal(Base64Utils.decodeFromString(content));
		return new String(encryptedBytes, "UTF-8");
	}

	/** RSA公钥key */
	public static final String RSA_PUBLIC_KEY = "PUBLIC_KEY";
	/** RSA私钥key */
	public static final String RSA_PRIVATE_KEY = "PRIVATE_KEY";

	/**
	 * 生成RSA公私钥对(Base64)，密钥格式PKCS8
	 * 
	 * @param keysize
	 *            密钥长度：1024, 2048
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> keyPairRSA(Integer keysize) throws Exception {
		// 生成RSA Key
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(keysize);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// Key --> Base64 (require Apache Commons Codec or Java8)
		String publicKeyBase64 = Base64Utils.encodeToString(publicKey.getEncoded());
		String privateKeyBase64 = Base64Utils.encodeToString(privateKey.getEncoded());

		Map<String, String> ret = new HashMap<String, String>();
		ret.put(RSA_PUBLIC_KEY, publicKeyBase64);
		ret.put(RSA_PRIVATE_KEY, privateKeyBase64);
		return ret;
	}

	/**
	 * RSA私钥签名防止数据篡改，签名方式SHA1withRSA
	 * 
	 * @param content
	 *            待签名的字节数组
	 * @param privateKeyBase64
	 *            私钥(Base64)
	 * @return
	 * @throws Exception
	 */
	public static byte[] signRSA(byte[] content, String privateKeyBase64) throws Exception {
		// Base64 --> Key
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(privateKeyBase64));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		// Sign
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initSign(privateKey);
		signature.update(content);
		return signature.sign();
	}

	/**
	 * RSA公钥验签
	 * 
	 * @param content
	 *            待签名的字节数组
	 * @param publicKeyBase64
	 *            公钥(Base64)
	 * @param signed
	 *            RSA私钥签名的字节数组
	 * @return 验签结果
	 * @throws Exception
	 */
	public static boolean verifyRSA(byte[] content, String publicKeyBase64, byte[] signed) throws Exception {
		// Base64 --> Key
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Utils.decodeFromString(publicKeyBase64));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		// verify
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initVerify(publicKey);
		signature.update(content);
		return signature.verify(signed);
	}

	// === Quick Start ===
	public static void main(String[] args) {
		try {
			String content = "待加密数据";
			String keyAES = "@jie";
			String encryptAES = EncryptUtils.encryptAES(content, keyAES);
			System.out.println("AES加密防止数据泄露: " + encryptAES);
			String decryptAES = EncryptUtils.decryptAES(encryptAES, keyAES);
			System.out.println("AES解密: " + decryptAES);

			Map<String, String> keyPairRSA = EncryptUtils.keyPairRSA(1024);
			byte[] signRSA = EncryptUtils.signRSA(content.getBytes(), keyPairRSA.get(EncryptUtils.RSA_PRIVATE_KEY));
			System.out.println("RSA私钥签名防止数据篡改: " + signRSA);
			System.out.println("RSA公钥验签: "
					+ EncryptUtils.verifyRSA(content.getBytes(), keyPairRSA.get(EncryptUtils.RSA_PUBLIC_KEY), signRSA));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
