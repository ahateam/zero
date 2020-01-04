package zyxhj.util;

import java.io.*;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;

public class ExcelUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

	
	private static Sheet initSheet;

	   static {
	      initSheet = new Sheet(1, 0);
	      initSheet.setSheetName("sheet");
	      //设置自适应宽度
	      initSheet.setAutoWidth(Boolean.TRUE);
	   }
	
	public static List<Object> readMoreThan1000RowBySheet(String filePath, Sheet sheet){
	      if(StringUtils.isBlank(filePath)){
	         return null;
	      }

	      sheet = sheet != null ? sheet : initSheet;

	      InputStream fileStream = null;
	      try {
	         fileStream = new FileInputStream(filePath);
	         DemoDataListener excelListener = new DemoDataListener();
	         EasyExcelFactory.readBySax(fileStream, sheet, excelListener);
//	         return excelListener.getDatas();
	      } catch (FileNotFoundException e) {
	    	  LOGGER.error("找不到文件或文件路径错误, 文件：{}", filePath);
	      }finally {
	         try {
	            if(fileStream != null){
	               fileStream.close();
	            }
	         } catch (IOException e) {
	        	 LOGGER.error("excel文件读取失败, 失败原因：{}", e);
	         }
	      }
	      return null;
	   }
}
