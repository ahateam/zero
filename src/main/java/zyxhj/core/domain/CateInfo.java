package zyxhj.core.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;

/**
 * 分类信息
 *
 */
@TSAnnEntity(alias = "CateInfo")
public class CateInfo {

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 消息编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	/**
	 * 地区（最低一级行政区参与索引）
	 */
	@TSAnnIndex(name = "CateInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String region;

	/**
	 * 分类（最低一级分类参与索引）
	 */
	@TSAnnIndex(name = "CateInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String cate;

	/**
	 * 标签
	 */
	@TSAnnIndex(name = "CateInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String tags;

	/**
	 * 状态
	 */
	@TSAnnIndex(name = "CateInfoIndex", type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long status;

	/**
	 * 标题
	 */
	@TSAnnIndex(name = "CateInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = false, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 位置
	 */
	@TSAnnIndex(name = "CateInfoIndex", type = FieldType.GEO_POINT, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String pos;

	/**
	 * 时间
	 */
	@TSAnnIndex(name = "CateInfoIndex", type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date time;

	/**
	 * 内容
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String content;

	/**
	 * 省
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String province;

	/**
	 * 市
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String city;

}
