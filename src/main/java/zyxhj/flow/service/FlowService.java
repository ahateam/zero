package zyxhj.flow.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.Module;
import zyxhj.flow.domain.Process;
import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessAsset;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.flow.domain.ProcessLog;
import zyxhj.flow.repository.ProcessActivityRepository;
import zyxhj.flow.repository.ProcessAssetReposition;
import zyxhj.flow.repository.ProcessDefinitionRepository;
import zyxhj.flow.repository.ProcessLogRepository;
import zyxhj.flow.repository.ProcessRepository;
import zyxhj.flow.repository.ModuleRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;

public class FlowService {

	private static Logger log = LoggerFactory.getLogger(FlowService.class);

	private ProcessRepository processRepository;
	private ProcessDefinitionRepository processDefinitionRepository;
	private ProcessActivityRepository processActivityRepository;
	
	private ProcessLogRepository processLogRepository;
	private ModuleRepository moduleRepository;
	private ProcessAssetReposition processAssetReposition;

	public FlowService() {
		try {
			processRepository = Singleton.ins(ProcessRepository.class);
			processDefinitionRepository = Singleton.ins(ProcessDefinitionRepository.class);
			processActivityRepository = Singleton.ins(ProcessActivityRepository.class);
			moduleRepository = Singleton.ins(ModuleRepository.class);
			processLogRepository = Singleton.ins(ProcessLogRepository.class);
			processAssetReposition = Singleton.ins(ProcessAssetReposition.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * ProcessDefinitionService
	 */
	/**
	 * 创建流程定义
	 */
	public void createProcessDefinition(DruidPooledConnection conn, Long moduleId, String title, JSONArray tags, JSONArray lanes) throws Exception {
		Long id = IDUtils.getSimpleId();
		ProcessDefinition pd = new ProcessDefinition();
		pd.moduleId = moduleId;
		pd.id = id;
		pd.tags = tags;
		pd.status = ProcessDefinition.STATUS_READY;
		pd.title = title;

		pd.lanes = lanes;

		processDefinitionRepository.insert(conn, pd);
	}

	/*
	 * 编辑表流程定义
	 */
	public int editProcessDefinition(DruidPooledConnection conn, Long id, String module, Byte status, JSONArray tags,
			String title, JSONArray lanes, JSONArray assets, JSONObject visualization) throws Exception {

		ProcessDefinition pd = new ProcessDefinition();
		pd.id = id;
		pd.tags = tags;
		pd.status = status;
		pd.title = title;

		pd.lanes = lanes;

		return processDefinitionRepository.updateByKey(conn, "id", id, pd, true);
	}

	/*
	 * 查询所有表流程定义
	 */
	public List<ProcessDefinition> queryProcessDefinition(DruidPooledConnection conn, String module, JSONArray tags, Integer count,
			Integer offset) throws Exception {
		return processDefinitionRepository.getListByANDKeys(conn, new String[] {"module","tags"}, new Object[] {module, tags.toArray()}, count, offset);
	}

	/*
	 * ProcessActivityService
	 */
	/**
	 * 设置流程节点，没有就增加，有就设置</br>
	 * 重名会被覆盖
	 */
	public void addPDActivity(DruidPooledConnection conn, Long pdId, String title, String part, JSONObject receivers, JSONArray actions) throws Exception {
		Long id = IDUtils.getSimpleId();
		ProcessActivity pa = new ProcessActivity();
		pa.pdId = pdId;
		pa.id = id;

		pa.title = title;
		pa.part = part;
		pa.receivers = receivers;
		pa.actions = actions;

		processActivityRepository.insert(conn, pa);
	}

	/**
	 * 删除流程节点
	 */
	public int delPDActivity(DruidPooledConnection conn, Long pdId, Long activityId) throws Exception {
		return processActivityRepository.deleteByANDKeys(conn, new String[]{"pd_id", "id"}, new Object[]{pdId, activityId});
	}

	/**
	 * 编辑流程节点
	 */
	public int editPDActivity(DruidPooledConnection conn, Long pdId, Long id, String title, String part, JSONObject receivers,
			JSONArray assets, JSONArray actions, JSONObject visualization) throws Exception {

		ProcessActivity pa = new ProcessActivity();
		pa.pdId = pdId;
		pa.id = id;

		pa.title = title;
		pa.part = part;
		pa.receivers = receivers;
		pa.actions = actions;

		return processActivityRepository.updateByANDKeys(null, new String[] {"pd_id", "id"}, new Object[] {pdId, id}, pa, true);
	}
	
	/*
	 * 查询所有流程节点
	 */
	public List<ProcessActivity> queryPDActivity(DruidPooledConnection conn, Integer count, Integer offset) throws ServerException {
		
		return processActivityRepository.getList(conn, count, offset);
		
	}
	/*
	 * ProcessService
	 */
	
	/*
	 * 创建Process流程实例 
	 */
	public void createProcess(DruidPooledConnection conn,Long pdid, String title, Long currActivityId, String remark ) throws ServerException {
		Long id = IDUtils.getSimpleId();
		Process pro = new Process();
		pro.pdId = pdid;
		pro.id = id;
		pro.title = title;
		pro.currActivityId = currActivityId;
		pro.timestamp = new Date();
		pro.remark = remark;
		processRepository.insert(conn, pro);
	}
	
	/*
	 * 编辑process流程实例
	 */
	public int editProcess(DruidPooledConnection conn, Long id,Long pdid, String title, Long currActivityId, String remark ) throws ServerException {
		Process pro = new Process();
		pro.pdId = pdid;
		pro.id = id;
		pro.title = title;
		pro.currActivityId = currActivityId;
		pro.timestamp = new Date();
		pro.remark = remark;
		return processRepository.updateByKey(conn, "id", id, pro, true);
	}
	
	/*
	 * 删除流程实例
	 */
	public int deleteProcess(DruidPooledConnection conn, Long id) throws ServerException {
		return processRepository.deleteByKey(conn, "id", id);
		
	}
	
	/*
	 * 查询所有流程实例
	 */
	public List<Process> getProcessList(DruidPooledConnection conn, Integer count, Integer offset) throws ServerException{
		return processRepository.getList(conn, count, offset);
	}
	
	/*
	 * 通过pdid查询下属process
	 */
	public List<Process> getProcessListByPdid(DruidPooledConnection conn,Long pdid, Integer count, Integer offset) throws ServerException{
		return processRepository.getListByKey(conn, "pd_id", pdid, count, offset);
	}
	
	/*
	 * 通过processid查询process
	 */
	public Process getProcessById(DruidPooledConnection conn,Long id) throws ServerException {
		return processRepository.getByKey(conn, "id", id);
	}
	
	
	/*
	 * processLogService
	 */
	/*
	 * 创建processLog
	 */
	public void createProcessLog(DruidPooledConnection conn,Long processId, String title
			,Long userid, String userName, String action, String actionDesc, JSONObject ext) throws ServerException {
		ProcessLog pl = new ProcessLog();
		Long id = IDUtils.getSimpleId();
		pl.processId = processId;
		pl.id = id;
		pl.title = title;
		pl.type = ProcessLog.TYPE_INFO;
		pl.userId = userid;
		pl.userName = userName;
		pl.action = action;
		pl.actionDesc = actionDesc;
		pl.ext = ext;
		processLogRepository.insert(conn, pl);
	}
	
	/*
	 * 编辑processLog
	 */
	public int editProcessLog(DruidPooledConnection conn,Long id,Long processId, String title
			,Long userid, String userName, String action, String actionDesc, JSONObject ext) throws ServerException {
		ProcessLog pl = new ProcessLog();
		pl.processId = processId;
		pl.id = id;
		pl.title = title;
		pl.type = ProcessLog.TYPE_INFO;
		pl.userId = userid;
		pl.userName = userName;
		pl.action = action;
		pl.actionDesc = actionDesc;
		pl.ext = ext;
		return processLogRepository.updateByKey(conn, "id", id, pl, true);
	}
	
	
	/*
	 * 查询所有processLog数据
	 */
	public List<ProcessLog> getProcessLogList(DruidPooledConnection conn, Integer count, Integer offset) 
			throws ServerException{
		return processLogRepository.getList(conn, count, offset);
	}
	
	
	/*
	 * 查询一个processid的所有processlog数据
	 */
	public List<ProcessLog> getProcessLogListByProcessId(DruidPooledConnection conn, Long processId, Integer count, Integer offset) 
			throws ServerException{
		return processLogRepository.getListByKey(conn, "process_id", processId, count, offset);
	}
	
	
	/*
	 * 通过userId查询所有processLog数据
	 */
	public List<ProcessLog> getProcessLogListByUserId(DruidPooledConnection conn, Long userId, Integer count, Integer offset) 
			throws ServerException{
		return processLogRepository.getListByKey(conn, "user_id", userId, count, offset);
	}
	
	/*
	 * ModuleService
	 */
	private static HashMap<Long, Module> SYS_MODULE_MAP = new HashMap<>();

	public static ArrayList<Module> SYS_MODULE_LIST = new ArrayList<>();

	static {
		// 添加module到系统中
		SYS_MODULE_MAP.put(Module.default_flow.id, Module.default_flow);

		Iterator<Module> it = SYS_MODULE_MAP.values().iterator();
		while (it.hasNext()) {
			SYS_MODULE_LIST.add(it.next());
		}
	}
	
	/*
	 * 创建自定义Module
	 */
	public void createModule(DruidPooledConnection conn, String name ) throws ServerException {
		Long id = IDUtils.getSimpleId();
		Module mod = new Module();
		mod.id = id;
		mod.name = name;		
		moduleRepository.insert(conn, mod);
	}
	
	/*
	 * 编辑自定义module
	 */
	public int editModule(DruidPooledConnection conn, Long id, String name) throws ServerException {
		Module mod = new Module();
		mod.id = id;
		mod.name = name;		
		return moduleRepository.updateByKey(conn, "id", id, mod, true);
	}
	/*
	 * 删除自定义module
	 */
	public int deleteModule(DruidPooledConnection conn, Long id) throws ServerException {
		return moduleRepository.deleteByKey(conn, "id", id);
	}
	/*
	 * 查询所有module
	 */
	public List<Module> getModuleList(DruidPooledConnection conn, Integer count, Integer offset) throws ServerException{
		return moduleRepository.getList(conn, count, offset);
	}
	/*
	 * 通过moduleId 查询module
	 */
	public Module getModuleListByKey(DruidPooledConnection conn, Long id) throws ServerException{
		return moduleRepository.getByKey(conn, "id", id);
	}
	/*
	 * 通过多个moduleId 查询module
	 */
	public List<Module> getModuleListByANDKeys(DruidPooledConnection conn, Long[] ids, Integer count, Integer offset) throws ServerException{
		String[] keys = new String[ids.length];
		for(int i = 0; i < ids.length; i++) {
			keys[i]="id";
		}
		return moduleRepository.getListByANDKeys(conn, keys, ids, count, offset);
	}
	
	/*
	 * processAsset
	 */

	/*
	 * 创建processAsset
	 */
	public void createProcessAsset(DruidPooledConnection conn, Byte type, Long ownerId, String name, Long annexId) throws ServerException {
		Long id = IDUtils.getSimpleId();
		
		ProcessAsset proA = new ProcessAsset(); 
		proA.type = ProcessAsset.TYPE_ACTIVITY;
		proA.ownerId = ownerId;
		proA.id = id;
		proA.name = name;
		proA.annexId = annexId;
		
		processAssetReposition.insert(conn, proA);
		
	}
	
}
