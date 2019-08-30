package zyxhj.flow.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.User;
import zyxhj.core.repository.UserRepository;
import zyxhj.flow.domain.Process;
import zyxhj.flow.domain.ProcessAction;
import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessActivity.Action;
import zyxhj.flow.domain.ProcessActivityGroup;
import zyxhj.flow.domain.ProcessActivityGroup.SubActivity;
import zyxhj.flow.domain.ProcessAsset;
import zyxhj.flow.domain.ProcessAssetDesc;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.flow.domain.ProcessLog;
import zyxhj.flow.domain.TableData;
import zyxhj.flow.repository.ProcessActionRepository;
import zyxhj.flow.repository.ProcessActivityGroupRepository;
import zyxhj.flow.repository.ProcessActivityRepository;
import zyxhj.flow.repository.ProcessAssetRepository;
import zyxhj.flow.repository.ProcessDefinitionRepository;
import zyxhj.flow.repository.ProcessLogRepository;
import zyxhj.flow.repository.ProcessRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataConst;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class ProcessService extends Controller {

	private static Logger log = LoggerFactory.getLogger(ProcessService.class);

	private DruidDataSource ds;

	private ProcessRepository processRepository;
	private ProcessActivityRepository activityRepository;
	private ProcessLogRepository processLogRepository;
	private ProcessAssetRepository processAssetRepository;
	private ProcessDefinitionRepository definitionRepository;
	private ProcessActionRepository processActionRepository;
	private ProcessActivityGroupRepository processActivityGroupRepository;

	private UserRepository userRepository;

	private FlowService flowService;

	public ProcessService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");

			processRepository = Singleton.ins(ProcessRepository.class);
			activityRepository = Singleton.ins(ProcessActivityRepository.class);
			processLogRepository = Singleton.ins(ProcessLogRepository.class);
			processAssetRepository = Singleton.ins(ProcessAssetRepository.class);
			definitionRepository = Singleton.ins(ProcessDefinitionRepository.class);
			processActionRepository = Singleton.ins(ProcessActionRepository.class);
			userRepository = Singleton.ins(UserRepository.class);
			processActivityGroupRepository = Singleton.ins(ProcessActivityGroupRepository.class);

			flowService = Singleton.ins(FlowService.class);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	///////////////////////////////////////
	// Process
	///////////////////////////////////////

	@POSTAPI(//
			path = "createProcess", //
			des = "创建流程实例", //
			ret = "Process实例")
	public Process createProcess(//
			@P(t = "流程定义编号") Long pdId, //
			@P(t = "使用者编号", r = false) Long userId, //
			@P(t = "流程标题") String title, //
			String remark//
	) throws Exception {
		Process pro = new Process();
		pro.pdId = pdId;
		pro.id = IDUtils.getSimpleId();
		pro.title = title;
		pro.timestamp = new Date();
		pro.remark = remark;
		pro.state = Process.STATE_USING;
		pro.active = DataConst.ACTIVE_NORMAL;

		try (DruidPooledConnection conn = ds.getConnection()) {

			// 设置PD的起点作为当前节点
			ProcessDefinition pd = definitionRepository.get(conn, EXP.INS().key("id", pdId));
			pro.currActivityId = pd.startActivityId;

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
			return processRepository.update(conn, EXP.INS().key("pd_id", pdId).andKey("id", processId), pro, true);
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
		pro.active = DataConst.ACTIVE_DELETED;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return processRepository.update(conn, EXP.INS().key("id", id), pro, true);
		}
	}

	@POSTAPI(//
			path = "getProcessInfo", //
			des = "得到流程实例所需数据Process，Activity，definition信息", //
			ret = "Process实例")
	public JSONObject getProcessInfo(//
			@P(t = "使用者编号", r = false) Long userId, //
			@P(t = "流程定义编号") Long processId //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {

			Process p = processRepository.get(conn, EXP.INS().key("id", processId));

			// 获取对应Definition的信息
			ProcessDefinition pd = definitionRepository.get(conn, EXP.INS().key("id", p.pdId));

			// 原始版本
			// 获取当前Activity的信息
//			ProcessActivity pa = activityRepository.get(conn, EXP.INS().key("pd_id", p.pdId)
//					.andKey("id", p.currActivityId).andKey("first", 0).andKey("active", DataConst.ACTIVE_NORMAL));

			// 重构action
			ProcessActivity pa = activityRepository.get(conn,
					EXP.INS().key("pd_id", p.pdId).andKey("id", p.currActivityId));

			// 获取当前Activity的行为Action信息
			ProcessAction paction = processActionRepository.get(conn,
					EXP.INS().key("pd_id", p.pdId).andKey("activity_id", p.currActivityId));

			JSONObject json = new JSONObject();
			json.put("process", p);
			json.put("definition", pd);
			json.put("activity", pa);
			json.put("action", paction);

			return json;
		}
	}

	@POSTAPI(//
			path = "getProcessInfoByTargerType", //
			des = "得到流程实例所需数据Process，Activity，definition信息", //
			ret = "Process实例")
	public JSONObject getProcessInfoByTargerType(//
			@P(t = "使用者编号", r = false) Long userId, //
			@P(t = "流程定义编号") Long processId, //
			@P(t = "action跳转类型", r = false) String targetType //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {

			Process p = processRepository.get(conn, EXP.INS().key("id", processId));

			// 获取对应Definition的信息
			ProcessDefinition pd = definitionRepository.get(conn, EXP.INS().key("id", p.pdId));
			JSONArray ja = new JSONArray();
			if (targetType != null && "activityGroup".equals(targetType)) {
				ProcessActivityGroup pag = processActivityGroupRepository.get(conn,
						EXP.INS().key("id", p.currActivityId));
				for (SubActivity s : pag.subActivities) {
					ProcessActivity pa = activityRepository.get(conn,
							EXP.INS().key("pd_id", p.pdId).andKey("id", s.subActivityId));
					ja.add(pa);
				}
			} else {
				ProcessActivity pa = activityRepository.get(conn,
						EXP.INS().key("pd_id", p.pdId).andKey("id", p.currActivityId));
				ja.add(pa);
			}

			// 获取当前Activity的行为Action信息
			ProcessAction paction = processActionRepository.get(conn,
					EXP.INS().key("pd_id", p.pdId).andKey("owner_id", p.currActivityId));

			JSONObject json = new JSONObject();
			json.put("process", p);
			json.put("definition", pd);
			json.put("activity", ja);
			json.put("action", paction);

			return json;
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
			return processRepository.getList(conn, EXP.INS().key("pd_id", pdId)
					.andKey("active", DataConst.ACTIVE_NORMAL).append("ORDER BY timestamp DESC"), count, offset);
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
			return processRepository.get(conn, EXP.INS().key("id", id));
		}
	}

	@POSTAPI(//
			path = "getProcessListByUserId", //
			des = "获取某用户的流程实例列表", //
			ret = "List<Process>") //
	public List<Process> getProcessListByUserId(Long userId, Integer count, Integer offset) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			// TODO 临时策略 ，需要集成用户
			return processRepository.getList(conn,
					EXP.INS().key("active", DataConst.ACTIVE_NORMAL).append("ORDER BY timestamp DESC"), count, offset);
		}
	}

	///////////////////////////////
	////// TableData
	///////////////////////////////

	@POSTAPI(//
			path = "insertProcessTableData", //
			des = "为流程中的表格表单插入数据" //
	)
	public ProcessAsset insertProcessTableData(//
			@P(t = "用户编号") Long userId, //
			@P(t = "流程编号") Long processId, //
			@P(t = "资源描述编号") Long descId, //
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "表结构编号") String tableSchemaName, //
			@P(t = "运算表数据") JSONObject data//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			// 先创建数据
			TableService tsService = Singleton.ins(TableService.class, "table");
			TableData td = tsService.insertTableData(tableSchemaId, data);

			// 再关联创建流程资产
			return createProcessAsset(processId, tableSchemaName, ProcessAssetDesc.TYPE_TABLE, descId, td.id.toString(),
					userId);
		}
	}

	@POSTAPI(//
			path = "editProcessTableData", //
			des = "更改表数据", //
			ret = "受影响行数"//
	)
	public int editProcessTableData(@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "表数据编号") Long tableDataId, //
			@P(t = "数据") JSONObject data//
	) throws Exception {
		TableService tsService = Singleton.ins(TableService.class, "table");
		return tsService.updateTableData(tableSchemaId, tableDataId, data);
	}

	///////////////////////////////
	////// ProcessAsset
	///////////////////////////////

	@POSTAPI(//
			path = "getProcessAssetByIdANDUserId", //
			des = "根据用戶编号和流程实例编号，获取对应的资产", //
			ret = "ProcessAsset"//
	)
	public JSONObject getProcessAssetByIdANDUserId(@P(t = "用户编号") Long userId, //
			@P(t = "资源编号") Long assetId //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {

			ProcessAsset pa = processAssetRepository.get(conn, EXP.INS().key("user_id", userId).andKey("id", assetId));
			if (pa == null) {
				return null;
			}
			TableService tsService = Singleton.ins(TableService.class, "table");

			JSONObject jo = new JSONObject();

			TableData td = tsService.getTableDatasById(new Long(pa.src));

			jo.put("processAsset", pa);
			jo.put("tableData", td);
			return jo;
		}
	}

	@POSTAPI(//
			path = "createProcessAsset", //
			des = "添加流程资产（附件，文件，表单等）", //
			ret = "ProcessAsset实例"//
	)
	public ProcessAsset createProcessAsset(//
			@P(t = "流程编号") Long processId, //
			@P(t = "资产名称") String name, //
			@P(t = "资产名称") String descType, //
			@P(t = "资源描述编号") Long descId, //
			@P(t = "资源内容（编号或url）") String src, //
			@P(t = "用户编号") Long userId//
	) throws Exception {
		ProcessAsset pa = new ProcessAsset();
		pa.id = IDUtils.getSimpleId();
		pa.processId = processId;
		pa.name = name;
		pa.descType = descType;

		pa.descId = descId;
		pa.userId = userId;
		pa.src = src;
		try (DruidPooledConnection conn = ds.getConnection()) {
			processAssetRepository.insert(conn, pa);
			return pa;
		}
	}

	@POSTAPI(//
			path = "editProcessAsset", //
			des = "编辑流程资产（附件，文件，表单等）", //
			ret = "更新影响的记录行数"//
	)
	public int editProcessAsset(//
			@P(t = "资产编号") Long assetId, //
			@P(t = "资产名称") String name, //
			@P(t = "资源内容（编号或url）") String src//
	) throws Exception {
		ProcessAsset renew = new ProcessAsset();
		renew.name = name;
		renew.src = src;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return processAssetRepository.update(conn, EXP.INS().key("id", assetId), renew, true);
		}
	}

	@POSTAPI(//
			path = "delProcessAsset", //
			des = "删除流程资产", //
			ret = "更新影响的记录行数"//
	)
	public int delProcessAsset(//
			@P(t = "assetId") Long assetId//
	) throws Exception {
		// 流程资产可以真删除
		try (DruidPooledConnection conn = ds.getConnection()) {
			return processAssetRepository.delete(conn, EXP.INS().key("id", assetId));
		}
	}

	@POSTAPI(//
			path = "getProcessAssetByDescIds", //
			des = "根据流程资产描述编号，获取对应的资产列表", //
			ret = "ProcessAsset列表"//
	)
	public List<ProcessAsset> getProcessAssetByDescIds(//
			@P(t = "资产编号") Long processId, //
			@P(t = "资产描述编号列表") JSONArray descIds, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			EXP exp = EXP.INS().key("process_id", processId).and(EXP.IN_ORDERED("desc_id", descIds.toArray()));
			return processAssetRepository.getList(conn, exp, count, offset);
		}
	}

	@POSTAPI(//
			path = "getProcessAssetList", //
			des = "根据用户编号以及流程编号，获取对应的资产列表", //
			ret = "ProcessAsset列表"//
	)
	public List<ProcessAsset> getProcessAssetList(//
			@P(t = "用户编号") Long userId, //
			@P(t = "资产编号") Long processId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {

			return processAssetRepository.getList(conn,
					EXP.INS().key("user_id", userId).andKey("process_id", processId), count, offset);
		}
	}

	///////////////////////////////
	////// ProcessAction
	///////////////////////////////

	/**
	 * 原始版本
	 *
	 */

	private void execAction(DruidPooledConnection conn, User user, Process p, ProcessActivity pa,
			ProcessActivity.Action action, Long activityId) throws Exception {

		// 判断type
		if (action.type.equals(ProcessActivity.Action.TYPE_REJECT)) {
			// 驳回，暂时没做
		} else if (action.type.equals(ProcessActivity.Action.TYPE_TERMINATE)) {
			// 终结，暂时没做
		} else {
			// 其它情况都当同意做

			// 判断role
			JSONArray rules = action.rules;
			String defaultTarget = null;
			for (int i = 0; i < rules.size(); i++) {
				JSONObject jo = rules.getJSONObject(i);
				String exp = jo.getString("exp");
				String target = jo.getString("target");

				System.out.println(StringUtils.join("exec rule>>> ", exp, " --- ", target));

				if (exp.equals("expDefault")) {
					// 默认case
					defaultTarget = target;
				} else {
					// 其它case，先判断表达式，然后执行
					// TODO 暂时不支持
				}
			}
			createProcessLog(p.id, "执行Action", user.id, user.name, action.type, action.label, activityId, "");

			Process renew = new Process();
			renew.currActivityId = Long.decode(defaultTarget);
			processRepository.update(conn, EXP.INS().key("id", p.id), renew, true);

		}

	}

	/**
	 * 
	 * 重构后版本
	 */

	// 判断节点分组中的节点是否全部操作完毕
	@POSTAPI(//
			path = "ifActivityAction", //
			des = "判断当前节点分组中的所有节点是否全部操作完毕", //
			ret = "返回是否完成，0为未完成，1为完成"//
	)
	public int ifActivityAction(//
			@P(t = "节点分组编号") Long activityGroupId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			int res = 0;
			List<SubActivity> sublist = flowService.getSubActivity(activityGroupId);
			for (SubActivity s : sublist) {

				List<ProcessAction> alist = processActionRepository.getList(conn,
						EXP.INS().key("owner_id", s.subActivityId).andKey("owner_type", 1), null, null);
				int i = 0;
				for (ProcessAction pa : alist) {
					i = i + pa.ext;
				}
				if (i == 0) {
					return 0;
				}
				res += i;
			}
			return res;
		}
	}

	// activityGroup中的节点操作记录、、更改ext数据，1为已经审核

	@POSTAPI(//
			path = "editActionExt", //
			des = "修改当前节点的action行为的操作状态", //
			ret = "受影响行数"//
	)
	public int editActionExt(//
			@P(t = "action行为编号，") Long actionId, //
			@P(t = "所属节点编号，") Long activityId, //
			@P(t = "所属节点操作状态，") Byte ext//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			ProcessAction pa = new ProcessAction();
			pa.ext = ext;
			return processActionRepository.update(conn, EXP.INS().key("owner_id", activityId).andKey("id", actionId),
					pa, true);
		}
	}

	// 同意
	@POSTAPI(//
			path = "processActionAccept", //
			des = "acceptAction同意操作", //
			ret = "受影响行数"//
	)
	public int processActionAccept(//
			@P(t = "流程实例编号") Long processId, //
			@P(t = "流程节点编号") Long activityId, //
			@P(t = "执行的行为编号") Long actionId, //
			@P(t = "执行的行为类型") String actionType, //
			@P(t = "用户编号") Long userId //
	) throws Exception {
		int ret = 1;
		try (DruidPooledConnection conn = ds.getConnection()) {
			ProcessAction action = processActionIF(processId, activityId, actionId);
			if (action != null) {
				if (actionType.equals(ProcessAction.TYPE_ACCEPT)) {
					// 判断role
					User user = userRepository.get(conn, EXP.INS().key("id", userId));
					JSONArray rules = action.rules;

					String defaultTarget = null;
					for (int i = 0; i < rules.size(); i++) {
						JSONObject jo = rules.getJSONObject(i);
						String exp = jo.getString("exp");
						String targetType = jo.getString("targetType");
						String target = null;
						// 判断是节点，还是节点分组
						if (targetType.equals("activity")) {
							target = jo.getString("target");

						} else if (targetType.equals("activityGroup")) {
							target = jo.getString("target");
							// TODO 未完善
//							Process p = new Process();
//							p.state = Process.STATE_WAITING;
//							processRepository.update(conn, EXP.INS().key("id", processId), p, true);
							ret = 2;
						}

						System.out.println(StringUtils.join("exec rule>>> ", exp, " --- ", target));

						if (exp.equals("expDefault")) {
							// 默认case
							defaultTarget = target;
							break;
						} else {
							// 其它case，先判断表达式，然后执行
							// TODO 暂时不支持
						}
					}
					createProcessLog(processId, "acceptAction操作", user.id, user.name, action.type, action.label,
							activityId, "");

					Process renew = new Process();
					renew.currActivityId = Long.decode(defaultTarget);
					processRepository.update(conn, EXP.INS().key("id", processId), renew, true);
				} else {
					throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "流程节点的行为Action类型错误");
				}
			} else {
				throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点的行为Action");
			}
		}
		return ret;
	}

	// 驳回
	@POSTAPI(//
			path = "processActionReject", //
			des = "rejectAction驳回操作", //
			ret = "受影响行数"//
	)
	public int processActionReject(//
			@P(t = "流程实例编号") Long processId, //
			@P(t = "流程节点编号") Long activityId, //
			@P(t = "执行的行为编号") Long actionId, //
			@P(t = "执行的行为类型") String actionType, //
			@P(t = "用户编号") Long userId, //
			@P(t = "驳回原因") String remark//
	) throws Exception {
		int ret = 1;
		try (DruidPooledConnection conn = ds.getConnection()) {
			ProcessAction action = processActionIF(processId, activityId, actionId);
			if (action != null) {
				if (actionType.equals(ProcessAction.TYPE_REJECT)) {
					// 判断role
					User user = userRepository.get(conn, EXP.INS().key("id", userId));
					JSONArray rules = action.rules;

					String defaultTarget = null;
					for (int i = 0; i < rules.size(); i++) {
						JSONObject jo = rules.getJSONObject(i);
						String exp = jo.getString("exp");
						String targetType = jo.getString("targetType");
						String target = null;
						// 判断是节点，还是节点分组
						if (targetType.equals("activity")) {
							target = jo.getString("target");

						} else if (targetType.equals("activityGroup")) {
							target = jo.getString("target");
							// TODO 未完善
							ret = 2;
						}

						System.out.println(StringUtils.join("exec rule>>> ", exp, " --- ", target));

						if (exp.equals("expDefault")) {
							// 默认case
							defaultTarget = target;
						} else {
							// 其它case，先判断表达式，然后执行
							// TODO 暂时不支持
						}
					}
					createProcessLog(processId, "驳回", user.id, user.name, action.type, action.label + ",驳回原因：" + remark,
							activityId, "");

					Process renew = new Process();
					renew.currActivityId = Long.decode(defaultTarget);
					processRepository.update(conn, EXP.INS().key("id", processId), renew, true);
				} else {
					throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "流程节点的行为Action类型错误");
				}
			} else {
				throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点的行为Action");
			}
		}
		return ret;
	}

	// 废除/终结
	public void processActionTerminate(//
			@P(t = "流程实例编号") Long processId, //
			@P(t = "流程节点编号") Long activityId, //
			@P(t = "执行的行为编号") Long actionId, //
			@P(t = "执行的行为类型") String actionType, //
			@P(t = "用户编号") Long userId, //
			@P(t = "废除/终结原因") String remark//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			ProcessAction action = processActionIF(processId, activityId, actionId);
			if (action != null) {
				if (actionType.equals(ProcessAction.TYPE_TERMINATE)) {
					User user = userRepository.get(conn, EXP.INS().key("id", userId));
					// 修改process流程实例状态为废除/终结
					Process t = new Process();
					t.state = Process.STATE_TERMINATE;
					processRepository.update(conn, EXP.INS().key("id", processId), t, true);
					createProcessLog(processId, "废除Process流程实例", user.id, user.name, action.type,
							action.label + ",废除原因：" + remark, activityId, "");
				}
			}
		}
	}

	public void processActionTransfer() {

	}

	public void processActionCirculation() {

	}

	protected ProcessAction processActionIF(//
			Long processId, //
			Long activityId, //
			Long actionId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			Process process = processRepository.get(conn, EXP.INS().key("id", processId));
			if (process == null) {
				throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程实例");
			} else {
				if (process.currActivityId.equals(activityId)) {
					ProcessActivity pa = activityRepository.get(conn, EXP.INS().key("id", activityId));
					ProcessActivityGroup pag = processActivityGroupRepository.get(conn,
							EXP.INS().key("id", activityId));
					if (pa == null && pag == null) {
						throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR,
								"没找到对应流程节点或节点分组Activity 或 ActivityGroup");
					} else {
						ProcessAction paction = processActionRepository.get(conn, EXP.INS().key("id", actionId));
						if (paction == null) {
							throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点的行为Action");
						} else {
							return paction;
						}
					}
				} else {
					throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "业务进度（当前流程节点）与提交的activityId不符");
				}
			}
		}
	}

	@POSTAPI(//
			path = "executeProcessAction", //
			des = "执行流程的行为"//
	)
	public void executeProcessAction(//
			@P(t = "流程实例编号") Long processId, //
			@P(t = "流程节点编号") Long activityId, //
			@P(t = "执行的行为编号") String actionId, //
			@P(t = "用户编号") Long userId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			Process process = processRepository.get(conn, EXP.INS().key("id", processId));
			if (process == null) {
				throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程实例");
			} else {
				if (process.currActivityId.equals(activityId)) {
					ProcessActivity pa = activityRepository.get(conn, EXP.INS().key("id", activityId));
					if (pa == null) {
						throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点Activity");
					} else {

						if (StringUtils.isBlank(pa.actions)) {
							throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点的行为Action");
						} else {

							User user = userRepository.get(conn, EXP.INS().key("id", userId));
							// if (user == null) {
							// throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点的用户");
							// } else {

							// 暂时没有鉴权
							// TODO 做鉴权

							user = new User();
							user.id = userId;
							user.name = "临时用户";

							List<ProcessActivity.Action> list = JSON.parseArray(pa.actions,
									ProcessActivity.Action.class);
							if (list.size() > 0) {
								HashMap<String, Action> actions = new HashMap<>();
								list.forEach(item -> {
									actions.put(item.id, item);
								});
								ProcessActivity.Action act = actions.get(actionId);
								if (act == null) {
									throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点的行为Action");
								} else {
									// 终于找到节点，要开始执行了。
									execAction(conn, user, process, pa, act, activityId);
								}
							} else {
								throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点的行为Action");
							}
							// }
						}

					}
				} else {
					throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "业务进度（当前流程节点）与提交的activityId不符");
				}
			}
		}
	}

	@POSTAPI(//
			path = "getProcessActionsInActivity", //
			des = "获取流程节点Activity中流程Action列表", //
			ret = "List<ProcessAction>")
	public List<ProcessAction> getProcessActionsInActivity(//
			@P(t = "Action编号") Long activityId //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return processActionRepository.getList(conn, EXP.INS().key("owner_id", activityId), 512, 0);
		}
	}

	@POSTAPI(//
			path = "getProcessActionsInPD", //
			des = "获取流程定义PD中流程Action列表", //
			ret = "List<ProcessAction>"//
	)
	public List<ProcessAction> getProcessActionsInPD(//
			@P(t = "流程定义编号") Long pdId //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return processActionRepository.getList(conn, EXP.INS().key("pd_id", pdId), 512, 0);
		}
	}

	// @POSTAPI(//
	// path = "getProcessAssetByDescIds", //
	// des = "根据流程资产描述编号，获取对应的资产列表", //
	// ret = "ProcessAsset列表"//
	// )
	// public JSONArray getProcessAssetByDescIds(//
	// @P(t = "资产编号") Long processId, //
	// @P(t = "资产描述编号列表") JSONArray descIds, //
	// Integer count, //
	// Integer offset//
	// ) throws Exception {
	//// TODO 换成 where in 语句，试一试
	// try (DruidPooledConnection conn = ds.getConnection()) {
	//
	// JSONArray ret = new JSONArray();
	// for (int i = 0; i < descIds.size(); i++) {
	// Long id = descIds.getLong(i);
	//
	// ProcessAsset pa = processAssetRepository.get(conn,
	// EXP.INS().key("process_id", processId).andKey("desc_id", id));
	//
	// JSONObject jo = new JSONObject();
	// jo.put("descId", id);
	// jo.put("asset", pa);
	// ret.add(jo);
	// }
	//
	// return ret;
	// }
	// }

	///////////////////////////////
	////// ProcessLog
	///////////////////////////////

	@POSTAPI(//
			path = "getProcessLogList", //
			des = "根据processId查询流程操作日志", //
			ret = "List<ProcessLog>"//
	)
	public List<ProcessLog> getProcessLogList(//
			@P(t = "processId流程编号") Long processId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return processLogRepository.getList(conn,
					EXP.INS().key("process_id", processId).append("ORDER BY timestamp DESC"), count, offset);
		}
	}

	/**
	 * 添加流程操作日志（不开放的接口）
	 */
	protected void createProcessLog(//
			@P(t = "流程编号") Long processId, //
			@P(t = "标题") String title, //
			@P(t = "使用者编号") Long userid, //
			@P(t = "使用者名称") String userName, //
			@P(t = "行为或活动") String action, //
			@P(t = "行为或活动说明") String actionDesc, //
			@P(t = "流程节点编号") Long activityId, //
			@P(t = "记录扩展数据") String ext//
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
		pl.activityId = activityId;
		pl.ext = ext;

		try (DruidPooledConnection conn = ds.getConnection()) {
			processLogRepository.insert(conn, pl);
		}

	}
}
