package zyxhj.flow.repository;

import zyxhj.flow.domain.ProcessActivity;
import zyxhj.utils.data.ts.TSRepository;

public class ProcessActivityRepository extends TSRepository<ProcessActivity> {

	public ProcessActivityRepository() {
		super(ProcessActivity.class);
	}

}
