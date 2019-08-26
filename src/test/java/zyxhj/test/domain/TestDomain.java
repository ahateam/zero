package zyxhj.test.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 测试
 *
 */
@RDSAnnEntity(alias = "tb_rds_test")
public class TestDomain {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String name;

	/**
	 * 文本
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String year;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public JSONObject tags;

	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public JSONArray arrays;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "开启")
	public static final Byte STATUS_OPEN = 0;

	@AnnDicField(alias = "关闭")
	public static final Byte STATUS_CLOSE = 1;
}
