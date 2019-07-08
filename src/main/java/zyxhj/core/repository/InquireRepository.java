package zyxhj.core.repository;

import zyxhj.core.domain.Inquire;
import zyxhj.utils.data.ts.TSRepository;

public class InquireRepository extends TSRepository<Inquire> {

	public InquireRepository() {
		super(Inquire.class);
	}

}
