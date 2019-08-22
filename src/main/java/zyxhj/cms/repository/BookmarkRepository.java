package zyxhj.cms.repository;

import zyxhj.cms.domian.Bookmark;
import zyxhj.utils.data.rds.RDSRepository;

public class BookmarkRepository extends RDSRepository<Bookmark> {

	public BookmarkRepository() {
		super(Bookmark.class);
	}

}
