package zyxhj.core.repository;

import zyxhj.core.domain.IMRelation;
import zyxhj.utils.data.ts.TSRepository;

public class IMRelationRepository extends TSRepository<IMRelation> {

	public IMRelationRepository() {
		super(IMRelation.class);
	}

}
