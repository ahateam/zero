package zyxhj.core.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 打听
 *
 */
@RDSAnnEntity(alias = "Inquire")
public class Inquire extends TSEntity {

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 打车编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	/**
	 * 标题
	 */
	@TSAnnIndex(name = "InquireInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = false, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 内容
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String content;

	/**
	 * 地区（最低一级行政区参与索引）
	 */
	@TSAnnIndex(name = "InquireInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String region;

	/**
	 * 标签
	 */
	@TSAnnIndex(name = "InquireInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String tags;

	/**
	 * 时间
	 */
	@TSAnnIndex(name = "InquireInfoIndex", type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date time;

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

	/**
	 * 类型 打听某种类型
	 */
	@TSAnnIndex(name = "InquireInfoIndex", type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long type;

}
