package zyxhj.utils.data.ts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.GeoBoundingBoxQuery;
import com.alicloud.openservices.tablestore.model.search.query.GeoDistanceQuery;
import com.alicloud.openservices.tablestore.model.search.query.GeoPolygonQuery;
import com.alicloud.openservices.tablestore.model.search.query.MatchAllQuery;
import com.alicloud.openservices.tablestore.model.search.query.MatchPhraseQuery;
import com.alicloud.openservices.tablestore.model.search.query.MatchQuery;
import com.alicloud.openservices.tablestore.model.search.query.PrefixQuery;
import com.alicloud.openservices.tablestore.model.search.query.Query;
import com.alicloud.openservices.tablestore.model.search.query.RangeQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermsQuery;
import com.alicloud.openservices.tablestore.model.search.query.WildcardQuery;
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import com.alicloud.openservices.tablestore.model.search.sort.Sort.Sorter;

import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;

public class TSQL {

	public static enum OP {
		AND, OR, NOT, NONE
	}

	public static enum RANGE {
		GREATER_THAN, GREATER_THAN_OR_EQUAL, //
		LESS_THAN, LESS_THAN_OR_EQUAL, //
		FROM, TO
	}

	private Integer offset = 0;
	private Integer limit = 10;
	private boolean getTotalCount = false;

	/**
	 * 表达式数组
	 */
	private ArrayList<Object> queries;

	/**
	 * 排序数组
	 */
	private ArrayList<Sorter> sorts;

	private OP op = OP.NONE;

	public TSQL() {
		queries = new ArrayList<>();
		sorts = new ArrayList<>();
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public void setGetTotalCount(boolean getTotalCount) {
		this.getTotalCount = getTotalCount;
	}

	public void addSort(Sorter sorter) {
		this.sorts.add(sorter);
	}

	private Query buildQuery() throws ServerException {
		if (queries.size() == 1) {
			// 只有一个节点，当即返回
			Object obj = queries.get(0);
			Query ret;
			if (obj instanceof TSQL) {
				TSQL tsql = (TSQL) obj;
				ret = tsql.buildQuery();
			} else {
				// Query
				ret = (Query) obj;
			}
			return ret;
		} else if (queries.size() > 1) {
			// 多个节点，需要根据连接符拼接成BoolQuery
			BoolQuery ret = new BoolQuery();
			List<Query> qs = new ArrayList<>();
			for (Object obj : queries) {
				if (obj instanceof TSQL) {
					TSQL tsql = (TSQL) obj;
					qs.add(tsql.buildQuery());
				} else {
					// Query
					qs.add((Query) obj);
				}
			}
			if (op == OP.AND) {
				ret.setMustQueries(qs);
			} else if (op == OP.OR) {
				ret.setMinimumShouldMatch(1);// 至少满足一个条件
				ret.setShouldQueries(qs);
			} else if (op == OP.NOT) {
				ret.setMustNotQueries(qs);
			} else {
				throw new ServerException(BaseRC.REPOSITORY_TABLESTORE_OP_ERROR);
			}
			return ret;
		} else {
			throw new ServerException(BaseRC.REPOSITORY_TABLESTORE_QUERY_ERROR);
		}
	}

	private Sort buildSort() {
		return new Sort(sorts);
	}

	public SearchQuery build() throws ServerException {
		SearchQuery ret = new SearchQuery();
		ret.setLimit(limit);
		ret.setOffset(offset);
		ret.setGetTotalCount(getTotalCount);

		ret.setQuery(buildQuery());
		if (!sorts.isEmpty()) {
			ret.setSort(buildSort());
		}

		return ret;
	}

	private TSQL link(OP op, Object query) throws ServerException {
		if (query != null) {
			if (op == OP.AND || op == OP.OR || op == OP.NOT) {
				if (queries.size() <= 0) {
					// 空，直接添加
					this.op = op;
					queries.add(query);
				} else {
					// 数组中已经有节点
					if (this.op != op) {
						// 操作符与之前不符，则添加应该抛异常
						throw new ServerException(BaseRC.REPOSITORY_TABLESTORE_OP_ERROR);
					} else {
						queries.add(query);
					}
				}
			} else {
				// 操作符不合法
				throw new ServerException(BaseRC.REPOSITORY_TABLESTORE_OP_ERROR);
			}
		}
		return this;
	}

	///////// TermQuery，精确匹配
	// https://help.aliyun.com/document_detail/100416.html?spm=a2c4g.11186623.6.812.f8cd494dyqoi7J

	public TSQL Term(OP op, String fieldName, Object key) throws ServerException {
		if (null != key) {
			TermQuery q = new TermQuery();
			q.setFieldName(fieldName);
			q.setTerm(ColumnBuilder.buildColumnValue(key)); // 设置要匹配的值
			link(op, q);
		}
		return this;
	}

	///////// TermsQuery，多值精确匹配
	// https://help.aliyun.com/document_detail/100416.html?spm=a2c4g.11186623.6.812.f8cd494dyqoi7J

	public TSQL Terms(OP op, String fieldName, Object... keys) throws ServerException {
		if (keys != null && keys.length > 0) {
			TermsQuery q = new TermsQuery();
			q.setFieldName(fieldName);
			for (Object key : keys) {
				q.addTerm(ColumnBuilder.buildColumnValue(key));
			}
			link(op, q);
		}
		return this;
	}

	///////// MatchAllQuery，全匹配，相当于任意匹配
	// https://help.aliyun.com/document_detail/100417.html?spm=a2c4g.11186623.6.813.2cb45bd8MLAGSE

	public TSQL MatchAll(OP op) throws ServerException {
		if (limit > 0) {
			MatchAllQuery q = new MatchAllQuery();
			link(op, q);
		}
		return this;
	}

	///////// MatchQuery，关键词近似匹配
	// https://help.aliyun.com/document_detail/100417.html?spm=a2c4g.11186623.6.813.2cb45bd8MLAGSE

	public TSQL Match(OP op, String fieldName, String text) throws ServerException {
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(text)) {
			MatchQuery q = new MatchQuery(); // 设置查询类型为MatchQuery
			q.setFieldName(fieldName); // 设置要匹配的字段
			q.setText(text); // 设置要匹配的值
			link(op, q);
		}
		return this;
	}

