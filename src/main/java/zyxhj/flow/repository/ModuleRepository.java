package zyxhj.flow.repository;

import zyxhj.flow.domain.Module;
import zyxhj.utils.data.rds.RDSRepository;

public class ModuleRepository extends RDSRepository<Module> {

	public ModuleRepository() {
		super(Module.class);
	}

	
}