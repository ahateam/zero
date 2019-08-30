package zyxhj.flow.repository;

import zyxhj.flow.domain.Form;
import zyxhj.utils.data.rds.RDSRepository;

public class FormRepository extends RDSRepository<Form> {

	public FormRepository() {
		super(Form.class);
	}

}
