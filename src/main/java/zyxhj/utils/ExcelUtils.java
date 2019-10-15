package zyxhj.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

//import com.aliyun.oss.OSS;
//import com.aliyun.oss.OSSClientBuilder;

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
			int c = 0;
			for (String key : listresult.get(i).keySet()) {
				for (String s : titles) {
					if (key.equals(s)) {
						row.createCell(c).setCellValue(listresult.get(i).get(key).toString());
						break;
					}
				}
				c++;
			}
		}
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
}
