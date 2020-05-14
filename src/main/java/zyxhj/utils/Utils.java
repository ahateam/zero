package zyxhj.utils;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;

public class Utils {

	// 修复fileName，兼容linux和windows
	private static String fixSeparator(String str) {
		// windows是\，而linux是/
		String spr = System.getProperty("file.separator");
		if (spr.equals("/")) {
			return StringUtils.replaceChars(str, '\\', '/');
		} else {
			return StringUtils.replaceChars(str, '/', '\\');
		}
	}

	public static Properties readProperties(String fileName) throws Exception {
		Properties p = new Properties();
		String ciname = StringUtils.join(fileName);
		ciname = fixSeparator(ciname);
		p.load(new FileInputStream(ciname));
		return p;
	}

	public static String readProp(String key, Properties p) throws ServerException {
		String ret = p.getProperty(key);
		if (StringUtils.isBlank(ret)) {
			throw new ServerException(BaseRC.SERVER_ERROR, StringUtils.join("***配置文件读取错误>", key));
		} else {
			return ret;
		}
	}

}
