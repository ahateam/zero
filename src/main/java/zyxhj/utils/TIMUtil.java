package zyxhj.utils;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.nio.charset.Charset;

import java.util.Arrays;
import java.util.zip.Deflater;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSONObject;

/**
 * 腾讯云IM，UserSig生成算法（腾讯云提供）
 * 
 * @author JXians
 * 
 */
public class TIMUtil {

	private static long sdkappid = 1400277699;
	private static String key = "281d8830bb13fa0923bc5c34cc13690faac214906fd5342782daba5a3e645f3c";
	private static final Long EXPIRETIME = 604800000L;
	

	public TIMUtil(long sdkappid, String key) {
		this.sdkappid = sdkappid;
		this.key = key;
	}

	private static String hmacsha256(String identifier, long currTime, long expire, String base64Userbuf) {
		String contentToBeSigned = "TLS.identifier:" + identifier + "\n" + "TLS.sdkappid:" + sdkappid + "\n"
				+ "TLS.time:" + currTime + "\n" + "TLS.expire:" + expire + "\n";
		if (null != base64Userbuf) {
			contentToBeSigned += "TLS.userbuf:" + base64Userbuf + "\n";
		}
		try {
			byte[] byteKey = key.getBytes("UTF-8");
			Mac hmac = Mac.getInstance("HmacSHA256");
			SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
			hmac.init(keySpec);
			byte[] byteSig = hmac.doFinal(contentToBeSigned.getBytes("UTF-8"));
			return (new BASE64Encoder().encode(byteSig)).replaceAll("\\s*", "");
		} catch (UnsupportedEncodingException e) {
			return "";
		} catch (NoSuchAlgorithmException e) {
			return "";
		} catch (InvalidKeyException e) {
			return "";
		}
	}

	private static String genSig(String identifier, long expire, byte[] userbuf) {

		long currTime = System.currentTimeMillis() / 1000;

		JSONObject sigDoc = new JSONObject();
		sigDoc.put("TLS.ver", "2.0");
		sigDoc.put("TLS.identifier", identifier);
		sigDoc.put("TLS.sdkappid", sdkappid);
		sigDoc.put("TLS.expire", expire);
		sigDoc.put("TLS.time", currTime);

		String base64UserBuf = null;
		if (null != userbuf) {
			base64UserBuf = new BASE64Encoder().encode(userbuf);
			sigDoc.put("TLS.userbuf", base64UserBuf);
		}
		String sig = hmacsha256(identifier, currTime, expire, base64UserBuf);
		if (sig.length() == 0) {
			return "";
		}
		sigDoc.put("TLS.sig", sig);
		Deflater compressor = new Deflater();
		compressor.setInput(sigDoc.toString().getBytes(Charset.forName("UTF-8")));
		compressor.finish();
		byte[] compressedBytes = new byte[2048];
		int compressedBytesLength = compressor.deflate(compressedBytes);
		compressor.end();
		return (new String(Base64URL.base64EncodeUrl(Arrays.copyOfRange(compressedBytes, 0, compressedBytesLength))))
				.replaceAll("\\s*", "");
	}

	public static String genSig(String identifier) {
		return genSig(identifier, EXPIRETIME, null);
	}

	public String genSigWithUserBuf(String identifier, byte[] userbuf) {
		return genSig(identifier, EXPIRETIME, userbuf);
	}

}
