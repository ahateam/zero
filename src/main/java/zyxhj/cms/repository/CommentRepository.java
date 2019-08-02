package zyxhj.cms.repository;

import zyxhj.cms.domian.Comment;
import zyxhj.utils.data.ts.TSRepository;

public class CommentRepository extends TSRepository<Comment> {

	public CommentRepository() {
		super(Comment.class);
	}

}
