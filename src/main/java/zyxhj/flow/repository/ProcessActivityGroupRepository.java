package zyxhj.flow.repository;

import zyxhj.flow.domain.ProcessActivityGroup;
import zyxhj.utils.data.rds.RDSRepository;

public class ProcessActivityGroupRepository extends RDSRepository<ProcessActivityGroup> {

	public ProcessActivityGroupRepository() {
		super(ProcessActivityGroup.class);
	}

}
