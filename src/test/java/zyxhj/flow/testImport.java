package zyxhj.flow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.controller.ImprotController;
import zyxhj.core.domain.ImportTask;
import zyxhj.core.domain.OrgUser;
import zyxhj.core.domain.User;
import zyxhj.core.repository.UserRepository;
import zyxhj.core.service.ImportTaskService;
import zyxhj.flow.domain.TableBatch;
import zyxhj.flow.domain.TableBatchData;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.repository.TableSchemaRepository;
import zyxhj.utils.ExcelUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.rds.RDSRepositoryServiceTest.RDSRepositoryTest;
import zyxhj.utils.data.rds.RDSRepositoryServiceTest.RDSRepositoryTest1;

public class testImport {

	private static DruidPooledConnection conn;
	private static UserRepository urep;
	private static TableSchemaRepository tableScheamRpository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			urep = Singleton.ins(UserRepository.class);
			tableScheamRpository = Singleton.ins(TableSchemaRepository.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}
	
	
	@Test
	public void testdownUserList() {
		ExcelUtils excel = new ExcelUtils();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Object> map3 = new HashMap<String, Object>();
		Map<String, Object> map4 = new HashMap<String, Object>();
		Map<String, Object> map5 = new HashMap<String, Object>();
		Map<String, Object> map6 = new HashMap<String, Object>();
		map1.put("name", "张三1");
		map1.put("age", "18");
		map1.put("sex", "男");
		map2.put("name", "张三2");
		map2.put("age", "18");
		map2.put("sex", "男");
		map3.put("name", "张三3");
		map3.put("age", "18");
		map3.put("sex", "男");
		map4.put("name", "张三4");
		map4.put("age", "18");
		map4.put("sex", "男");
		map5.put("name", "张三5");
		map5.put("age", "18");
		map5.put("sex", "男");
		map6.put("name", "张三6");
		map6.put("age", "18");
		map6.put("sex", "男");
		list.add(map1);
		list.add(map2);
		list.add(map3);
		list.add(map4);
		list.add(map5);
		list.add(map6);
		int size = map1.keySet().size();
		String[] titles = new String[size];
		int i = 0;
		for (String key : map1.keySet()) {
			titles[i] = key;
			i++;
		}
		excel.downErrorData(list, titles);
	}

	@Test
	public void testReadExcel() {
		String url = "C:\\Users\\Admin\\Desktop\\123456.xlsx";
		Long importTaskId = 401784026919259L;
		Long tableSchemaId = 401655491082651L;
		Long userId = 10010L;
		Long batchId = 401769446115940L;
		String batchVer = "ce_1_1";
		try {
			readExcel01(tableSchemaId, url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void readExcel01(Long tableSchemaId, String path) throws Exception {
		

		System.out.println("进入readExcel-----Service");
		File file = new File(path);
		FileInputStream fis = null;
		Workbook workBook = null;
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				workBook = WorkbookFactory.create(fis);

				TableSchema ts = tableScheamRpository.get(conn,EXP.INS().key("id", tableSchemaId));
				JSONArray ja = ts.columns;

				List<String> alias = new ArrayList<String>();
				List<String> columnName = new ArrayList<String>();
				for (int a = 0; a < ja.size(); a++) {
					JSONObject jo = ja.getJSONObject(a);
					columnName.add(jo.getString("name"));
					alias.add(jo.getString("alias"));
				}

				// sheet工作表
				Sheet sheetAt = workBook.getSheetAt(0);
				// 获取工作表名称
				String sheetName = sheetAt.getSheetName();
				System.out.println("工作表名称：" + sheetName);
				// 获取当前Sheet的总行数
				int rowsOfSheet = sheetAt.getPhysicalNumberOfRows();
				System.out.println("当前表格的总行数:" + rowsOfSheet);
				// 第一行
				Row row0 = sheetAt.getRow(0);
				int physicalNumberOfCells = sheetAt.getRow(0).getPhysicalNumberOfCells();
				String[] titles = new String[physicalNumberOfCells];
				for (int i = 0; i < physicalNumberOfCells; i++) {
					titles[i] = row0.getCell(i).getStringCellValue();
				}

				ImportTask imp = new ImportTask();
				imp.amount = rowsOfSheet;
				int completedCount = 0;
				for (int r = 1; r < rowsOfSheet; r++) {
					Row row = sheetAt.getRow(r);
					if (row == null) {
						continue;
					} else {
						for (int t = 0; t < titles.length; t++) {
							for (int al = 0; al < alias.size(); al++) {
								System.out.println("alias:" + alias.get(al));
								if (titles[t].equals(alias.get(al))) {
									JSONObject colData = new JSONObject();
									colData.put(columnName.get(al), row.getCell(t).getStringCellValue());
//									tableService.addBatchData(batchId, tableSchemaId, userId, batchVer, colData,
//											"Excel数据导入");
//									imp.completedCount = ++completedCount;
//									taskRepository.update(conn, EXP.INS().key("id", taskId), imp, true);
									System.out.println(colData.toJSONString());
									break;
								}
							}
						}
					}
				}
				imp.successCount = completedCount;
				imp.failureCount = (rowsOfSheet - completedCount);
//				taskRepository.update(conn, EXP.INS().key("id", taskId), imp, true);

				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("文件不存在!");
		}
	}

	public static void readExcel(String path) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File file = new File(path);
		FileInputStream fis = null;
		Workbook workBook = null;
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				workBook = WorkbookFactory.create(fis);
				int numberOfSheets = workBook.getNumberOfSheets();
				// sheet工作表
				for (int s = 0; s < numberOfSheets; s++) {
					Sheet sheetAt = workBook.getSheetAt(s);
					// 获取工作表名称
					String sheetName = sheetAt.getSheetName();
					System.out.println("工作表名称：" + sheetName);
					// 获取当前Sheet的总行数
					int rowsOfSheet = sheetAt.getPhysicalNumberOfRows();
					System.out.println("当前表格的总行数:" + rowsOfSheet);
					// 第一行
					Row row0 = sheetAt.getRow(0);
					int physicalNumberOfCells = sheetAt.getRow(0).getPhysicalNumberOfCells();
					String[] title = new String[physicalNumberOfCells];
					for (int i = 0; i < physicalNumberOfCells; i++) {
						title[i] = row0.getCell(i).getStringCellValue();
					}
					for (int r = 1; r < rowsOfSheet; r++) {
						Row row = sheetAt.getRow(r);
						if (row == null) {
							continue;
						} else {
							int rowNum = row.getRowNum() + 1;
							System.out.println("当前行:" + rowNum);
							// 总列(格)
							Cell cell0 = row.getCell(0);
							Cell cell1 = row.getCell(1);
							Cell cell2 = row.getCell(2);

							if (cell1.getCellTypeEnum() == CellType.STRING) {
								String stringCellValue = cell0.getStringCellValue();
								System.out.println("第" + rowNum + "行，第一列[" + title[0] + "]数据===" + stringCellValue);
							} else {
								System.out.println("第" + rowNum + "行，第一列[" + title[0] + "]数据错误！");
							}
							if (cell1.getCellTypeEnum() == CellType.STRING) {
								String stringCellValue = cell1.getStringCellValue();
								System.out.println("第" + rowNum + "行，第二列[" + title[1] + "]数据===" + stringCellValue);
							} else {
								System.out.println("第" + rowNum + "行，第二列[" + title[1] + "]数据错误！");
							}
						}
					}
				}
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("文件不存在!");
		}
	}

	public void testimportTableBatchData() {

	}

}
