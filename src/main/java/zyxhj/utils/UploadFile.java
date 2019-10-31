package zyxhj.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import zyxhj.utils.api.Controller.ENUMVALUE;

/**
 * OSS文件上传工具<br><br>
 * 该版本使用的均为公司ID和SECRET
 * @author JXians
 * @version 1.0.0
 *
 */
public class UploadFile {

	public static String OSSCATALOGUE_ASSET = "asset/";
	public static String OSSCATALOGUE_EXAMINE = "examine/";
	public static String OSSCATALOGUE_PRINT_EXCEL = "print-excel/";
	public static String OSSCATALOGUE_TEMPLATE = "template/";
	public static String OSSCATALOGUE_USER = "user/";
	
	public static String BUCKETNAME_JITI = "jitijingji-test1";//文件存放一级文件夹（自定义默认存放路径）正式服
//	public static String BUCKETNAME_JITI = "jiti-img-test";//文件存放一级文件夹（自定义默认存放路径） 测试服
	
	private static String ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";// endpoint
	private static String ACCESSKEYID = "LTAIJ9mYIjuW54Cj";// accessKeyId
	private static String ACCESSKEYSECRET = "89EMlXLsP13H8mWKIvdr4iM1OvdVxs";// accessKeySecret

	/**
	 * 上传文件到OSS服务器
	 * 
	 * @param OSSBucktName    文件存放根目录 (例：jitijingji-test1)
	 * @param OSSCatalogue 上传到OSS的文件路径（文件存放的文件夹）
	 * @param fileName     文件名（带文件后缀）
	 * @param outputStream 输出流
	 * @param inputStream  输入流 输入流和输出流只能选其一
	 * 
	 * @return 本方法返回文件在OSS的完全路径
	 * @throws Exception
	 */
	public String uploadFileToOSS(String OSSBucktName, String OSSCatalogue, String fileName, InputStream inputStream,
			String... bucketName) throws Exception {

		// 生成上传文件路径
		String filePath = StringUtils.join(OSSCatalogue, fileName);
		// 上传文件到OSS
		OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);
		// 判断文件存放地址是否修改
		if (bucketName != null && bucketName.length > 0) {
			ossClient.putObject(bucketName[0], filePath, inputStream);
		} else {
			ossClient.putObject(OSSBucktName, filePath, inputStream);
		}

		// 关闭OSSClient，关闭输入流。
		ossClient.shutdown();
		inputStream.close();

		String url = "https://" + OSSBucktName + ".oss-cn-hangzhou.aliyuncs.com/" + filePath;
		System.out.println("文件下载路径(完全路径);" + url);
		return url;
	}
}
