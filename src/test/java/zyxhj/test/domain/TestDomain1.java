package zyxhj.test.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 测试
 *
 */
@RDSAnnEntity(alias = "tb_rds_test1")
public class TestDomain1 {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String name;

}
