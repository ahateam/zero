package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;

@RDSAnnEntity(alias = "tb_table_data_batch")
public class TableDataBatch {

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
	
	/**
	 * 当前批次（任务）运算表数据
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public JSONArray data;
	
	
}
