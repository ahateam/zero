package zyxhj.cms.repository;

import zyxhj.cms.domian.Upvote;
import zyxhj.utils.data.ts.TSRepository;

public class UpvoteRepository extends TSRepository<Upvote> {

	public UpvoteRepository() {
		super(Upvote.class);
	}

}
