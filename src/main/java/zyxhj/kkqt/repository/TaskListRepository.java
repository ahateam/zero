package zyxhj.kkqt.repository;

import zyxhj.kkqt.domain.TaskList;
import zyxhj.utils.data.ts.TSRepository;

public class TaskListRepository extends TSRepository<TaskList> {

	public TaskListRepository() {
		super(TaskList.class);
	}

}
