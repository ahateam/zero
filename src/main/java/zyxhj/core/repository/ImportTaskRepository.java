package zyxhj.core.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.ImportTask;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class ImportTaskRepository extends RDSRepository<ImportTask> {

	public ImportTaskRepository() {
		super(ImportTask.class);
	}

	/**
	 * 根据状态获取导入任务列表（创建时间倒序）</br>
	 * 不传状态（null）相当于全部查询
	 */
	public List<ImportTask> getImportTasks(DruidPooledConnection conn, String origin, Byte status, Integer count,
			Integer offset) throws ServerException {
		if (!status.equals(ImportTask.STATUS.WAITING.v()) || !status.equals(ImportTask.STATUS.FILE_READY.v())
				|| !status.equals(ImportTask.STATUS.PENDING.v()) || !status.equals(ImportTask.STATUS.PROGRESSING.v())
				|| !status.equals(ImportTask.STATUS.COMPLETED.v())) {
			// 非正常状态
			return getList(conn, "WHERE origin=? ORDER BY create_time DESC", new Object[] { origin }, count, offset);

		} else {
			return getList(conn, "WHERE origin=? AND status=? ORDER BY create_time DESC",
					new Object[] { origin, status }, count, offset);
		}
	}
}
