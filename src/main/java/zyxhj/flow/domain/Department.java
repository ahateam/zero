package zyxhj.flow.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_department")
public class Department {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;
	
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;
	
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;
}
