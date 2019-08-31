package zyxhj.core.domain;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

@TSAnnEntity(alias = "core_appraise", indexName = "core_appraise_index")
public class Appraise extends TSEntity {

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 回复所属对象编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long ownerId;

	/**
	 * 用户id
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long userId;

	/**
	 * 评价的值
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = false, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long value;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "赞")
	public static final Long VALUE_PRAISE = 0L;

	@AnnDicField(alias = "踩")
	public static final Long STATUS_DISS = 1L;

}