package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 表数据
 *
 */
@RDSAnnEntity(alias = "tb_table_data")
public class TableData {

	/**
	 * 表结构ID
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long tableSchemaId;

	/**
	 * 表数据编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;
	
	/**
	 * 运算表数据
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public JSONObject data;
	
	/**
	 * 批次（任务）编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long batchId;
	
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

}