	///////// MatchPhraseQuery，短语匹配，关键词顺序不会乱
	// https://help.aliyun.com/document_detail/100417.html?spm=a2c4g.11186623.6.813.2cb45bd8MLAGSE

	public TSQL MatchPhrase(OP op, String fieldName, String text) throws ServerException {
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(text)) {
			MatchPhraseQuery q = new MatchPhraseQuery(); // 设置查询类型为MatchPhraseQuery
			q.setFieldName(fieldName); // 设置要匹配的字段
			q.setText(text); // 设置要匹配的值
			link(op, q);
		}
		return this;
	}

	///////// PrefixQuery，前缀匹配
	// https://help.aliyun.com/document_detail/100418.html?spm=a2c4g.11186623.6.814.7b2e7c3bll5VE4

	public TSQL Prefix(OP op, String fieldName, String text) throws ServerException {
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(text)) {
			PrefixQuery q = new PrefixQuery(); // 设置查询类型为PrefixQuery
			q.setFieldName(fieldName);
			q.setPrefix(text);
			link(op, q);
		}
		return this;
	}

	///////// RangeQuery，范围查询
	// https://help.aliyun.com/document_detail/117496.html?spm=a2c4g.11186623.6.669.50df6c98z8c5zA

	// TODO 有问题，仍未实现

	public TSQL Range(OP op, String fieldName, RANGE range, Object value) throws ServerException {
		if (value != null) {
			RangeQuery q = new RangeQuery(); // 设置查询类型为RangeQuery
			q.setFieldName(fieldName); // 设置针对哪个字段
			q.greaterThan(ColumnValue.fromLong(3));
			// link(op, q);
		}
		return this;
	}

	///////// WildcardQuery，通配符查询，查询关键字中支持通配符
	// 星号("*")代表任意字符序列，或者用问号("?")代表任意单个字符
	// https://help.aliyun.com/document_detail/100420.html?spm=a2c4g.11186623.6.816.68817c3b04d8la

	public TSQL Wildcard(OP op, String fieldName, String text) throws ServerException {
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(text)) {
			WildcardQuery q = new WildcardQuery(); // 设置查询类型为WildcardQuery
			q.setFieldName(fieldName);
			q.setValue(text); // wildcardQuery支持通配符
			link(op, q);
		}
		return this;
	}

	///////// GeoBoundingBoxQuery，地理位置边界框查询，返回矩形范围内的数据
	// https://help.aliyun.com/document_detail/100421.html?spm=a2c4g.11186623.6.817.24445ed7NBQCdN

	public TSQL GeoBoundingBox(OP op, String fieldName, String topLeft, String bottomRight) throws ServerException {
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(topLeft)
				&& StringUtils.isNotBlank(bottomRight)) {
			GeoBoundingBoxQuery q = new GeoBoundingBoxQuery(); // 设置查询类型为GeoBoundingBoxQuery
			q.setFieldName(fieldName); // 设置比较哪个字段的值
			q.setTopLeft(topLeft); // 设置矩形左上角
			q.setBottomRight(bottomRight); // 设置矩形右下角
			link(op, q);
		}
		return this;
	}

	///////// GeoDistanceQuery，地理位置距离查询，返回有效距离内的数据
	// https://help.aliyun.com/document_detail/100421.html?spm=a2c4g.11186623.6.817.24445ed7NBQCdN

	public TSQL GeoDistance(OP op, String fieldName, String centerPoint, int distanceInMeter) throws ServerException {
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(centerPoint) && distanceInMeter > 0) {
			GeoDistanceQuery q = new GeoDistanceQuery(); // 设置查询类型为GeoDistanceQuery
			q.setFieldName(fieldName);
			q.setCenterPoint(centerPoint); // 设置中心点
			q.setDistanceInMeter(distanceInMeter); // 设置到中心点的距离
			link(op, q);
		}
		return this;
	}

	///////// GeoPolygonQuery，地理位置多边形查询，返回多边形内的数据
	// https://help.aliyun.com/document_detail/100421.html?spm=a2c4g.11186623.6.817.24445ed7NBQCdN

	public TSQL GeoPolygon(OP op, String fieldName, String... geoPoints) throws ServerException {
		if (StringUtils.isNotBlank(fieldName) && geoPoints != null && geoPoints.length > 0) {
			GeoPolygonQuery q = new GeoPolygonQuery(); // 设置查询类型为GeoPolygonQuery
			q.setFieldName(fieldName);
			q.setPoints(Arrays.asList(geoPoints)); // 设置多边形的顶点
			link(op, q);
		}
		return this;
	}

	///////// NestedQuery，嵌套查询，返回多边形内的数据
	// https://help.aliyun.com/document_detail/100423.html?spm=a2c4g.11186623.6.819.503811d8kerlWU

	// TODO 暂时不支持
}
