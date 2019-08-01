package zyxhj.flow.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.User;
import zyxhj.core.domain.UserRole;
import zyxhj.core.repository.UserRepository;
import zyxhj.core.repository.UserRoleRepository;
import zyxhj.core.service.UserRoleService;
import zyxhj.core.service.UserService;
import zyxhj.flow.domain.Department;
import zyxhj.flow.domain.Process;
import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessActivity.Action;
import zyxhj.flow.domain.ProcessActivity.Receiver;
import zyxhj.flow.domain.ProcessAsset;
import zyxhj.flow.domain.ProcessAssetDesc;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.flow.domain.ProcessLog;
import zyxhj.flow.repository.DepartmentRepository;
import zyxhj.flow.repository.ProcessActivityRepository;
import zyxhj.flow.repository.ProcessAssetDescRepository;
import zyxhj.flow.repository.ProcessAssetRepository;
import zyxhj.flow.repository.ProcessDefinitionRepository;
import zyxhj.flow.repository.ProcessLogRepository;
import zyxhj.flow.repository.ProcessRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;

public class FlowService extends Controller {

	private static Logger log = LoggerFactory.getLogger(FlowService.class);

	private DruidDataSource ds;

	private ProcessAssetDescRepository assetDescRepository;
	private ProcessRepository processRepository;
	private ProcessDefinitionRepository definitionRepository;
	private ProcessActivityRepository activityRepository;
	private ProcessLogRepository processLogRepository;
	private ProcessAssetRepository processAssetRepository;
	private DepartmentRepository departmentRepository;
	private UserRepository userRepository;
	private UserRoleRepository roleRepository;

	public FlowService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");

			processRepository = Singleton.ins(ProcessRepository.class);
			definitionRepository = Singleton.ins(ProcessDefinitionRepository.class);
			activityRepository = Singleton.ins(ProcessActivityRepository.class);
			processLogRepository = Singleton.ins(ProcessLogRepository.class);
			processAssetRepository = Singleton.ins(ProcessAssetRepository.class);
			assetDescRepository = Singleton.ins(ProcessAssetDescRepository.class);
			departmentRepository = Singleton.ins(DepartmentRepository.class);
			userRepository  = Singleton.ins(UserRepository.class);
			roleRepository = Singleton.ins(UserRoleRepository.class);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@POSTAPI(//
			path = "createPD", //
			des = "创建流程定义", //
			ret = "ProcessDefinition实例" //
	)
	public ProcessDefinition createPD(//
			@P(t = "模块关键字") String moduleKey, //
			@P(t = "流程定义标题") String title, //
			@P(t = "标签列表") JSONArray tags, //
			@P(t = "流程图泳道名称列表，泳道名称不可重复") JSONArray lanes//
	) throws Exception {

		Long id = IDUtils.getSimpleId();

		ProcessDefinition pd = new ProcessDefinition();
		pd.moduleKey = moduleKey;
		pd.id = id;
		pd.title = title;
		pd.tags = tags;
		pd.status = ProcessDefinition.STATUS_READY;
		pd.assetDesc = new ArrayList<ProcessAssetDesc>();
		pd.visual = new JSONObject();
		pd.lanes = lanes;

		try (DruidPooledConnection conn = ds.getConnection()) {
			definitionRepository.insert(conn, pd);
		}
		return pd;
	}

