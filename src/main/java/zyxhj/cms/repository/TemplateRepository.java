package zyxhj.cms.repository;

import zyxhj.cms.domian.Template;
import zyxhj.utils.data.rds.RDSRepository;

public class TemplateRepository extends RDSRepository<Template> {

	public TemplateRepository() {
		super(Template.class);
	}

}
