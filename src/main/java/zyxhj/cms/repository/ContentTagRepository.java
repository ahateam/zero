package zyxhj.cms.repository;

import zyxhj.cms.domian.ContentTag;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentTagRepository extends RDSRepository<ContentTag> {

	public ContentTagRepository() {
		super(ContentTag.class);
	}

}
