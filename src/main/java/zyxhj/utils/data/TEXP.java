package zyxhj.utils.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alicloud.openservices.tablestore.model.ColumnValue;
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

import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ts.ColumnBuilder;

/**
 * 查询表达式工具</br>
 * 支持 && , || , AND , OR , and , or 连接词</br>
 * 支持 == , = , != , <> , > , < , >= , <= , LIKE 关键字</br>
 * 支持 IN 语句（直接用函数表达式）</br>
 * 不支持BETWEEN</br>
 * 
 * 表达式中的变量用{{}}包裹</br>
 */
public class TEXP {

	/**
	 * TermQuery，单值精确匹配</br>
	 * https://help.aliyun.com/document_detail/100416.html?spm=a2c4g.11186623.6.812.f8cd494dyqoi7J
	 */
	public static Query Term(String fieldName, Object key) throws ServerException {
		TermQuery q = null;
		if (null != key) {
			q = new TermQuery();
			q.setFieldName(fieldName);
			q.setTerm(ColumnBuilder.buildColumnValue(key)); // 设置要匹配的值
		}
		return q;
	}

	/**
	 * TermsQuery，多值精确匹配</br>
	 * https://help.aliyun.com/document_detail/100416.html?spm=a2c4g.11186623.6.812.f8cd494dyqoi7J
	 */
	public static Query Terms(String fieldName, Object... keys) throws ServerException {
		TermsQuery q = null;
		if (keys != null && keys.length > 0) {
			q = new TermsQuery();
			q.setFieldName(fieldName);
			for (Object key : keys) {
				q.addTerm(ColumnBuilder.buildColumnValue(key));
			}
		}
		return q;
	}

	/**
	 * MatchAllQuery，全匹配，相当于任意匹配</br>
	 * https://help.aliyun.com/document_detail/100417.html?spm=a2c4g.11186623.6.813.2cb45bd8MLAGSE
	 */
	public static Query MatchAll() throws ServerException {
		return new MatchAllQuery();
	}

