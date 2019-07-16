package zyxhj.flow.repository;

import zyxhj.flow.domain.ProcessRecord;
import zyxhj.utils.data.ts.TSRepository;

public class ProcessRecordRepository extends TSRepository<ProcessRecord> {

	public ProcessRecordRepository() {
		super(ProcessRecord.class);
	}

}
