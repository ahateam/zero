package zyxhj.kkqt.repository;

import zyxhj.kkqt.domain.MakeTask;
import zyxhj.utils.data.ts.TSRepository;

public class MakeTaskRepository extends TSRepository<MakeTask> {

	public MakeTaskRepository() {
		super(MakeTask.class);
	}

}
