package org.example.fabricjava.controller;


import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 大一统的例子，包括生成公私钥对，加解密字符串。
 * @ClassName: RsaDemo
 */
public class Rsademo {
    private static final String ALGO = "RSA";
    private static final String CHARSET = "UTF-8";

    /*
     * 用于存储随机产生的公钥与私钥
     */
    private static Map<Integer, String> KEY_CACHE = new HashMap<>();

    /**
     * 随机生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    private static void generateKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator 类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGO);
        // 初始化密钥对生成器，密钥大小为 96-1024 位
        keyPairGen.initialize(2048, new SecureRandom());
        // 生成一个密钥对，保存在 keyPair 中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = new String(Base64.getEncoder().encode(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.getEncoder().encode((privateKey.getEncoded())));
        // 将公钥和私钥保存到 Map
        KEY_CACHE.put(0, publicKeyString);
        KEY_CACHE.put(1, privateKeyString);
    }

    /**
     * RSA公钥加密
     *
     * @param data      加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    private static String encrypt(String data, String publicKey) throws Exception {
        // base64 编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(ALGO).generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance(ALGO);
        // 公钥加密
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(CHARSET)));
    }

    /**
     * RSA私钥解密
     *
     * @param data       加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    private static String decrypt(String data, String privateKey) throws Exception {
        byte[] inputByte = Base64.getDecoder().decode(data.getBytes(CHARSET));
        // base64 编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(ALGO).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA 解密
        Cipher cipher = Cipher.getInstance(ALGO);
        // 私钥解密
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

    public static void main(String[] args) {
        String originData = "Test Asymmetric encrypt!";
        System.out.println("originData = " + originData);
        try {
            generateKeyPair();

            String publicKey = KEY_CACHE.get(0);
            System.out.println("publicKey = " + publicKey);

            String encryData = encrypt(originData, publicKey);
            System.out.println("encryData = " + encryData);

            String privateKey = KEY_CACHE.get(1);
            System.out.println("privateKey = " + privateKey);
            String decryData = decrypt(encryData, privateKey);
            System.out.println("decryData = " + decryData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}