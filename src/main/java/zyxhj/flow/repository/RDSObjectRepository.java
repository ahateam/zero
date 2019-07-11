package zyxhj.flow.repository;

import zyxhj.flow.domain.RDSObject;
import zyxhj.utils.data.rds.RDSRepository;

public class RDSObjectRepository extends RDSRepository<RDSObject> {

	public RDSObjectRepository() {
		super(RDSObject.class);
	}
}
