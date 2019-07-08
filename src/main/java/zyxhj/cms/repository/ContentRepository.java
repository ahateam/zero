package zyxhj.cms.repository;

import zyxhj.cms.domian.Content;
import zyxhj.utils.data.ts.TSRepository;

public class ContentRepository extends TSRepository<Content> {

	public ContentRepository() {
		super(Content.class);
	}

}
