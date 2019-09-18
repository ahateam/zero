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
	 * 表数据
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public JSONObject data;

	/**
	 * 批次（任务）数据编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long batchDataId;

	/**
	 * 用户编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 错误数据标识
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte errorStatus;

	/**
	 * 错误数据标识
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String errorDesc;


	//正确CORRECT
	public static Byte ERROR_STATUS_CORRECT = 0;
	//错误WRONG
	public static Byte ERROR_STATUS_WRONG = 1;
	//异常abnormal
	public static Byte ERROR_STATUS_ABNORMAL = 2;
	
}
