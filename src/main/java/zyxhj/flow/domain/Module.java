package zyxhj.flow.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_module")
public class Module {
	
	private static Module buildSysModule(Long moduleId,String name) {
		Module module = new Module();
		module.id = moduleId;
		module.name = name;
		return module;
	}
	
	private static Long temp = 100L;//自增编号
	
	public static final Module default_flow = buildSysModule(temp++,"默认流程组件");
	 
	/**
	 * 
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;
	/*
	 * 
	 */

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;
}
