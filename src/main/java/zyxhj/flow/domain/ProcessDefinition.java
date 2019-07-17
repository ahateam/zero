package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 流程定义
 *
 */
@TSAnnEntity(alias = "ProcessDefinition", indexName = "ProcessDefinitionIndex")
public class ProcessDefinition extends TSEntity {

	public static final Byte STATUS_ON = 0;
	public static final Byte STATUS_OFF = 1;

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
	 * 所属模块
	 */
	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String module;

	/**
	 * 状态
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Byte status;

	/**
	 * 标签列表
	 */
	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONArray tags;

	/**
	 * 标题
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 流程图泳道名称列表，泳道名称不可重复</br>
	 * JSONArray格式
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONArray lanes;

	/**
	 * 资产</br>
	 * 文件，合同等
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONArray assets;

	/**
	 * startPoint，流程起点位置</br>
	 * endPoint，流程终点位置</br>
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONObject visualization;

}
