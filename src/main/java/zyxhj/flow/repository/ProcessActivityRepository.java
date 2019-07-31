package zyxhj.flow.repository;


import zyxhj.flow.domain.ProcessActivity;
import zyxhj.utils.data.rds.RDSRepository;

public class ProcessActivityRepository extends RDSRepository<ProcessActivity> {

	public ProcessActivityRepository() {
		super(ProcessActivity.class);
	}
	

}
