package zyxhj.flow;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.controller.ImprotController;
import zyxhj.core.domain.OrgUser;
import zyxhj.core.domain.User;
import zyxhj.core.repository.UserRepository;
import zyxhj.core.service.ImportTaskService;
import zyxhj.flow.domain.TableBatchData;
import zyxhj.utils.ExcelUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.rds.RDSRepositoryServiceTest.RDSRepositoryTest;
import zyxhj.utils.data.rds.RDSRepositoryServiceTest.RDSRepositoryTest1;

public class testImport {

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
		readExcel("D:\\401716947882931.xlsx");
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
//							Cell cell3 = row.getCell(3);
//							Cell cell4 = row.getCell(4);

							if (cell1.getCellTypeEnum() == CellType.STRING) {
								String stringCellValue = cell1.getStringCellValue();
								System.out.println("第" + rowNum + "行，第一列[" + title[0] + "]数据" + stringCellValue);
							} else {
								System.out.println("第" + rowNum + "行，第一列[" + title[0] + "]数据错误！");
							}
							if (cell1.getCellTypeEnum() == CellType.STRING) {
								String stringCellValue = cell1.getStringCellValue();
								System.out.println("第" + rowNum + "行，第二列[" + title[1] + "]数据" + stringCellValue);
							} else {
								System.out.println("第" + rowNum + "行，第二列[" + title[1] + "]数据错误！");
							}
//							if (cell2.getCellTypeEnum() == CellType.STRING) {
//								String stringCellValue = cell2.getStringCellValue();
//								System.out.println(stringCellValue);
//							} else {
//								System.out.println("第" + rowNum + "行，第三列[" + title[2] + "]数据错误！");
//							}
//							if ((cell3.getCellTypeEnum() == CellType.NUMERIC) && DateUtil.isCellDateFormatted(cell3)) {
//								Date dateCellValue = cell3.getDateCellValue();
//								System.out.println(sdf.format(dateCellValue));
//							} else {
//								System.out.println("第" + rowNum + "行，第四列[" + title[3] + "]数据错误！");
//							}
//							if ((cell4.getCellTypeEnum() == CellType.NUMERIC)
//									&& (!DateUtil.isCellDateFormatted(cell4))) {
//								double numericCellValue = cell4.getNumericCellValue();
//								System.out.println(numericCellValue);
//							} else {
//								System.out.println("第" + rowNum + "行，第五列[" + title[4] + "]数据错误！");
//							}
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

	private static DruidPooledConnection conn;
	private static UserRepository urep;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			urep = Singleton.ins(UserRepository.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	// 正式表数据导出测试
	@Test
	public void testFormalExport() {

		try {

			List<Map<String, Object>> exportDataList = new ArrayList<Map<String, Object>>();

			String newSql = "SELECT OU.family_number AS '户序号', OU.family_master AS '户主姓名', OU.address AS '地址', U.real_name AS '姓名', U.id_number AS '身份证号码', ou.is_org_user AS '是否组织成员', ou.share_amount AS '个人持股数（股）', ou.family_relations AS '与户主关系', ou.share_cer_no AS '成员股权证号', ou.resource_shares AS '本户资源股', ou.asset_shares AS '本户资产股', o.`name` AS '合作社名称', o.address AS '合作社地址', o.create_time AS '合作社成立时间', o.`code` AS '合作社信用代码', o.asset_shares AS '集体资产股', o.resource_shares AS '集体资源股' FROM ( tb_ecm_org O LEFT JOIN tb_ecm_org_user OU ON O.id = OU.org_id ) LEFT JOIN tb_user U ON OU.user_id = U.id WHERE O.id = 397652553337218 ";
			
			
			
//			String sql = "select real_name, id_number, sex, pwd from tb_user";
			
			List<Object[]> olist = urep.testExport(conn, newSql, 100, 0);

			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < olist.size(); i++) {
				Object[] s = olist.get(i);
				Map<String, Object> data = new HashMap<String, Object>();

				if(s[0]==null) {
					data.put("户序号", "");
				}else {
					data.put("户序号", s[0].toString());
				}
				if(s[1]==null) {
					data.put("户主姓名", "");
				}else {
					data.put("户主姓名", s[1].toString());
				}
				if(s[2]==null) {
					data.put("地址", "");
				}else {
					data.put("地址", s[2].toString());
				}
				if(s[3]==null) {
					data.put("姓名","");
				}else {
					data.put("姓名", s[3].toString());
				}

				if(s[4]==null) {
					data.put("性别", "");
					data.put("身份证号码","");
				}else {
					String idNumber = s[4].toString();
					if(idNumber.length()==18) {
						if(Integer.parseInt(idNumber.substring(16, 17))%2==0) {
							data.put("性别", "女");
						}else {
							data.put("性别", "男");
						}
					}
					data.put("身份证号码", idNumber);
				}
				if(s[5]==null) {
					data.put("是否集体组织成员", "否");
				}else {
					if((boolean)s[5]==true) {
						data.put("是否集体组织成员", "是");
					}else {
						data.put("是否集体组织成员", "否");
					}
				}
				
				if(s[6]==null) {
					data.put("个人持股数（股）", "");
				}else {
					data.put("个人持股数（股）", s[6].toString());
				}
				
				if(s[7]==null) {
					data.put("与户主关系", "");
				}else {
					data.put("与户主关系", s[7].toString());
				}
				
				if(s[8]==null) {
					data.put("成员股权证号", "");
				}else {
					data.put("成员股权证号", s[8].toString());
				}
				
				if(s[9]==null) {
					data.put("本户资源股", "");
				}else {
					data.put("本户资源股", s[9].toString());
				}
				
				if(s[10]==null) {
					data.put("本户资产股","");
				}else {
					data.put("本户资产股", s[10].toString());
				}

				if(s[11]==null) {
					data.put("合作社名称", "");
				}else {
					data.put("合作社名称", s[11].toString());
				}
				
				if(s[12]==null) {
					data.put("合作社地址", "");
				}else {
					data.put("合作社地址", s[11].toString());
				}
				
				if(s[13]==null) {
					data.put("合作社成立时间","");
				}else {
					data.put("合作社成立时间", s[13].toString().substring(0,10));
				}
				
				if(s[14]==null) {
					data.put("合作社信用代码","");
				}else {
					data.put("合作社信用代码", s[14].toString());
				}

				if(s[15]==null) {
					data.put("集体资产股", "");
				}else {
					data.put("集体资产股", s[15].toString());
				}
				
				data.put("原合作社集体资产股", "");
				
				if(s[16]==null) {
					data.put("集体资源股", "");
				}else {
					data.put("集体资源股", s[16].toString());
				}
				data.put("原合作社集体资产股","");
				
				exportDataList.add(data);
				map = data;
			}
			
			
			String[] titles = new String[]{"户序号","户主姓名","地址","姓名","性别","身份证号码","是否集体组织成员","个人持股数（股）","与户主关系","成员股权证号","本户资产股","本户资源股","合作社名称","合作社地址","合作社成立时间","合作社信用代码","集体资产股","原合作社集体资产股","集体资源股","原合作社集体资源股"};
//			int i = 0;
//			for (String key : map.keySet()) {
//				System.out.println(key);
//				titles[i] = key;
//				i++;
//			}
			ExcelUtils utils = new ExcelUtils();
			utils.exportData(exportDataList, titles);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Test
	public void testuuuu() {

		Date date = new Date();
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		System.out.println(new Date().getTime());
	}

}
