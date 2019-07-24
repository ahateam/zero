package zyxhj.cms.repository;

import zyxhj.cms.domian.ContentTag;
import zyxhj.utils.data.ts.TSRepository;

public class ContentTagRepository extends TSRepository<ContentTag> {

	public ContentTagRepository() {
		super(ContentTag.class);
	}

}
