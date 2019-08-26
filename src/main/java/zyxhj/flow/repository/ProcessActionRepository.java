package zyxhj.flow.repository;

import zyxhj.flow.domain.ProcessAction;
import zyxhj.utils.data.rds.RDSRepository;

public class ProcessActionRepository extends RDSRepository<ProcessAction> {

	public ProcessActionRepository() {
		super(ProcessAction.class);
	}
}
