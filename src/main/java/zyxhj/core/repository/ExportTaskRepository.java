package zyxhj.core.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.ExportTask;
import zyxhj.utils.data.rds.RDSRepository;

public class ExportTaskRepository extends RDSRepository<ExportTask> {

	public ExportTaskRepository() {
		super(ExportTask.class);
	}

}
