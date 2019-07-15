package zyxhj.kkqt.domain;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

@TSAnnEntity(alias = "SpreadTask")
public class SpreadTask extends TSEntity {

	public static enum PATTERN {
		LOCAL("本地"), //
		OFF_DATE("线下约会"), //
		ON_VIDEO("视频夸"), //
		ON_AUDIO("音频夸"), //
		;

		public String txt;

		private PATTERN(String txt) {
			this.txt = txt;
		}

	}

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
	 * 传播模式
	 */
	@TSAnnIndex(name = "SpreadTaskIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String pattern;

	/**
	 * 状态
	 */
	@TSAnnIndex(name = "SpreadTaskIndex", type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long status;
}
