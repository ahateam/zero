package zyxhj.flow.repository;

import zyxhj.flow.domain.Part;
import zyxhj.utils.data.ts.TSRepository;

public class PartRepository extends TSRepository<Part> {

	public PartRepository() {
		super(Part.class);
	}

}
