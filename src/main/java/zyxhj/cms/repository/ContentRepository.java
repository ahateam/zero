package zyxhj.cms.repository;

import zyxhj.cms.domian.Content;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentRepository extends RDSRepository<Content> {

	public ContentRepository() {
		super(Content.class);
	}

}
