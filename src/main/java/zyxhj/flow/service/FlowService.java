package zyxhj.flow.service;

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
import zyxhj.flow.domain.Department;
import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessAssetDesc;
import zyxhj.flow.domain.ProcessDefinition;
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
import zyxhj.utils.data.ACTIVE;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

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
			userRepository = Singleton.ins(UserRepository.class);
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

		ProcessDefinition pd = new ProcessDefinition();
		pd.moduleKey = moduleKey;
		pd.id = IDUtils.getSimpleId();
		pd.title = title;
		pd.tags = tags;
		pd.status = ProcessDefinition.STATUS_READY;
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
			return definitionRepository.update(conn, EXP.INS().key("id", id), renew, true);
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
			return definitionRepository.getList(conn, EXP.INS().key("module_key", moduleKey), count, offset);
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
			List<ProcessDefinition> p = definitionRepository.getList(conn, EXP.INS().key("id", pdId), 1, 0);
			return p.get(0);
		}
	}

	@POSTAPI(path = "delPD", //
			des = "删除流程定义,将下属流程节点物理删除,将与流程定义、流程节点关联的资产定义数据物理删除", //
			ret = "更新影响的记录行数")
	public int delPD(@P(t = "流程定义编号") Long pdId) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			int status = definitionRepository.delete(conn, EXP.INS().andKey("id", pdId));
			assetDescRepository.delete(conn, EXP.INS().key("owner_id", pdId));
			if (status > 0) {
				List<ProcessActivity> palist = activityRepository.getList(conn, EXP.INS().key("pd_id", pdId), null,
						null, "id");
				if (palist.size() > 0) {
					for (ProcessActivity pa : palist) {
						assetDescRepository.delete(conn, EXP.INS().key("owner_id", pa.id));
					}
				}
				activityRepository.delete(conn, EXP.INS().key("pd_id", pdId));
			}
			return status;
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
			return definitionRepository.update(conn, EXP.INS().key("id", pdId), pd, true);
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
	public ProcessActivity createPDActivity(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "活动标题") String title, //
			@P(t = "所属泳道") String part, //
			@P(t = "接收者（departments部门，roles角色，users用户）") String receivers, //
			@P(t = "行为动作", r = false) String actions//
	) throws Exception {
		Long id = IDUtils.getSimpleId();
		ProcessActivity pa = new ProcessActivity();
		pa.pdId = pdId;
		pa.id = id;

		pa.title = title;
		pa.part = part;
		pa.receivers = receivers;
		pa.actions = actions;
		pa.active = ACTIVE.NORMAL;

		try (DruidPooledConnection conn = ds.getConnection()) {
			activityRepository.insert(conn, pa);
		}
		return pa;
	}

	@POSTAPI(//
			path = "setPDStartActivity", //
			des = "设置流程定义的起始节点" //
	) //
	public void setPDStartActivity(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "流程节点编号") Long startActivityId//
	) throws Exception {
		ProcessDefinition df = new ProcessDefinition();
		df.startActivityId = startActivityId;

		try (DruidPooledConnection conn = ds.getConnection()) {
			definitionRepository.update(conn, EXP.INS().key("id", pdId), df, true);
		}
	}

	@POSTAPI(//
			path = "setPDEndActivity", //
			des = "设置流程定义的结束节点" //
	) //
	public void setPDEndActivity(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "流程节点编号") Long activityId//
	) throws Exception {
		ProcessDefinition df = new ProcessDefinition();
		df.endActivityId = activityId;

		try (DruidPooledConnection conn = ds.getConnection()) {
			definitionRepository.update(conn, EXP.INS().key("id", pdId), df, true);
		}
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
		renew.active = ACTIVE.DELETED;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return activityRepository.update(conn, EXP.INS().key("pd_id", pdId).andKey("id", activityId), renew, true);
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
			@P(t = "接收者（departments部门，roles角色，users用户）") String receivers, //
			@P(t = "行为动作") String actions//
	) throws Exception {

		ProcessActivity renew = new ProcessActivity();
		renew.title = title;
		renew.part = part;
		renew.receivers = receivers;
		renew.actions = actions;

		try (DruidPooledConnection conn = ds.getConnection()) {

			return activityRepository.update(conn, EXP.INS().key("pd_id", pdId).andKey("id", id), renew, true);

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
			return activityRepository.getList(conn, EXP.INS().key("pd_id", pdId).andKey("active", ACTIVE.NORMAL), count,
					offset);
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
			List<ProcessActivity> a = activityRepository.getList(conn,
					EXP.INS().key("pd_id", pdid).andKey("id", activityid), 1, 0);
			return a.get(0);
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
			return activityRepository.update(conn, EXP.INS().key("id", activityId), pa, true);
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

	/**
	 * 返回值暂定为空，可能会返回desc对象
	 */
	@POSTAPI(//
			path = "createAssetDesc", //
			des = "创建资产需求描述(资产定义)"//
	)
	public void createAssetDesc(//
			@P(t = "资产所属编号（流程定义编号或流程节点编号）") Long ownerId, //
			@P(t = "资产类型") String type, //
			@P(t = "资产名称") String name, //
			@P(t = "是否必须") Boolean necessary, //
			@P(t = "资产说明") String remark, //
			@P(t = "模板信息", r = false) String template, //
			@P(t = "模板信息", r = false) String uri//
	) throws Exception {
		ProcessAssetDesc pad = new ProcessAssetDesc();
		pad.id = IDUtils.getSimpleId();
		pad.ownerId = ownerId;
		pad.type = type;
		pad.name = name;
		pad.necessary = necessary;
		pad.remark = remark;
		pad.template = template;
		pad.uri = uri;

		try (DruidPooledConnection conn = ds.getConnection()) {
			assetDescRepository.insert(conn, pad);
		}
	}

	@POSTAPI(//
			path = "editAssetDesc", //
			des = "修改资产需求描述(资产定义)数据", //
			ret = "受影响行数")
	public int editAssetDesc(//
			@P(t = "资产所属编号（流程定义编号或流程节点编号）") Long ownerId, //
			@P(t = "资产定义编号") Long id, //
			@P(t = "资产类型") String type, //
			@P(t = "资产名称") String name, //
			@P(t = "是否必须") Boolean necessary, //
			@P(t = "资产说明") String remark, //
			@P(t = "模板信息", r = false) String template, //
			@P(t = "模板信息", r = false) String uri//
	) throws Exception {
		ProcessAssetDesc pad = new ProcessAssetDesc();
		pad.type = type;
		pad.name = name;
		pad.necessary = necessary;
		pad.remark = remark;
		pad.template = template;
		pad.uri = uri;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return assetDescRepository.update(conn, EXP.INS().key("owner_id", ownerId).andKey("id", id), pad, true);
		}
	}

	@POSTAPI(//
			path = "delAssetDesc", //
			des = "删除资产需求描述(资产定义)", //
			ret = "受影响行数")
	public void delAssetDesc(@P(t = "资产定义编号") JSONArray ids//
	) {
		try (DruidPooledConnection conn = ds.getConnection()) {
			if (ids != null) {
				for (int i = 0; i < ids.size(); i++) {
					System.out.println(ids.get(i));
					assetDescRepository.delete(conn, EXP.INS().key("id", ids.get(i)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@POSTAPI(//
			path = "getAssetDescList", //
			des = "得到资产需求描述(资产定义)列表", //
			ret = "List<ProcessAssetDesc>")
	public List<ProcessAssetDesc> getAssetDescList(//
			@P(t = "资产所属编号（流程定义编号或流程节点编号）") Long ownerId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return assetDescRepository.getList(conn, EXP.INS().key("owner_id", ownerId), count, offset);
		}
	}

	@POSTAPI(//
			path = "getUserList", //
			des = "得到用户列表", //
			ret = "List<User>"//
	)
	public List<User> getUserList(//
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return userRepository.getList(conn, null, count, offset, "id", "real_name");
		}

	}

	@POSTAPI(//
			path = "getUserRoleList", //
			des = "得到角色列表", //
			ret = "List<UserRole>"//
	)
	public List<UserRole> getUserRoleList(//
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return roleRepository.getList(conn, null, count, offset, "id", "name");
		}
	}

	@POSTAPI(//
			path = "getDepartmentList", //
			des = "得到部门列表", //
			ret = "List<Department>"//
	)
	public List<Department> getDepartmentList(//
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return departmentRepository.getList(conn, null, count, offset, "id", "name");
		}
	}
}
