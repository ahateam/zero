package zyxhj.flow.repository;

import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.utils.data.rds.RDSRepository;

public class ProcessDefinitionRepository extends RDSRepository<ProcessDefinition> {

	public ProcessDefinitionRepository() {
		super(ProcessDefinition.class);
	}

}
