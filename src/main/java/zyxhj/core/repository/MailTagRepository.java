package zyxhj.core.repository;

import zyxhj.core.domain.MailTag;
import zyxhj.utils.data.ts.TSRepository;

public class MailTagRepository extends TSRepository<MailTag> {

	public MailTagRepository() {
		super(MailTag.class);
	}

}
