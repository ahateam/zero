package zyxhj.flow.repository;

import zyxhj.utils.data.ts.TSRepository;

public class ProcessRepository extends TSRepository<zyxhj.flow.domain.Process> {

	public ProcessRepository() {
		super(zyxhj.flow.domain.Process.class);
	}

}
