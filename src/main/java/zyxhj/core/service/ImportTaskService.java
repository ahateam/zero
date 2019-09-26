package zyxhj.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.Direction;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;

import io.vertx.core.Vertx;
import zyxhj.core.controller.ImprotController;
import zyxhj.core.domain.ImportTask;
import zyxhj.core.domain.ImportTempRecord;
import zyxhj.core.repository.ImportTaskRepository;
import zyxhj.core.repository.ImportTempRecordRepository;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.repository.TableBatchDataRepository;
import zyxhj.flow.service.TableService;
import zyxhj.utils.ExcelUtils;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.RowChangeBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSQL.OP;

public class ImportTaskService {

	private static Logger log = LoggerFactory.getLogger(ImportTaskService.class);

	private static ImportTaskRepository taskRepository;
	private ImportTempRecordRepository tempRecordRepository;
	private static TableService tableService;

	// private static TSAutoCloseableClient client;

	public ImportTaskService() {
		try {

			// client = (TSAutoCloseableClient)
			// DataSourceUtils.getDataSource("tsDefault").openConnection();

			taskRepository = Singleton.ins(ImportTaskRepository.class);
			tempRecordRepository = Singleton.ins(ImportTempRecordRepository.class);
			tableService = Singleton.ins(TableService.class);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 创建导入任务
	 */
	public void createImportTask(DruidPooledConnection conn, String origin, String title, Long userId)
			throws Exception {
		ImportTask it = new ImportTask();
		it.origin = origin;
		it.id = IDUtils.getSimpleId();
		it.title = title;
		it.userId = userId;
		it.status = ImportTask.STATUS.WAITING.v();

		it.createTime = new Date();

		it.amount = 0;
		it.completedCount = 0;
		it.successCount = 0;
		it.failureCount = 0;

		taskRepository.insert(conn, it);
	}

	/**
	 * 设置导入文件地址列表</br>
	 * JSONArray格式的文件地址列表
	 */
	public int setImportFileUrls(DruidPooledConnection conn, String origin, Long taskId, JSONArray fileUrls)
			throws Exception {
		if (fileUrls != null && fileUrls.size() > 0) {
			ImportTask renew = new ImportTask();
			renew.status = ImportTask.STATUS.FILE_READY.v();
			renew.fileUrls = JSON.toJSONString(fileUrls);

			return taskRepository.update(conn, EXP.INS().key("origin", origin).andKey("task_id", taskId), renew, true);

		} else {
			return 0;
		}
	}

	public void createImportTask(DruidPooledConnection conn, String title, Long batchId, Long userId) throws Exception {
		ImportTask imp = new ImportTask();
		imp.origin = "tableBatch";
		imp.id = IDUtils.getSimpleId();
		imp.orgId = batchId;
		imp.title = title;
		imp.userId = userId;
		imp.createTime = new Date();
		imp.startTime = new Date();
		imp.finishTime = new Date();
		imp.amount = 0;
		imp.completedCount = 0;
		imp.successCount = 0;
		imp.failureCount = 0;
		imp.status = ImportTask.STATUS.WAITING.v();
		taskRepository.insert(conn, imp);
	}

	// 查询组织导入
	public List<ImportTask> getListImportTask(DruidPooledConnection conn, Long orgId, Byte type, Integer count,
			Integer offset) throws Exception {
		return taskRepository.getListImportTask(conn, orgId, type, count, offset);
	}

	/**
	 * 导入到临时表
	 * 
	 * @param importTaskId 导入id
	 * @param skipRowCount 第几行开始
	 * @param colCount     总列数
	 */
	public void importRecord(SyncClient client, DruidPooledConnection conn, Long batchId, Long userId, String url,
			Long importTaskId, Integer skipRowCount, Integer colCount) throws Exception {

		PrimaryKey pk = new PrimaryKeyBuilder().add("taskId", importTaskId).addAutoIncermentKey("recordId").build();
		JSONArray json = JSONArray.parseArray(url);
		Integer count = 0;// 总条数
		List<List<Object>> table = null;
		for (int o = 0; o < json.size(); o++) {
			table = ExcelUtils.readExcelOnline(json.getString(0), skipRowCount, colCount, 0);

			List<List<Column>> batchRows = new ArrayList<>();
			for (List<Object> row : table) {
				ColumnBuilder cb = new ColumnBuilder();
				cb.add("batch", batchId);
				cb.add("status", (long) ImportTempRecord.STATUS.PENDING.v());
				for (int i = 0; i < colCount; i++) {
					cb.add(StringUtils.join("Col", i), ExcelUtils.getString(row.get(i)));
				}

				count++;
				List<Column> list = cb.build();

				batchRows.add(list);

				if (batchRows.size() >= 10) {
					RowChangeBuilder rcb = new RowChangeBuilder();
					for (List<Column> cc : batchRows) {
						rcb.put(tempRecordRepository.getTableName(), pk, cc, true);
					}

					TSRepository.nativeBatchWrite(client, rcb.build());
					batchRows.clear();
				}
			}

			if (batchRows.size() > 0) {
				RowChangeBuilder rcb = new RowChangeBuilder();
				for (List<Column> cc : batchRows) {
					rcb.put(tempRecordRepository.getTableName(), pk, cc, true);
				}

				TSRepository.nativeBatchWrite(client, rcb.build());
				batchRows.clear();
			}
		}

		// 修改导入任务总数
		ImportTask imp = new ImportTask();
		imp.amount = count;
		imp.startTime = new Date();
		imp.status = ImportTask.STATUS.FILE_READY.v();
		taskRepository.update(conn, EXP.INS().key("id", importTaskId), imp, true);

	}

//	public static void main(String[] args) {
//		String url = "C:\\Users\\Admin\\Desktop\\123456.xlsx";
//		Long importTaskId = 401784026919259L;
//		Long tableSchemaId = 401655491082651L;
//		Long userId = 10010L;
//		Long batchId = 401769446115940L;
//		String batchVer = "ce_1_1";
//		try {
//			importTableBatchData(importTaskId,tableSchemaId, batchId, batchVer, userId, url);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	// 开始导入数据到批次数据表TableBatchData
	public static void importTableBatchData(DruidPooledConnection conn, Long importTaskId, Long tableSchemaId,
			Long batchId, String batchVer, Long userId, String fileUrl) throws Exception {

		System.out.println("进入importTableBatchData-----Service");
//
//		// 异步方法，不会阻塞
//		Vertx.vertx().executeBlocking(future -> {
//			// 下面这行代码可能花费很长时间

//			System.out.println("进入executeBlocking-----Service");
//			DruidDataSource dds;
//			DruidPooledConnection conn = null;
//			try {
//				dds = DataSource.getDruidDataSource("rdsDefault.prop");
//				conn = (DruidPooledConnection) dds.getConnection();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			try {

		// 修改导入任务为正在导入
		ImportTask imp = new ImportTask();
		imp.status = 3;

		System.out.println("00000000000000000000");
		System.out.println(importTaskId);
		int i = taskRepository.update(conn, EXP.INS(false).key("id", importTaskId), imp, true);
		System.out.println(i);
		System.out.println("11111111111111111111");
		readExcel(conn, fileUrl, tableSchemaId, batchId, userId, batchVer, importTaskId);

		// 执行完成 修改任务表里成功与失败数量
		imp.finishTime = new Date();
		imp.status = ImportTask.STATUS.COMPLETED.v();
		taskRepository.update(conn, EXP.INS().key("id", importTaskId), imp, true);

//			} catch (Exception eee) {
//				eee.printStackTrace();
//			} finally {
//				try {
//					conn.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			future.complete("ok");
//		}, res -> {
//			System.out.println("The result is: " + res.result());
//		});

	}

	public static void readExcel(DruidPooledConnection conn, String path, Long tableSchemaId, Long batchId, Long userId,
			String batchVer, Long taskId) throws Exception {

		System.out.println("进入readExcel-----Service");
		File file = new File(path);
		FileInputStream fis = null;
		Workbook workBook = null;
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				workBook = WorkbookFactory.create(fis);

				TableSchema ts = tableService.getTableSchemaById(tableSchemaId);
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
					JSONObject colData = new JSONObject();
					Row row = sheetAt.getRow(r);
					if (row == null) {
						continue;
					} else {
						for (int t = 0; t < titles.length; t++) {
							for (int al = 0; al < alias.size(); al++) {
								System.out.println("alias:" + alias.get(al));
								if (titles[t].equals(alias.get(al))) {
									Cell col = row.getCell(t);
									switch (col.getCellTypeEnum()) {
									case NUMERIC:
										if(DateUtil.isCellDateFormatted(col)) {
											Date date =  col.getDateCellValue();
											colData.put(columnName.get(al), new SimpleDateFormat("yyyy-MM-dd").format(date) );
										}else {
											colData.put(columnName.get(al), col.getNumericCellValue());
										}
										break;
									case STRING:
										colData.put(columnName.get(al), col.getStringCellValue());
										break;
										
									}
									break;
								}
							}
						}
					}
					System.out.println(colData.toJSONString());
					tableService.addBatchData(batchId, tableSchemaId, userId, batchVer, colData, "Excel数据导入");
					imp.completedCount = ++completedCount;
					taskRepository.update(conn, EXP.INS().key("id", taskId), imp, true);
				}
				imp.successCount = completedCount;
				imp.failureCount = (rowsOfSheet - completedCount);
				taskRepository.update(conn, EXP.INS().key("id", taskId), imp, true);

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

	private Object getValues(String dataType, JSONObject data, int c) {
		if (!StringUtils.isBlank(dataType)) {
			if ("Integer".equals(dataType)) {
				return data.getInteger(StringUtils.join("Col", c));
			} else if ("String".equals(dataType)) {
				return data.getString(StringUtils.join("Col", c));
			} else if ("decimal".equals(dataType)) {
				return data.getDouble(StringUtils.join("Col", c));
			} else if ("date".equals(dataType)) {
				return data.getDate(StringUtils.join("Col", c));
			} else if ("time".equals(dataType)) {
				return data.getDate(StringUtils.join("Col", c));
			} else if ("money".equals(dataType)) {
				return data.getDouble(StringUtils.join("Col", c));
			} else if ("bool".equals(dataType)) {
				return data.getBoolean(StringUtils.join("Col", c));
			} else if ("subtable".equals(dataType)) {
				return data.getString(StringUtils.join("Col", c));
			} else {
				return "数据为空";
			}
		} else {
			return "数据类型错误";
		}
	}

	public void deleteImportTask(SyncClient client, Long importTaskId) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("taskId", importTaskId).addAutoIncermentKey("recordId").build();
		TSRepository.nativeDel(client, "ImportTempRecord", pk);
	}

	public JSONArray getListImportTemp(SyncClient client, Long importTaskId, Integer count, Integer offset)
			throws Exception {

		// 设置起始主键
		PrimaryKey pkStart = new PrimaryKeyBuilder().add("taskId", importTaskId)
				.add("recordId", PrimaryKeyValue.INF_MIN).build();

		// 设置结束主键
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add("taskId", importTaskId).add("recordId", PrimaryKeyValue.INF_MAX)
				.build();
		return tempRecordRepository.getRange(client, Direction.FORWARD, pkStart, pkEnd, count, offset);

	}

	public JSONObject getFailImportRecord(SyncClient client, Long importTaskId, Integer count, Integer offset)
			throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "status", (long) ImportTempRecord.STATUS.FAILURE.v()).Term(OP.AND, "taskId", importTaskId);
		ts.setLimit(count);
		ts.setOffset(offset);
		ts.setGetTotalCount(true);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, tempRecordRepository.getTableName(), "ImportTempRecordIndex", query);
	}

	public ImportTask getImportTask(DruidPooledConnection conn, Long importTaskId) throws Exception {
		return taskRepository.get(conn, EXP.INS().key("id", importTaskId));
	}

	/**
	 * 将文件导入临时表
	 */
	public void importFiles2Temp(DruidPooledConnection conn, String origin, Long id, int skipRowCount, int colCount,
			int sheetIndex) throws Exception {
		// ImportTask it = taskRepository.getByANDKeys(conn, new String[] { "origin",
		// "id" }, new Object[] { origin, id });
		//
		// if (it == null) {
		// throw new ServerException(BaseRC.REPOSITORY_QUERY_EMPTY);
		// } else {
		//
		// if (it.status.equals(ImportTask.STATUS.FILE_READY.v())) {
		// // 没有导入过文件
		//
		// JSONArray fileUrls = JSON.parseArray(it.fileUrls);
		// // 导入临时记录
		// int size = fileUrls.size();
		// if (size > 0) {
		// for (int i = 0; i < size; i++) {
		// String url = fileUrls.getString(i);
		// importFile(conn, origin, id, url, skipRowCount, colCount, sheetIndex);
		// }
		//
		// // 获取总数
		// int amount = tempRecordRepository.countByTaskId(conn, id);
		//
		// // 更新任务状态，更新临时记录总数
		// ImportTask renew = new ImportTask();
		// renew.status = ImportTask.STATUS.PENDING.v();
		// renew.amount = amount;
		// taskRepository.updateByANDKeys(conn, new String[] { "origin", "id" }, new
		// Object[] { origin, id },
		// renew, true);
		// }
		// } else {
		// throw new ServerException(new RC("ImportTaskService",
		// "导入任务没准备好文件，或进行中，或已结束，不能修改文件"));
		// }
		// }
	}

	/**
	 * 操作比较耗时，需要开新线程处理</br>
	 * 而且新线程可能无法公用当前现成的conn对象
	 */
	private void importFile(DruidPooledConnection conn, String origin, Long id, String fileUrl, int skipRowCount,
			int colCount, int sheetIndex) throws Exception {

		// // 读取文件
		// List<List<Object>> excelTable = ExcelUtils.readExcelOnline(fileUrl,
		// skipRowCount, colCount, sheetIndex);
		//
		// List<ImportTempRecord> list = new ArrayList<ImportTempRecord>();
		// // 遍历文件，并构造ImportTempRecord数组
		// for (List<Object> excelRow : excelTable) {
		//
		// // 循环读取全部列，存入JSONArray中
		// JSONArray row = new JSONArray();
		// for (int col = 0; col < colCount; col++) {
		// row.add(ExcelUtils.getString(excelRow.get(col)));
		// }
		//
		// // 构造临时记录
		// ImportTempRecord record = new ImportTempRecord();
		// record.taskId = id;
		// record.recordId = IDUtils.getSimpleId();
		//// record.content = JSON.toJSONString(row);
		//
		// list.add(record);
		// }
		//
		// // 开始循环并逐批导入到数据库临时表
		// int batchCount = 10;// 10个一批
		// ArrayList<ImportTempRecord> temp = new ArrayList<>();
		// Iterator<ImportTempRecord> it = list.iterator();
		// while (it.hasNext()) {
		// temp.add(it.next());
		// if (temp.size() == batchCount) {
		// // 满10个，先批量处理
		// tempRecordRepository.insertList(conn, temp);
		//
		// // 清空，再继续
		// temp = new ArrayList<>();
		// }
		// }
		// // 可能还有未满10个，但没处理完的
		// if (temp.size() > 0) {
		// tempRecordRepository.insertList(conn, temp);
		// }
	}

	/**
	 * 根据状态获取导入任务列表（创建时间倒序）</br>
	 * 不传状态（null）相当于全部查询
	 */
	public List<ImportTask> getImportTasks(DruidPooledConnection conn, String origin, Byte status, Integer count,
			Integer offset) throws Exception {
		return taskRepository.getImportTasks(conn, origin, status, count, offset);
	}

	/**
	 * 根据状态获取导入临时记录列表</br>
	 * 不传状态（null）相当于全部查询
	 */
	public List<ImportTempRecord> getImportTempRecords(DruidPooledConnection conn, Long taskId, Byte status,
			Integer count, Integer offset) throws Exception {
		// return tempRecordRepository.getImportTempRecords(conn, taskId, status, count,
		// offset);
		return null;
	}

	public void importTemp2Assets() throws Exception {
		// TODO 具体实现
	}

}
