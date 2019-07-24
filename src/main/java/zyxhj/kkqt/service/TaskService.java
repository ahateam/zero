//package zyxhj.kkqt.service;
//
//import java.util.Date;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.alicloud.openservices.tablestore.SyncClient;
//import com.alicloud.openservices.tablestore.model.search.SearchQuery;
//
//import zyxhj.kkqt.domain.MakeTask;
//import zyxhj.kkqt.repository.MakeTaskRepository;
//import zyxhj.kkqt.repository.SpreadTaskRepository;
//import zyxhj.kkqt.repository.TaskWallRepository;
//import zyxhj.utils.IDUtils;
//import zyxhj.utils.Singleton;
//import zyxhj.utils.data.ts.TSQL;
//import zyxhj.utils.data.ts.TSQL.OP;
//import zyxhj.utils.data.ts.TSUtils;
//
//public class TaskService {
//
//	private static Logger log = LoggerFactory.getLogger(TaskService.class);
//
//	private TaskWallRepository taskWallRepository;
//	private SpreadTaskRepository spreadTaskRepository;
//
//	public TaskService() {
//		try {
//
//			taskWallRepository = Singleton.ins(TaskWallRepository.class);
//			spreadTaskRepository = Singleton.ins(SpreadTaskRepository.class);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//	}
//
//	/**
//	 * 创建制作任务
//	 */
//	public void createMakeTask(SyncClient client, Long upUserId, Byte level, JSONArray needs, String title,
//			String detail, String pos) throws Exception {
//
//		long id = IDUtils.getSimpleId();
//		
//		
//		MakeTask mt = new MakeTask();
//		mt._id = TSUtils.get_id(id);
//		mt.id = id;
//		mt.level = level;
//		mt.needs = needs.toJSONString();
//		mt.status = MakeTask.STATUS.CREATED.v();
//
//		mt.upUserId = upUserId;
//		mt.time = new Date();
//		mt.pos = pos;
//		mt.title = title;
//		mt.detail = detail;
//
//		taskWallRepository.insert(client, mt, false);
//	}
//
//	/**
//	 * 获取制作任务列表</br>
//	 * 
//	 * @return JSONObject{isAllSuccess,totalCount,list}
//	 */
//	public JSONObject getMakeTaskList(SyncClient client, Long upUserId, Byte level, Byte status, JSONArray needs,
//			String pos, Integer distanceInMeter, Boolean getTotalCount, Integer offset, Integer count)
//			throws Exception {
//
//		SearchQuery query = new TSQL().Term(OP.AND, "upUserId", upUserId)//
//				.Term(OP.AND, "level", level)//
//				.Term(OP.AND, "status", status)//
//				.Terms(OP.AND, "needs", needs.toArray())//
//				.GeoDistance(OP.AND, "pos", pos, distanceInMeter).build();
//
//		query.setOffset(offset);
//		query.setLimit(count);
//		query.setGetTotalCount(getTotalCount);
//
//		return taskWallRepository.search(client, query);
//	}
//
//	/**
//	 * 获取推广任务列表
//	 */
//	public void getSpreadTaskList() {
//
//	}
//
//	/**
//	 * 接受任务
//	 */
//	public void acceptTask(SyncClient client, Long id, Long acceptUserId) {
//
//	}
//
//	/**
//	 * 获取我的任务列表
//	 */
//	public void getMyTaskList() {
//
//	}
//
//	/**
//	 * 查看任务详细
//	 */
//	public void getMyTaskDetail() {
//
//	}
//
//}
