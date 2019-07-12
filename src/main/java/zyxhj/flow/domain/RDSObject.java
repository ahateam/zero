package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_rds_object")
public class RDSObject {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	@RDSAnnField(column = RDSAnnField.OBJECT)
	public JSONObject jsonObject;

	@RDSAnnField(column = RDSAnnField.OBJECT)
	public JSONArray jsonArray;

	@RDSAnnField(column = RDSAnnField.OBJECT)
	public TableSchema tsObject;

	@RDSAnnField(column = RDSAnnField.OBJECT)
	public Asset assetObject;
}