	/**
	 * MatchQuery，关键词近似匹配</br>
	 * https://help.aliyun.com/document_detail/100417.html?spm=a2c4g.11186623.6.813.2cb45bd8MLAGSE
	 */
	public static Query Match(String fieldName, String text) throws ServerException {
		MatchQuery q = null;
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(text)) {
			q = new MatchQuery(); // 设置查询类型为MatchQuery
			q.setFieldName(fieldName); // 设置要匹配的字段
			q.setText(text); // 设置要匹配的值
		}
		return q;
	}

	/**
	 * MatchPhraseQuery，短语匹配，关键词顺序不会乱</br>
	 * https://help.aliyun.com/document_detail/100417.html?spm=a2c4g.11186623.6.813.2cb45bd8MLAGSE
	 */
	public static Query MatchPhrase(String fieldName, String text) throws ServerException {
		MatchPhraseQuery q = null;
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(text)) {
			q = new MatchPhraseQuery(); // 设置查询类型为MatchPhraseQuery
			q.setFieldName(fieldName); // 设置要匹配的字段
			q.setText(text); // 设置要匹配的值
		}
		return q;
	}

	/**
	 * PrefixQuery，前缀匹配</br>
	 * https://help.aliyun.com/document_detail/100418.html?spm=a2c4g.11186623.6.814.7b2e7c3bll5VE4
	 */
	public static Query Prefix(String fieldName, String text) throws ServerException {
		PrefixQuery q = null;
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(text)) {
			q = new PrefixQuery(); // 设置查询类型为PrefixQuery
			q.setFieldName(fieldName);
			q.setPrefix(text);
		}
		return q;
	}

	/**
	 * RangeQuery，范围查询</br>
	 * https://help.aliyun.com/document_detail/117496.html?spm=a2c4g.11186623.6.669.50df6c98z8c5zA</br>
	 * TODO direction没做
	 */
	public static Query Range(String fieldName, ColumnValue from, ColumnValue to) throws ServerException {

		// TODO 有问题，仍未完全实现

		RangeQuery q = new RangeQuery(); // 设置查询类型为RangeQuery
		q.setFieldName(fieldName); // 设置针对哪个字段
		q.setFrom(from); // 开始位置值
		q.setTo(to); // 结束位置值
		// link(op, q);
		return q;
	}

	/**
	 * WildcardQuery，通配符查询，查询关键字中支持通配符</br>
	 * 星号("*")代表任意字符序列，或者用问号("?")代表任意单个字符</br>
	 * https://help.aliyun.com/document_detail/100420.html?spm=a2c4g.11186623.6.816.68817c3b04d8la
	 */
	public static Query Wildcard(String fieldName, String text) throws ServerException {
		WildcardQuery q = null;
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(text)) {
			q = new WildcardQuery(); // 设置查询类型为WildcardQuery
			q.setFieldName(fieldName);
			q.setValue(text); // wildcardQuery支持通配符
		}
		return q;
	}

	/**
	 * GeoBoundingBoxQuery，地理位置边界框查询，返回矩形范围内的数据</br>
	 * https://help.aliyun.com/document_detail/100421.html?spm=a2c4g.11186623.6.817.24445ed7NBQCdN
	 */
	public static Query GeoBoundingBox(String fieldName, String topLeft, String bottomRight) throws ServerException {
		GeoBoundingBoxQuery q = null;
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(topLeft)
				&& StringUtils.isNotBlank(bottomRight)) {
			q = new GeoBoundingBoxQuery(); // 设置查询类型为GeoBoundingBoxQuery
			q.setFieldName(fieldName); // 设置比较哪个字段的值
			q.setTopLeft(topLeft); // 设置矩形左上角
			q.setBottomRight(bottomRight); // 设置矩形右下角
		}
		return q;
	}

	/**
	 * GeoDistanceQuery，地理位置距离查询，返回有效距离内的数据</br>
	 * https://help.aliyun.com/document_detail/100421.html?spm=a2c4g.11186623.6.817.24445ed7NBQCdN
	 */
	public static Query GeoDistance(String fieldName, String centerPoint, int distanceInMeter) throws ServerException {
		GeoDistanceQuery q = null;
		if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(centerPoint) && distanceInMeter > 0) {
			q = new GeoDistanceQuery(); // 设置查询类型为GeoDistanceQuery
			q.setFieldName(fieldName);
			q.setCenterPoint(centerPoint); // 设置中心点
			q.setDistanceInMeter(distanceInMeter); // 设置到中心点的距离
		}
		return q;
	}

	/**
	 * GeoPolygonQuery，地理位置多边形查询，返回多边形内的数据</br>
	 * https://help.aliyun.com/document_detail/100421.html?spm=a2c4g.11186623.6.817.24445ed7NBQCdN
	 */
	public static Query GeoPolygon(String fieldName, String... geoPoints) throws ServerException {
		GeoPolygonQuery q = null;
		if (StringUtils.isNotBlank(fieldName) && geoPoints != null && geoPoints.length > 0) {
			q = new GeoPolygonQuery(); // 设置查询类型为GeoPolygonQuery
			q.setFieldName(fieldName);
			q.setPoints(Arrays.asList(geoPoints)); // 设置多边形的顶点
		}
		return q;
	}

	public static String BOOL_TYPE_AND = "&&";
	public static String BOOL_TYPE_OR = "||";
	public static String BOOL_TYPE_NOT = "!";

	/**
	 * BoolQuery，多字段自由组合查询</br>
	 * https://help.aliyun.com/document_detail/117498.html?spm=a2c4g.11186623.6.628.62025d84DV2WNU
	 */
	public static Query Bool(String boolType, List<Query> queries) {
		BoolQuery q = null;

		if (queries != null && queries.size() > 0) {

			// 移除空查询
			ArrayList<Query> nqs = new ArrayList<>();
			for (Query t : queries) {
				if (t != null) {
					nqs.add(t);
				}
			}

			if (nqs.size() > 0) {
				q = new BoolQuery();
				if (boolType.equals(BOOL_TYPE_AND)) {
					q.setMustQueries(nqs);
				} else if (boolType.equals(BOOL_TYPE_NOT)) {
					q.setMustNotQueries(nqs);
				} else {
					// OR
					q.setMinimumShouldMatch(1);// 至少满足一个条件
					q.setShouldQueries(nqs);
				}
			}

		}

		return q;
	}
}
