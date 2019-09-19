package zyxhj.utils;


import java.nio.charset.Charset;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

import me.chanjar.weixin.common.util.crypto.PKCS7Encoder;
public class AESUtils {
    private static Charset CHARSET = Charset.forName("utf-8");
    /**
     * 对密文进行解密
     *
     * @param text 需要解密的密文
     *
     * @return 解密得到的明文
     *
     * @throws TpException 异常错误信息
     */
    public String decrypt(String text, String sessionKey)
            throws Exception {
        byte [] aesKey = Base64.decodeBase64(sessionKey + "=");
        byte[] original;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] encrypted = Base64.decodeBase64(text);
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new Exception(e);
        }
        String xmlContent;
        String fromClientId;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);
            // 分离16位随机字符串,网络字节序和ClientId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
            int xmlLength = recoverNetworkBytesOrder(networkOrder);
            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            fromClientId = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), CHARSET);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return xmlContent;
    }
    /**
     * 还原4个字节的网络字节序
     *
     * @param orderBytes 字节码
     *
     * @return sourceNumber
     */
    private int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        int length = 4;
        int number = 8;
        for (int i = 0; i < length; i++) {
            sourceNumber <<= number;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }
    /**
     * 加密机密demo
     * @param args
     */
//    public static void main(String[] args) {
//        String dy = "PacInBMsdSKCQL+QkezEYQFiE3cref+U8btpW6ali2kNzJu3xAiiabOX1eOyMLty7aTBGI0JvgL6pgq5O/DLztl2aT+t6cJfmb7R68QgEZcZhdURVuLtLEGcLjqPG9Fz9QgKbUDjuhAz27IEd5IDkmtvuZSTkYEbBHG+lc4LIzNiIVazkI/8sEYExd1HU9LhYiGrrP7EqGYJks9j3qJnn8yiwQdqWKWl1Y9Wc0c17laI3H9tg349Y62CTY8nI3rfZjc/kKVhAKKjmXeaorSEX/sCZOmh0AQiKN3v9g4pXjNKsPFe62hCLJakyHdaXN4fCyqVXbTxMkBJekErPohNy9swwzmQ0R4MVF4oFtQ+pI9seBeZtGnO+UpZrt/mdXZP";
//
//        String sessionKey = "1df09d0a1677dd72b8325aec59576e0c";
//
//        AESUtils demo = new AESUtils();
//        String dd;
//		try {
//			dd = demo.decrypt(dy, sessionKey);
//			System.out.println(dd);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
}