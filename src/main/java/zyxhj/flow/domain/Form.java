package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_form")
public class Form {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;
	

	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;
	

	@RDSAnnField(column = RDSAnnField.OBJECT)
	public JSONObject data;
	
}
