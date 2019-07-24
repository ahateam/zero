package zyxhj.flow.repository;

import zyxhj.utils.data.rds.RDSRepository;

public class ProcessRepository extends RDSRepository<zyxhj.flow.domain.Process> {

	public ProcessRepository() {
		super(zyxhj.flow.domain.Process.class);
	}

}
