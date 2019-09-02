package zyxhj.core.repository;

import zyxhj.core.domain.Mail;
import zyxhj.utils.data.ts.TSRepository;

public class MailRepository extends TSRepository<Mail> {

	public MailRepository() {
		super(Mail.class);
	}

}
