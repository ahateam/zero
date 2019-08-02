package zyxhj.flow.repository;

import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.flow.domain.ProcessLog;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class ProcessLogRepository extends RDSRepository<ProcessLog> {

	public ProcessLogRepository() {
		super(ProcessLog.class);
	}

	public List<ProcessLog> getProcessLogList(DruidPooledConnection conn, Long processId, Integer count,
			Integer offset) {

		String where = "process_id = ? ORDER BY timestamp desc";

		try {
			return this.getList(conn, where, Arrays.asList(processId), count, offset);
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