	@POSTAPI(//
			path = "editPD", //
			des = "编辑流程定义", //
			ret = "所影响记录行数"//
	)
	public int editPD(@P(t = "流程定义表编号") Long id, //
			@P(t = "流程定义标题") String title, //
			@P(t = "流程定义状态") Byte status, //
			@P(t = "标签列表") JSONArray tags, //
			@P(t = "流程图泳道名称列表，泳道名称不可重复") JSONArray lanes//
	) throws Exception {

		ProcessDefinition renew = new ProcessDefinition();
		renew.tags = tags;
		renew.status = status;
		renew.title = title;

		renew.lanes = lanes;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return definitionRepository.updateByKey(conn, "id", id, renew, true);
		}
	}

	@POSTAPI(//
			path = "getPDList", //
			des = "查询当前模块的所有流程定义", //
			ret = "List<ProcessDefinition>"//
	)
	public List<ProcessDefinition> getPDList(//
			@P(t = "模块关键字") String moduleKey, //
			Integer count, //
			Integer offset//
	) throws Exception {
		
		try (DruidPooledConnection conn = ds.getConnection()) {
			return definitionRepository.getListByKey(conn, "module_key", moduleKey, count, offset);
		}
	}

	@POSTAPI(//
			path = "getPDById", //
			des = "查询当前流程定义", //
			ret = "ProcessDefinition"//
	)
	public ProcessDefinition getPDById(@P(t = "流程定义编号") Long pdId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return definitionRepository.getByKey(conn, "id", pdId);
		}
	}

	@POSTAPI(path = "delPD", //
			des = "删除流程定义", //
			ret = "更新影响的记录行数")
	public int delPD(@P(t = "流程定义编号") Long pdId) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return definitionRepository.deleteByKey(conn, "id", pdId);
		}
	}

	@POSTAPI(//
			path = "setPDVisual", //
			des = "设置流程定义的全局样式信息", //
			ret = "所影响的记录行数")
	public int setPDVisual(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "流程定义的全局样式信息") JSONObject visual//
	) throws Exception {
		ProcessDefinition pd = new ProcessDefinition();
		pd.visual = visual;
		try (DruidPooledConnection conn = ds.getConnection()) {
			return definitionRepository.updateByKey(conn, "id", pdId, pd, true);
		}
	}

	@POSTAPI(//
			path = "setPDAssetDescList", //
			des = "为流程定义设置资产需求描述", //
			ret = "更新影响的记录行数")
	public int setPDAssetDescList(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "要设置的列表") List<ProcessAssetDesc> assetDescList//
	) throws Exception {

		for (int i = 0; i < assetDescList.size(); i++) {
			assetDescList.get(i).id = IDUtils.getSimpleId();
		}

		ProcessDefinition pd = new ProcessDefinition();
		pd.assetDesc = assetDescList;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return definitionRepository.updateByKey(conn, "id", pdId, pd, true);
		}
		
	}

	///////////////////////////////////////
	// Activity
	///////////////////////////////////////

	@POSTAPI(//
			path = "createPDActivity", //
			des = "创建流程节点", //
			ret = "ProcessActivity实例"//
	)
	public Long createPDActivity(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "活动标题") String title, //
			@P(t = "所属泳道") String part, //
			@P(t = "接收者（departments部门，roles角色，users用户）") List<Receiver> receivers, //
			@P(t = "行为动作") List<Action> actions//
	) throws Exception {
		Long id = IDUtils.getSimpleId();
		ProcessActivity pa = new ProcessActivity();
		pa.pdId = pdId;
		pa.id = id;

		pa.title = title;
		pa.part = part;
		pa.receivers = receivers;
		pa.actions = actions;
		pa.active = ProcessActivity.ACTIVE_DELETE_N;
		pa.assetDesc = new ArrayList<ProcessAssetDesc>();

		try (DruidPooledConnection conn = ds.getConnection()) {
			activityRepository.insert(conn, pa);
		}

		return pa.id;
	}

	/**
	 * 删除流程节点
	 *
	 */
	@POSTAPI(//
			path = "delPDActivity", //
			des = "删除流程节点", //
			ret = "所影响记录行数")
	public int delPDActivity(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "流程节点编号") Long activityId//
	) throws Exception {
		ProcessActivity renew = new ProcessActivity();
		renew.active = ProcessActivity.ACTIVE_DELETE_Y;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return activityRepository.updateByANDKeys(conn, new String[] { "pd_id", "id" },
					new Object[] { pdId, activityId }, renew, true);
		}

	}

	@POSTAPI(//
			path = "editPDActivity", //
			des = "编辑流程节点", //
			ret = "所影响记录行数")
	public int editPDActivity(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "流程节点编号") Long id, //
			@P(t = "活动标题") String title, //
			@P(t = "所属泳道") String part, //
			@P(t = "接收者（departments部门，roles角色，users用户）") List<Receiver> receivers, //
			@P(t = "行为动作") List<Action> actions//
	) throws Exception {

		ProcessActivity renew = new ProcessActivity();
		renew.title = title;
		renew.part = part;
		renew.receivers = receivers;
		renew.actions = actions;

		try (DruidPooledConnection conn = ds.getConnection()) {

			return activityRepository.updateByANDKeys(conn, new String[] { "pd_id", "id" }, new Object[] { pdId, id },
					renew, true);

		}
	}

	@POSTAPI(//
			path = "getPDActivityList", //
			des = "获取当前流程定义编号pdId下的所有流程节点", //
			ret = "List<ProcessActivity>"//
	)
	public List<ProcessActivity> getPDActivityList(//
			@P(t = "流程定义编号") Long pdId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return activityRepository.getListByANDKeys(conn, new String[] {"pd_id", "active"}, new Object[] {pdId, 0}, count, offset);
		}
	}

	@POSTAPI(//
			path = "getPDActivityById", //
			des = "通过流程定义编号与流程节点编号得到流程节点", //
			ret = "ProcessActivity"//
	)
	public ProcessActivity getPDActivityById(@P(t = "流程定义编号") Long pdid, //
			@P(t = "流程节点编号") Long activityid//
	) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {
			return activityRepository.getByANDKeys(conn, new String[] { "pd_id", "id" },
					new Object[] { pdid, activityid });
		} catch (ServerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@POSTAPI(//
			path = "setPDActivityVisual", //
			des = "设置流程节点样式信息", //
			ret = "所影响的记录行数"//
	)
	public int setPDActivityVisual(//
			@P(t = "流程定义编号") Long activityId, //
			@P(t = "activity节点样式信息") JSONObject visual//
	) throws Exception {
		ProcessActivity pa = new ProcessActivity();
		pa.visual = visual;
		try (DruidPooledConnection conn = ds.getConnection()) {
			return activityRepository.updateByKey(conn, "id", activityId, pa, true);
		}
	}

	@POSTAPI(//
			path = "setPDActivityVisualList", //
			des = "设置流程节点样式信息", //
			ret = "所影响的记录行数"//
	)
	public void setPDActivityVisualList(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "流程节点编号") JSONArray activityVisualList, //
			Integer count, Integer offset) throws Exception {

		for (int i = 0; i < activityVisualList.size(); i++) {
			JSONObject jo = activityVisualList.getJSONObject(i);

			Long id = jo.getLong("id");
			try {
				setPDActivityVisual(id, jo);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	///////////////////////////////////////
	// Activity
	///////////////////////////////////////

	@POSTAPI(//
			path = "createProcess", //
			des = "创建流程实例", //
			ret = "Process实例")
	public Process createProcess(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "流程节点编号") Long activityId, //
			@P(t = "流程标题") String title, //
			String remark//
	) throws Exception {
		Process pro = new Process();
		pro.pdId = pdId;
		pro.id = IDUtils.getSimpleId();
		pro.title = title;
		pro.currActivityId = activityId;
		pro.timestamp = new Date();
		pro.remark = remark;
		pro.state = Process.STATE_USING;
		pro.active = Process.ACTIVE_DELETE_N;

		try (DruidPooledConnection conn = ds.getConnection()) {
			processRepository.insert(conn, pro);
		}
		return pro;
	}

	@POSTAPI(//
			path = "editProcess", //
			des = "编辑流程实例（Process）", //
			ret = "state ---- int")
	public int editProcess(//
			@P(t = "processId") Long processId, //
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "流程节点编号") Long activityId, //
			@P(t = "流程标题") String title, //
			String remark, //
			@P(t = "流程实例状态(0-->使用中，1-->等待中，2-->已结束)") Byte state//
	) throws Exception {
		Process pro = new Process();

		pro.title = title;
		pro.currActivityId = activityId;
		pro.timestamp = new Date();
		pro.remark = remark;
		pro.state = state;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return processRepository.updateByANDKeys(conn, new String[] { "pd_id", "id" },
					new Object[] { pdId, processId }, pro, true);
		}
	}

	@POSTAPI(//
			path = "delProcess", //
			des = "删除Process流程实例", //
			ret = "更新影响的记录行数")
	public int delProcess(//
			@P(t = "processId ") Long id//
	) throws Exception {
		Process pro = new Process();
		pro.active = Process.ACTIVE_DELETE_Y;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return processRepository.updateByKey(conn, "id", id, pro, true);
		}
	}

	@POSTAPI(//
			path = "getProcessListByPDId", //
			des = "通过流程定义编号查询下属流程实例", //
			ret = "List<Process>"//
	)
	public List<Process> getProcessListByPDId(//
			@P(t = "流程定义编号") Long pdId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return processRepository.getListByANDKeys(conn, new String[] {"pd_id","active"}, new Object[] {pdId, 0} , count, offset);
		}
	}

	/**
	 * TODO 根据复杂条件查询Process列表</br>
	 * 如：部门，个人，状态，等
	 */

	@POSTAPI(//
			path = "getProcessById", //
			des = "通过流程实例编号精确查询流程实例", //
			ret = "Process实例")
	public Process getProcessById(//
			@P(t = "流程实例编号") Long id//
	) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {
			return processRepository.getByKey(conn, "id", id);
		}
	}

	@POSTAPI(//
			path = "createProcessLog", //
			des = "添加流程操作日志"//
	)
	public void createProcessLog(//
			@P(t = "流程编号") Long processId, //
			@P(t = "标题") String title, //
			@P(t = "使用者编号") Long userid, //
			@P(t = "使用者名称") String userName, //
			@P(t = "行为或活动") String action, //
			@P(t = "行为或活动说明") String actionDesc, //
			@P(t = "记录扩展数据") JSONObject ext//
	) throws Exception {
		// 最好做成异步

		ProcessLog pl = new ProcessLog();
		pl.processId = processId;
		pl.id = IDUtils.getSimpleId();
		pl.title = title;
		pl.type = ProcessLog.TYPE_INFO;
		pl.userId = userid;
		pl.userName = userName;
		pl.action = action;
		pl.actionDesc = actionDesc;
		pl.ext = ext;

		try (DruidPooledConnection conn = ds.getConnection()) {
			processLogRepository.insert(conn, pl);
		}

	}

	@POSTAPI(//
			path = "getProcessLogList", //
			des = "根据processId查询流程抄作日志", //
			ret = "List<ProcessLog>")
	public List<ProcessLog> getProcessLogList(//
			@P(t = "processId流程编号") Long processId, //
			Integer count, //
			Integer offset//
	) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {

			return processLogRepository.getProcessLogList(conn, processId, count, offset);
		}
	}

	@POSTAPI(//
			path = "createPDAsset", //
			des = "添加流程定义全局资产（附件，文件，表单等）", //
			ret = "ProcessAsset实例"//
	)
	public ProcessAsset createPDAsset(//
			@P(t = "流程定义ProcessDefinition编号") Long pdId, //
			@P(t = "资产名称") String name, //
			@P(t = "附件编号，可扩展") Long annexId//
	) throws Exception {
		return createAsset(ProcessAsset.TYPE_DEFINITON, pdId, name, annexId);
	}

	@POSTAPI(//
			path = "createActivityAsset", //
			des = "添加流程定义全局资产（附件，文件，表单等）", //
			ret = "ProcessAsset实例"//
	)
	public ProcessAsset createActivityAsset(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "流程节点Activity编号") Long activityId, //
			@P(t = "资产类型") String type, //
			@P(t = "资产名称") String name, //
			@P(t = "附件编号，可扩展") Long annexId, //
			@P(t = "是否必须") Boolean necessary//
	) throws Exception {

		return createAsset(ProcessAsset.TYPE_ACTIVITY, activityId, name, annexId);
	}

	private ProcessAsset createAsset(Byte type, Long ownerId, String name, Long annexId) throws Exception {
		ProcessAsset pa = new ProcessAsset();
		pa.type = type;
		pa.ownerId = ownerId;
		pa.id = IDUtils.getSimpleId();
		pa.name = name;
		pa.annexId = annexId;

		try (DruidPooledConnection conn = ds.getConnection()) {
			processAssetRepository.insert(conn, pa);
		}
		return pa;
	}

	@POSTAPI(//
			path = "editAsset", //
			des = "编辑流程资产（附件，文件，表单等）", //
			ret = "更新影响的记录行数"//
	)
	public int editAsset(//
			@P(t = "资产编号") Long assetId, //
			@P(t = "资产名称") String name, //
			@P(t = "附件编号，可扩展") Long annexId//
	) throws Exception {
		ProcessAsset renew = new ProcessAsset();
		renew.name = name;
		renew.annexId = annexId;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return processAssetRepository.updateByKey(conn, "id", assetId, renew, true);
		}
	}

	@POSTAPI(//
			path = "getPDAssetList", //
			des = "查询流程定义所属流程资产", //
			ret = "List<ProcessAsset>"//
	)
	public List<ProcessAsset> getPDAssetList(//
			@P(t = "所属流程定义ProcessDefinition编号") Long pdId, //
			@P(t = "所属流程节点Activity编号，不填表示只查看流程定义中的全局资产", r = false) JSONArray activityIds, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return processAssetRepository.getListByANDKeys(conn, new String[] { "type", "owner_id" },
					new Object[] { ProcessAsset.TYPE_DEFINITON, pdId }, count, offset);
		}
	}

	@POSTAPI(//
			path = "getActivityAssetList", //
			des = "查询流程节点所属流程资产", //
			ret = "List<ProcessAsset>"//
	)
	public List<ProcessAsset> getActivityAssetList(//
			@P(t = "所属流程节点Activity编号") Long activityId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return processAssetRepository.getListByANDKeys(conn, new String[] { "type", "owner_id" },
					new Object[] { ProcessAsset.TYPE_ACTIVITY, activityId }, count, offset);
		}
	}

	@POSTAPI(//
			path = "delAsset", //
			des = "删除流程资产", //
			ret = "更新影响的记录行数"//
	)
	public int delAsset(//
			@P(t = "assetId") Long assetId//
	) throws Exception {
		// 流程资产可以真删除
		try (DruidPooledConnection conn = ds.getConnection()) {
			return processAssetRepository.deleteByKey(conn, "id", assetId);
		}
	}

	@POSTAPI(//
			path = "createAssetDesc", //
			des = "创建资产需求描述(资产定义)"//
	)
	public void createAssetDesc(//
			@P(t = "资产所属编号（流程定义编号或流程节点编号）") Long ownerId,//
			@P(t = "资产类型") String type,//
			@P(t = "资产名称") String name,//
			@P(t = "是否必须") Boolean necessary,//
			@P(t = "资产说明") String remark//
			) throws Exception {
		ProcessAssetDesc pad = new ProcessAssetDesc();
		pad.id = IDUtils.getSimpleId();
		pad.ownerId = ownerId;
		pad.type = type;
		pad.name = name;
		pad.necessary = necessary;
		pad.remark = remark;
		
		try (DruidPooledConnection conn = ds.getConnection()) {
			assetDescRepository.insert(conn, pad);
		}
	}
	
	@POSTAPI(//
			path = "editAssetDesc", //
			des = "修改资产需求描述(资产定义)数据",//
			ret = "受影响行数"
	)
	public int editAssetDesc(//
			@P(t = "资产所属编号（流程定义编号或流程节点编号）") Long ownerId,//
			@P(t = "资产定义编号") Long id,//
			@P(t = "资产类型") String type,//
			@P(t = "资产名称") String name,//
			@P(t = "是否必须") Boolean necessary,//
			@P(t = "资产说明") String remark//
			) throws Exception {
		ProcessAssetDesc pad = new ProcessAssetDesc();
		pad.type = type;
		pad.name = name;
		pad.necessary = necessary;
		pad.remark = remark;
		
		try (DruidPooledConnection conn = ds.getConnection()) {
			return assetDescRepository.updateByANDKeys(conn, new String[] {"owner_id", "id"}, new Object[] {ownerId, id}, pad, true);
		} 
	}
	
	@POSTAPI(//
			path = "delAssetDesc", //
			des = "删除资产需求描述(资产定义)",//
			ret = "受影响行数"
	)
	public void delAssetDesc(
			@P(t = "资产所属编号（流程定义编号或流程节点编号）") Long ownerId,//
			@P(t = "资产定义编号") Long id//
			) {
		try (DruidPooledConnection conn = ds.getConnection()) {
			assetDescRepository.deleteByANDKeys(conn, new String[] { "owner_id", "id" }, new Object[] {ownerId, id});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@POSTAPI(//
			path = "getAssetDescList", //
			des = "得到资产需求描述(资产定义)列表",//
			ret = "List<ProcessAssetDesc>"
	)
	public List<ProcessAssetDesc> getAssetDescList(
			@P(t = "资产所属编号（流程定义编号或流程节点编号）") Long ownerId,
			Integer count,
			Integer offset
			) throws Exception{
		try (DruidPooledConnection conn = ds.getConnection()) {
			return assetDescRepository.getListByKey(conn, "owner_id", ownerId, count, offset);
		} 
	}
	

	@POSTAPI(//
			path = "getUserList", //
			des = "得到用户列表",//
			ret = "List<User>"//
	)
	public List<User> getUserList(//
			Integer count,//
			Integer offset//
			) throws Exception{
		try (DruidPooledConnection conn = ds.getConnection()) {
			List<User> ulist =  userRepository.getList(conn, count, offset);
			for(User u : ulist) {
				u.pwd = null;
			}
			return ulist;
		} 

	}
	
	@POSTAPI(//
			path = "getUserRoleList", //
			des = "得到角色列表",//
			ret = "List<UserRole>"//
	)
	public List<UserRole> getUserRoleList(//
			Integer count,//
			Integer offset//
			) throws Exception{
		try (DruidPooledConnection conn = ds.getConnection()) {
			return roleRepository.getList(conn, count, offset);
		} 
	}
	
	
	@POSTAPI(//
			path = "getDepartmentList", //
			des = "得到部门列表",//
			ret = "List<Department>"//
	)
	public List<Department> getDepartmentList(//
			Integer count,//
			Integer offset//
			) throws Exception{
		try (DruidPooledConnection conn = ds.getConnection()) {
			return departmentRepository.getList(conn, count, offset);
		} 
	}
	
}
