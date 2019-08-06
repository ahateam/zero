package zyxhj.flow.service;

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

import zyxhj.core.repository.UserRepository;
import zyxhj.core.repository.UserRoleRepository;
import zyxhj.flow.domain.Process;
import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessActivity.Action;
import zyxhj.flow.domain.ProcessAsset;
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
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class ProcessService extends Controller {

	private static Logger log = LoggerFactory.getLogger(ProcessService.class);

	private DruidDataSource ds;

	private ProcessRepository processRepository;
	private ProcessActivityRepository activityRepository;
	private ProcessLogRepository processLogRepository;
	private ProcessAssetRepository processAssetRepository;

	public ProcessService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");

			processRepository = Singleton.ins(ProcessRepository.class);
			activityRepository = Singleton.ins(ProcessActivityRepository.class);
			processLogRepository = Singleton.ins(ProcessLogRepository.class);
			processAssetRepository = Singleton.ins(ProcessAssetRepository.class);

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
			return processRepository.update(conn, EXP.ins().key("pd_id", pdId).andKey("id", processId), pro, true);
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
			return processRepository.update(conn, EXP.ins().key("id", id), pro, true);
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

			return processRepository.getList(conn, EXP.ins().key("pd_id", pdId).andKey("active", 0), count, offset);
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
			List<Process> p = processRepository.getList(conn, EXP.ins().key("id", id), 1, 0);
			return p.get(0);
		}
	}

	/**
	 * 添加流程操作日志（不开放的接口）
	 */
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

	private void execAction(DruidPooledConnection conn, ProcessActivity activity, ProcessActivity.Action action) {

		// 判断role
		String rule = action.rule;
		

		// 执行跳转

		// 记录日志
	}

	@POSTAPI(//
			path = "executeProcessAction", //
			des = "执行流程的行为"//
	)
	public void executeProcessAction(//
			@P(t = "流程实例编号") Long processId, //
			@P(t = "流程节点编号") Long activityId, //
			@P(t = "执行的行为编号") String actionId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			Process process = processRepository.get(conn, EXP.ins().key("id", processId));
			if (process == null) {
				throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程实例");
			} else {
				if (process.currActivityId.equals(activityId)) {
					ProcessActivity pa = activityRepository.get(conn, EXP.ins().key("id", activityId));
					if (pa == null) {
						throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点Activity");
					} else {

						if (StringUtils.isBlank(pa.actions)) {
							throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点的行为Action");
						} else {
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
									execAction(conn, pa, act);
								}
							} else {
								throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "没找到对应流程节点的行为Action");
							}
						}

					}
				} else {
					throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, "业务进度（当前流程节点）与提交的activityId不符");
				}
			}
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
			return processAssetRepository.update(conn, EXP.ins().key("id", assetId), renew, true);
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

			return processAssetRepository.getList(conn,
					EXP.ins().key("type", ProcessAsset.TYPE_DEFINITON).andKey("owner_id", pdId), count, offset);
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

			return processAssetRepository.getList(conn,
					EXP.ins().key("type", ProcessAsset.TYPE_ACTIVITY).andKey("owner_id", activityId), count, offset);
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
			return processAssetRepository.delete(conn, EXP.ins().andKey("id", assetId));
		}
	}
}
