package zyxhj.kkqt.repository;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;

import zyxhj.kkqt.domain.TaskList;
import zyxhj.utils.data.rds.RDSRepository;

public class TaskListRepository extends RDSRepository<TaskList> {

	public TaskListRepository() {
		super(TaskList.class);
	}

	public Integer countTaskListByAccUserId(DruidPooledConnection conn, Long userId) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) c from tb_task_list WHERE acc_user_id = ").append(userId).append(" AND status = ")
				.append(TaskList.STATUS.SUCCESS.v());
		JSONObject countTaskList = sqlGetJSONObject(conn, sb.toString(), null);
		return countTaskList.getInteger("c");
	}

}
