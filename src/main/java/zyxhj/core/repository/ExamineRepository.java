package zyxhj.core.repository;

import zyxhj.core.domain.Examine;
import zyxhj.utils.data.ts.TSRepository;

public class ExamineRepository extends TSRepository<Examine> {

	public ExamineRepository() {
		super(Examine.class);
	}

}