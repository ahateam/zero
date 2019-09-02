package zyxhj.cms.repository;

import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentTagGroupRepository extends RDSRepository<ContentTagGroup> {

	public ContentTagGroupRepository() {
		super(ContentTagGroup.class);
	}

}
