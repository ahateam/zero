package zyxhj.core.repository;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
			return getList(conn, "origin=? ORDER BY create_time DESC", Arrays.asList(origin), count, offset);

		} else {
			return getList(conn, "origin=? AND status=? ORDER BY create_time DESC", Arrays.asList(origin, status),
					count, offset);
		}
	}

	// 组织查询任务列表
	public List<ImportTask> getListImportTask(DruidPooledConnection conn, Long orgId, Byte type, Integer count,
			Integer offset) throws Exception {
		StringBuffer sb = new StringBuffer("org_id = ? ");
		if (type == 0) {
			sb.append(" AND origin = 'user'");
		} else if (type == 1) {
			sb.append(" AND origin = 'asset'");
		}
		sb.append(" ORDER BY create_time DESC");
		return getList(conn, sb.toString(), Arrays.asList(orgId), count, offset);
	}

	public void countORGUserImportCompletionTask(DruidPooledConnection conn, Long importTaskId) throws Exception {
		this.update(conn, StringUtils.join("SET success_count = success_count+1,completed_count = completed_count + 1"),
				null, "id = ?", Arrays.asList(importTaskId));
	}

	public void countORGUserImportNotCompletionTask(DruidPooledConnection conn, Long importTaskId) throws Exception {
		this.update(conn, StringUtils.join("SET failure_count = failure_count+1,completed_count = completed_count + 1"),
				null, "id = ?", Arrays.asList(importTaskId));
	}
}
