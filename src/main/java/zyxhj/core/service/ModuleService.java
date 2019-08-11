package zyxhj.core.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.Module;
import zyxhj.flow.repository.ModuleRepository;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class ModuleService extends Controller {

	private static Logger log = LoggerFactory.getLogger(ModuleService.class);

	private DruidDataSource ds;

	private ModuleRepository moduleRepository;

	public ModuleService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");

			moduleRepository = Singleton.ins(ModuleRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * 创建自定义Module
	 */
	@POSTAPI(//
			path = "createModule", //
			des = "创建自定义模块"//
	)
	public void createModule(//
			@P(t = "自定义模块关键字（不可重复）") String key, //
			@P(t = "自定义模块名称") String name//
	) throws Exception {
		Module mod = new Module();
		mod.key = key;
		mod.sys = false;// 自定义
		mod.name = name;

		try (DruidPooledConnection conn = ds.getConnection()) {
			moduleRepository.insert(conn, mod);
		}
	}

	/*
	 * 编辑自定义模块
	 */
	@POSTAPI(//
			path = "editModule", //
			des = "编辑自定义模块", //
			ret = "更新影响的记录行数")
	public int editModule(//
			@P(t = "模块编号") Long key, //
			@P(t = "自定义模块名称") String name//
	) throws Exception {
		Module renew = new Module();
		renew.name = name;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return moduleRepository.update(conn,EXP.INS().key("key", key), renew, true);
			
		}
	}

	/**
	 * 根据count和offset取子列表</br>
	 * 需要小心参数是否越界
	 */
	private static <X> List<X> subList(List<X> src, int count, int offset) {
		int size = src.size();
		int start = offset;
		int end = offset + count;
		if (offset >= size) {
			return new ArrayList<>();
		} else {
			return src.subList(start, Math.min(size, end));
		}
	}

	/*
	 * 查询所有module
	 */
	@POSTAPI(//
			path = "getModuleList", //
			des = "查询模块列表", //
			ret = "模块列表"//
	)
	public List<Module> getModuleList(//
			@P(t = "是否系统模块（可选参数），true表示系统模块，false表示自定义模块") Boolean sys, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			if (sys) {
				// 返回系统模块
				ArrayList<Module> list = new ArrayList<>(Module.SYS_MODULE_MAP.values());
				return subList(list, count, offset);
			} else {
				// 返回自定义模块
				return moduleRepository.getList(conn, null, count, offset);
			}
		}
	}
}
