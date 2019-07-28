package zyxhj.core.domain;

import java.util.TreeMap;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_module")
public class Module {

	/*
	 * 模块关键字
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String key;

	@RDSAnnField(column = RDSAnnField.BOOLEAN)
	public Boolean sys;

	/*
	 * 模块名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/////////////////////////////////////
	/////////////////////////////////////
	/////////////////////////////////////

	private static Module buildSysModule(String key, String name) {
		Module m = new Module();
		m.key = key;
		m.sys = true;
		m.name = name;
		return m;
	}

	public static final Module USER = buildSysModule("user", "用户模块");
	public static final Module FLOW = buildSysModule("flow", "流程模块");
	public static final Module CMS = buildSysModule("cms", "内容模块");
	public static final Module ANNEX = buildSysModule("annex", "附件模块");

	/**
	 * 有序，方便查看</br>
	 * values()，可以直接获取数组</br>
	 */
	public static final TreeMap<String, Module> SYS_MODULE_MAP = new TreeMap<>();

	static {
		SYS_MODULE_MAP.put(USER.key, USER);
		SYS_MODULE_MAP.put(FLOW.key, FLOW);
		SYS_MODULE_MAP.put(CMS.key, CMS);
		SYS_MODULE_MAP.put(ANNEX.key, ANNEX);
	}
}
