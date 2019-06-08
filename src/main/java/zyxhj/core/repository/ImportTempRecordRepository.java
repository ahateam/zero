package zyxhj.core.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.ImportTempRecord;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class ImportTempRecordRepository extends RDSRepository<ImportTempRecord> {

	public ImportTempRecordRepository() {
		super(ImportTempRecord.class);
	}

	public int countByTaskId(DruidPooledConnection conn, Long taskId) throws ServerException {
		Object[] getObj = sqlGetObjects(conn, "SELECT COUNT(*) FROM tb_import_temp_record WHERE task_id=?",
				new Object[] { taskId });
		return Integer.parseInt(getObj[0].toString());
	}

	/**
	 * 根据状态获取导入临时记录列表</br>
	 * 不传状态（null）相当于全部查询
	 */
	public List<ImportTempRecord> getImportTempRecords(DruidPooledConnection conn, Long taskId, Byte status,
			Integer count, Integer offset) throws ServerException {
		if (!status.equals(ImportTempRecord.STATUS.PENDING.v()) || //
				!status.equals(ImportTempRecord.STATUS.SUCCESS.v()) || //
				!status.equals(ImportTempRecord.STATUS.FAILURE.v())) {
			// 非正常状态，全部获取
			return getList(conn, "WHERE taskId=?", new Object[] { taskId }, count, offset);
		} else {
			return getList(conn, "WHERE taskId=? AND status=?", new Object[] { taskId, status }, count, offset);
		}
	}
}