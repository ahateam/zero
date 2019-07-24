package zyxhj.flow.repository;

import zyxhj.flow.domain.ProcessLog;
import zyxhj.utils.data.rds.RDSRepository;

public class ProcessLogRepository extends RDSRepository<ProcessLog> {

	public ProcessLogRepository() {
		super(ProcessLog.class);
	}

}
