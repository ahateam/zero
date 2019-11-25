package zyxhj.util;

import java.io.File;

import org.junit.Test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;

public class ExcelUtilsTest {

	@Test
	public void simpleRead() {
	    // 写法1：
	    String fileName = "C:/Users/Admin/Desktop/新建 XLS 工作表.xls";

	    ExcelReader excelReader = EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).build();
	    ReadSheet readSheet = EasyExcel.readSheet(0).build();
	    excelReader.read(readSheet);
	    // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
	    excelReader.finish();
	}
}
