package zyxhj.core.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.ExportTask;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.rds.RDSRepository;

public class ExportTaskRepository extends RDSRepository<ExportTask> {

	public ExportTaskRepository() {
		super(ExportTask.class);
	}

	public List<ExportTask> getExportTasks(DruidPooledConnection conn) throws Exception {
		String where = "file_urls is not null ";
		return this.getList(conn, where, null, null, null);
	}

	public void editOrgs(DruidPooledConnection conn, ExportTask o) throws Exception {
		this.update(conn, EXP.INS().key("id", o.id), o, true);
	}

}
