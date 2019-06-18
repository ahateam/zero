package zyxhj.utils.data.ts;

import java.util.ArrayList;
import java.util.List;

import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.Query;
import com.alicloud.openservices.tablestore.model.search.query.TermQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermsQuery;
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import com.alicloud.openservices.tablestore.model.search.sort.Sort.Sorter;

import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;

public class TSQL {

	private static final int AND = 0;
	private static final int OR = 1;
	private static final int NOT = 2;

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

	private int op = -1;

	public TSQL() {
		queries = new ArrayList<>();
		sorts = new ArrayList<>();
	}

	public TSQL setFirst(Query query) {
		queries.clear();
		queries.add(query);
		return this;
	}

	public TSQL setFirst(TSQL query) {
		queries.clear();
		queries.add(query);
		return this;
	}

	private TSQL link(int op, Object query) throws ServerException {
		if (query != null) {
			if (op == AND || op == OR || op == NOT) {
				if (queries.size() <= 0) {
					// 空，直接添加
					queries.add(query);
				} else if (queries.size() == 1) {
					// 只有一个节点，无需判断之前的操作符op是否相同
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

	public TSQL AND(Query query) throws ServerException {
		return link(AND, query);
	}

	public TSQL AND(TSQL query) throws ServerException {
		return link(AND, query);
	}

	public TSQL OR(Query query) throws ServerException {
		return link(OR, query);
	}

	public TSQL OR(TSQL query) throws ServerException {
		return link(OR, query);
	}

	public TSQL NOT(Query query) throws ServerException {
		return link(NOT, query);
	}

	public TSQL NOT(TSQL query) throws ServerException {
		return link(NOT, query);
	}

	///////// TermQuery

	private TSQL linkTerm(int op, String fieldName, Object key) throws ServerException {
		if (null != key) {
			TermQuery q = new TermQuery();
			q.setFieldName(fieldName);
			q.setTerm(ColumnBuilder.buildColumnValue(key)); // 设置要匹配的值
			link(op, q);
		}
		return this;
	}

	public TSQL setFirstTerm(String fieldName, Object key) {
		if (null != key) {
			TermQuery q = new TermQuery();
			q.setFieldName(fieldName);
			q.setTerm(ColumnBuilder.buildColumnValue(key)); // 设置要匹配的值
			setFirst(q);
		}
		return this;
	}

	public TSQL ANDTerm(String fieldName, Object key) throws ServerException {
		return linkTerm(AND, fieldName, key);
	}

	public TSQL ORTerm(String fieldName, Object key) throws ServerException {
		return linkTerm(OR, fieldName, key);
	}

	public TSQL NOTTerm(String fieldName, Object key) throws ServerException {
		return linkTerm(NOT, fieldName, key);
	}

	///////// TermsQuery

	private TSQL linkTerms(int op, String fieldName, Object... keys) throws ServerException {
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

	public TSQL setFirstTerms(String fieldName, Object... keys) throws ServerException {
		if (keys != null && keys.length > 0) {
			TermsQuery q = new TermsQuery();
			q.setFieldName(fieldName);
			for (Object key : keys) {
				q.addTerm(ColumnBuilder.buildColumnValue(key));
			}
			setFirst(q);
		}
		return this;
	}

	public TSQL ANDTerms(String fieldName, Object... keys) throws ServerException {
		return linkTerms(AND, fieldName, keys);
	}

	public TSQL ORTerms(String fieldName, Object... keys) throws ServerException {
		return linkTerms(OR, fieldName, keys);
	}

	public TSQL NOTTerms(String fieldName, Object... keys) throws ServerException {
		return linkTerms(NOT, fieldName, keys);
	}

	///////// MatchAllQuery

	///////// MatchQuery

	///////// MatchPhraseQuery

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
			if (op == AND) {
				ret.setMustQueries(qs);
			} else if (op == OR) {
				ret.setMinimumShouldMatch(1);// 至少满足一个条件
				ret.setShouldQueries(qs);
			} else if (op == NOT) {
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

}
