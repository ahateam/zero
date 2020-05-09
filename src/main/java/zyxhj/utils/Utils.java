package zyxhj.utils;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

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
	
	
}
