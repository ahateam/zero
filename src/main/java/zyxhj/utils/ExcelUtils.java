package zyxhj.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

public class ExcelUtils {

	private static final String EXCEL_XLS = "xls";
	private static final String EXCEL_XLSX = "xlsx";

	private static List<List<Object>> readxxx(Workbook workbook, int skipRowCount, int colCount, int sheetIndex) {
		// 目前只读取第一个sheet
		Sheet sheet = workbook.getSheetAt(sheetIndex); // 遍历第几个Sheet
		int count = 0;

		List<List<Object>> ret = new ArrayList<>();

		for (Row row : sheet) {
			try {
				// 跳过表头
				if (count < skipRowCount) {
					count++;
					continue;
				}
				// 如果当前行没有数据，跳出循环
				if (row.getCell(0) == null || StringUtils.isBlank(row.getCell(0).toString())) {
					break;
				}

				ArrayList<Object> objs = new ArrayList<>();
				for (int i = 0; i < colCount; i++) {
					Cell cell = row.getCell(i);
					if (cell == null) {
						System.out.print("null" + "\t");
						objs.add(null);
					} else {
						Object obj = getValue(cell);
						objs.add(obj);
						System.out.print(obj + "\t");
					}
				}

				System.out.println();

				ret.add(objs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	private static Object getValue(Cell cell) {
		Object obj = null;
		switch (cell.getCellType()) {
		case BOOLEAN:
			obj = cell.getBooleanCellValue();
			break;
		case ERROR:
			obj = cell.getErrorCellValue();
			break;
		case NUMERIC:
			// obj = cell.getNumericCellValue();
			DecimalFormat df = new DecimalFormat("0");
			obj = df.format(cell.getNumericCellValue());
			break;
		case STRING:
			obj = cell.getStringCellValue();
			break;
		default:
			break;
		}
		return obj;
	}

	public static List<List<Object>> readExcelOnline(String excelUrl, int skipRowCount, int colCount, int sheetIndex)
			throws Exception {

		URL url = new URL(excelUrl);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3 * 60 * 1000);
		InputStream is = conn.getInputStream();

		Workbook workbook = ExcelUtils.getWorkbook(is, excelUrl);

		List<List<Object>> ret = readxxx(workbook, skipRowCount, colCount, sheetIndex);

		is.close();

		return ret;
	}

	public static List<List<Object>> readExcelFile(String fileName, int skipRowCount, int colCount, int sheetIndex)
			throws Exception {
		File excelFile = new File(fileName);
		ExcelUtils.checkExcel(excelFile);
		FileInputStream in = new FileInputStream(excelFile); // 文件流
		Workbook workbook = ExcelUtils.getWorkbook(in, excelFile);

		List<List<Object>> ret = readxxx(workbook, skipRowCount, colCount, sheetIndex);

		in.close();

		return ret;
	}

	private static void checkExcel(File file) throws Exception {
		if (!file.exists()) {
			throw new Exception("文件不存在");
		}
		if (!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))) {
			throw new Exception("文件不是Excel");
		}
	}

	private static Workbook getWorkbook(InputStream in, File file) throws IOException {
		Workbook wb = null;
		if (file.getName().endsWith(EXCEL_XLS)) { // Excel 2003
			wb = new HSSFWorkbook(in);
		} else if (file.getName().endsWith(EXCEL_XLSX)) { // Excel 2007/2010
			wb = new XSSFWorkbook(in);
		}
		return wb;
	}

	private static Workbook getWorkbook(InputStream in, String url) throws IOException {
		Workbook wb = null;
		if (url.endsWith(EXCEL_XLS)) { // Excel 2003
			wb = new HSSFWorkbook(in);
		} else if (url.endsWith(EXCEL_XLSX)) { // Excel 2007/2010
			wb = new XSSFWorkbook(in);
		}
		return wb;
	}

	public static Double parseDouble(Object o) {
		if (o == null) {
			return null;
		} else {
			String str = o.toString();
			if (StringUtils.isBlank(str)) {
				return null;
			} else {
				try {
					return Double.parseDouble(str);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}

	public static Integer parseInt(Object o) {
		if (o == null) {
			return null;
		} else {
			String str = o.toString();
			if (StringUtils.isBlank(str)) {
				return null;
			} else {
				try {
					return Integer.parseInt(str);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}

	public static String getString(Object o) {
		if (o == null) {
			return "";
		} else {
			return o.toString();
		}
	}

	/**
	 * 解析是否成为true，false
	 */
	public static Boolean parseShiFou(Object o) {
		if (o == null) {
			return false;
		} else {
			String str = StringUtils.trim(o.toString());
			if (str.equals("是") || str.equals("true")) {
				return true;
			} else {
				return false;
			}
		}
	}

	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////

	private XSSFWorkbook createErrorData(List<Map<String, Object>> listresult, String[] titles) {
		// 1.创建HSSFWorkbook，一个HSSFWorkbook对应一个Excel文件
		XSSFWorkbook wb = new XSSFWorkbook();
		// 2.在workbook中添加一个sheet,对应Excel文件中的sheet
		XSSFSheet sheet = wb.createSheet("sheet1");
		// 3.设置表头，即每个列的列名
		String[] titel = titles;
		// 3.1创建第一行
		XSSFRow row = sheet.createRow(0);
//        // 此处创建一个序号列
//        row.createCell(0).setCellValue("序号");
		// 将列名写入
		for (int i = 0; i < titel.length; i++) {
			// 给列写入数据,创建单元格，写入数据
			row.createCell(i).setCellValue(titel[i]);
		}
		// 写入正式数据
		for (int i = 0; i < listresult.size(); i++) {
			// 创建行
			row = sheet.createRow(i + 1);
			// 序号
//            row.createCell(0).setCellValue(i+1);
//           
//            row.createCell(1).setCellValue(listresult.get(i).get("rowKey1").toString());
//            sheet.autoSizeColumn(1, true);
//            
//            row.createCell(2).setCellValue(listresult.get(i).get("rowKey2").toString());
//            
//            row.createCell(3).setCellValue(listresult.get(i).get("rowKey3").toString());
//            
//            row.createCell(4).setCellValue(listresult.get(i).get("rowKey4").toString());

			int c = 0;
			for (String key : listresult.get(i).keySet()) {
				System.out.println("key---------------:" + key);
				for (String s : titles) {
//        			System.out.println("--------------title:"+s);
					if (key.equals(s)) {
						System.out.println("succ++++++++key:" + key);
						System.out.println("succ----------" + s);
						row.createCell(c).setCellValue(listresult.get(i).get(key).toString());
						System.out.println(listresult.get(i).get(key).toString());
						break;
					}
				}
				System.out.println("c----------------" + c);
				c++;
			}
		}
		/**
		 * 上面的操作已经是生成一个完整的文件了，只需要将生成的流转换成文件即可； 下面的设置宽度可有可无，对整体影响不大
		 */
		// 设置单元格宽度
		int curColWidth = 0;
		for (int i = 0; i <= titel.length; i++) {
			// 列自适应宽度，对于中文半角不友好，如果列内包含中文需要对包含中文的重新设置。
			sheet.autoSizeColumn(i, true);
			// 为每一列设置一个最小值，方便中文显示
			curColWidth = sheet.getColumnWidth(i);
			if (curColWidth < 2500) {
				sheet.setColumnWidth(i, 2500);
			}
			// 第3列文字较多，设置较大点。
			sheet.setColumnWidth(3, 8000);
		}
		return wb;
	}

	/**
	 * 用户列表导出
	 * 
	 * @param userForm
	 */
	public String downErrorData(List<Map<String, Object>> listresult, String[] titles) {
		// getTime()是一个返回当前时间的字符串，用于做文件名称
		String name = getString(IDUtils.getSimpleId());
		// csvFile是我的一个路径，自行设置就行
		String csvFile = "D:\\";
		String ys = csvFile + "//" + name + ".xlsx";
		// 1.生成Excel
		XSSFWorkbook userListExcel = createErrorData(listresult, titles);
		try {
			// 输出成文件
			File file = new File(csvFile);
			if (file.exists() || !file.isDirectory()) {
				file.mkdirs();
			}
			// TODO 生成的wb对象传输
			FileOutputStream outputStream = new FileOutputStream(new File(ys));
			userListExcel.write(outputStream);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	public String exportData(List<Map<String, Object>> listresult, String[] titles) {
		// getTime()是一个返回当前时间的字符串，用于做文件名称
		String name = getString(IDUtils.getSimpleId());
		// csvFile是我的一个路径，自行设置就行
//		String csvFile = "D:\\";
//		String ys = csvFile + "//" + name + ".xlsx";
		// 1.生成Excel
		XSSFWorkbook userListExcel = exportDataTOExcel(listresult, titles);
		try {
			// 输出成文件
//			File file = new File(csvFile);
//			if (file.exists() || !file.isDirectory()) {
//				file.mkdirs();
//			}
			// TODO 生成的wb对象传输
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			userListExcel.write(outputStream);
			InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			//上传到OSS
			Date date = new Date();
			SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			//当前下载时间作为文件名
			String fileName = "print-excel/"+new Date().getTime()+".xlsx";
			String bucketName = "jitijingji-test1";
			
			uploadToOSS(inputStream, bucketName, fileName);
			String url = "https://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/"+fileName;
			inputStream.close();
			outputStream.close();
			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 将数据导入到Excel中
	 * @param listresult
	 * @param titles
	 * @return
	 */
	private XSSFWorkbook exportDataTOExcel(List<Map<String, Object>> listresult, String[] titles) {
		// 1.创建HSSFWorkbook，一个HSSFWorkbook对应一个Excel文件
		XSSFWorkbook wb = new XSSFWorkbook();
		// 2.在workbook中添加一个sheet,对应Excel文件中的sheet
		XSSFSheet sheet = wb.createSheet("sheet1");
		// 3.设置表头，即每个列的列名
		String[] titel = titles;
		// 3.1创建第一行
		XSSFRow row = sheet.createRow(0);
//        // 此处创建一个序号列
//        row.createCell(0).setCellValue("序号");
		// 将列名写入
		for (int i = 0; i < titel.length; i++) {
			// 给列写入数据,创建单元格，写入数据
			row.createCell(i).setCellValue(titel[i]);
		}
		// 写入正式数据
		//TODO 等待修改
		for (int i = 0; i < listresult.size(); i++) {
			// 创建行
			row = sheet.createRow(i + 1);
			for (String key : listresult.get(i).keySet()) {
				for (String s : titles) {
					if (key.equals(s)) {
						if ("户序号".equals(s)) {
							row.createCell(0).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("户主姓名".equals(s)) {
							row.createCell(1).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("地址".equals(s)) {
							row.createCell(2).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("姓名".equals(s)) {
							row.createCell(3).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("性别".equals(s)) {
							row.createCell(4).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("身份证号码".equals(s)) {
							row.createCell(5).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("是否集体组织成员".equals(s)) {
							row.createCell(6).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("个人持股数（股）".equals(s)) {
							row.createCell(7).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("与户主关系".equals(s)) {
							row.createCell(8).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("成员股权证号".equals(s)) {
							row.createCell(9).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("成员资产股".equals(s)) {
							row.createCell(10).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("本户资源股".equals(s)) {
							row.createCell(11).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("合作社名称".equals(s)) {
							row.createCell(12).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("合作社地址".equals(s)) {
							row.createCell(13).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("合作社成立时间".equals(s)) {
							row.createCell(14).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("合作社信用代码".equals(s)) {
							row.createCell(15).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("集体资产股".equals(s)) {
							row.createCell(16).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("原合作社集体资产股".equals(s)) {
							row.createCell(17).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("集体资源股".equals(s)) {
							row.createCell(18).setCellValue(listresult.get(i).get(key).toString());
							break;
						} else if ("原合作社集体资源股".equals(s)) {
							row.createCell(19).setCellValue(listresult.get(i).get(key).toString());
							break;
						}
					}
				}
			}
		}
		/**
		 * 上面的操作已经是生成一个完整的文件了，只需要将生成的流转换成文件即可； 下面的设置宽度可有可无，对整体影响不大
		 */
		// 设置单元格宽度
		int curColWidth = 0;
		for (int i = 0; i <= titel.length; i++) {
			// 列自适应宽度，对于中文半角不友好，如果列内包含中文需要对包含中文的重新设置。
			sheet.autoSizeColumn(i, true);
			// 为每一列设置一个最小值，方便中文显示
			curColWidth = sheet.getColumnWidth(i);
			if (curColWidth < 2500) {
				sheet.setColumnWidth(i, 2500);
			}
			// 第3列文字较多，设置较大点。
			sheet.setColumnWidth(3, 8000);
		}
		return wb;
	}

	
	/**
	 * 
	 * @param inputStream
	 * 		输入流对象
	 * @param bucketName
	 * 		分组包名称
	 * @param fileName
	 * 		分组包下的路径与文件名（print-excel/123456.xlsx）
	 */
	private void uploadToOSS(InputStream inputStream,String bucketName,String fileName) {

		String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
		String accessKeyId = "LTAIJ9mYIjuW54Cj";
		String accessKeySecret = "89EMlXLsP13H8mWKIvdr4iM1OvdVxs";

		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

		// 上传文件流。
		try {
			ossClient.putObject(bucketName, fileName, inputStream);
			// 关闭OSSClient。
			ossClient.shutdown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
