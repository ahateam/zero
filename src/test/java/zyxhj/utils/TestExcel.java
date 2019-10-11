package zyxhj.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

public class TestExcel {

	
	@Test
	public void testWriteExcel1() {
		List<TestExcelModel> tlist = new ArrayList<TestExcelModel>();
		for(int i = 0; i<5; i++) {
			TestExcelModel t = new TestExcelModel();
			t.setName("testst"+i);
			t.setAge(20);
			t.setSex("男");
			t.setMobile("18275422377");
			tlist.add(t);
		}
		// 写法1
        String fileName = "C:\\Users\\Admin\\Desktop\\1.xlsx";
        // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, TestExcelModel.class).sheet("模板").doWrite(tlist);
	}
	
	@Test
	public void testRedExcel1() {
		// 写法1：
        String fileName = "C:\\Users\\Admin\\Desktop\\1.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, TestExcelModel.class, new ExcelDataListener()).sheet().doRead();
		
	}
	
}
