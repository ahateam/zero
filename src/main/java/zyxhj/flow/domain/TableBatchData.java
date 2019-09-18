package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_table_batch_data")
public class TableBatchData {

	/**
	 * 表结构ID
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long tableSchemaId;

	/**
	 * 批次（任务）编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long batchId;

	/**
	 * 数据编号（行号）
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer dataId;

	/**
	 * 批次（任务）数据版本号
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String batchVer;

	/**
	 * 用户编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 表数据
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public JSONObject data;

	/**
	 * 备注
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;

}
