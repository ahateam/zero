package zyxhj.core.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;

import zyxhj.core.domain.ImportTask;
import zyxhj.core.domain.ImportTempRecord;
import zyxhj.core.repository.ImportTaskRepository;
import zyxhj.core.repository.ImportTempRecordRepository;
import zyxhj.utils.CodecUtils;
import zyxhj.utils.ExcelUtils;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;

public class ImportTaskService {

	private static Logger log = LoggerFactory.getLogger(ImportTaskService.class);

	private ImportTaskRepository taskRepository;
	private ImportTempRecordRepository tempRecordRepository;

//	private static TSAutoCloseableClient client;

	public ImportTaskService() {
		try {

//			client = (TSAutoCloseableClient) DataSourceUtils.getDataSource("tsDefault").openConnection();

			taskRepository = Singleton.ins(ImportTaskRepository.class);
			tempRecordRepository = Singleton.ins(ImportTempRecordRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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

			return taskRepository.update(conn,EXP.INS().key("origin", origin).andKey("task_id", taskId), renew, true);
			
		} else {
			return 0;
		}
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
