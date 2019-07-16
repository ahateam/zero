package zyxhj.flow.repository;

import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.utils.data.ts.TSRepository;

public class ProcessDefinitionRepository extends TSRepository<ProcessDefinition> {

	public ProcessDefinitionRepository() {
		super(ProcessDefinition.class);
	}

}
