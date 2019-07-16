package zyxhj.flow.domain;

import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 附件
 *
 */
@TSAnnEntity(alias = "Part")
public class Part extends TSEntity {

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	/**
	 * 附件名称
	 */
	@TSAnnIndex(name = "PartIndex", type = FieldType.TEXT, enableSortAndAgg = false, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String name;

	/**
	 * 标签列表
	 */
	@TSAnnIndex(name = "PartIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONArray tags;

	/**
	 * 创建时间
	 */
	@TSAnnIndex(name = "PartIndex", type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date createTime;

	/**
	 * 附件地址
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String url;

	/**
	 * 扩展
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String ext;
}
