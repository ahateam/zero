package zyxhj.utils.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.Query;
import com.alicloud.openservices.tablestore.model.search.query.RangeQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermQuery;

import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.TSObjectMapper;

/**
 * 查询表达式工具</br>
 * 支持 && , || , AND , OR , and , or 连接词</br>
 * 支持 == , = , != , <> , > , < , >= , <= , LIKE 关键字</br>
 * 支持 IN 语句（直接用函数表达式）</br>
 * 不支持BETWEEN</br>
 * 
 * 表达式中的变量用{{}}包裹</br>
 */
public class EXP implements Cloneable {

	public static final String TYPE_EXP = "e";// 二元表达式
	public static final String TYPE_METHOD = "m";// 函数

	public String t;
	public String op;
	public List<Object> ps;

	public EXP(String op, List<Object> ps) {
		this.t = TYPE_METHOD;
		this.op = op;
		this.ps = ps;
	}

	public EXP(Object left, String op, Object right) {
		this.t = TYPE_EXP;
		this.op = op;
		this.ps = Arrays.asList(left, right);
	}

	protected EXP clone() {
		EXP clone = null;
		try {
			clone = (EXP) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return clone;
	}

	public EXP and(EXP exp) {
		EXP left = this.clone();
		EXP right = exp;

		this.t = TYPE_EXP;
		this.op = "&&";
		this.ps = Arrays.asList(left, right);

		return this;
	}

	public EXP and(Object l, String op, Object r) {
		return and(new EXP(l, op, r));
	}

	public EXP and(String op, List<Object> ps) {
		return and(new EXP(op, ps));
	}

	public EXP or(EXP exp) {
		EXP left = this.clone();
		EXP right = exp;

		this.t = TYPE_EXP;
		this.op = "||";
		this.ps = Arrays.asList(left, right);

		return this;
	}

	public EXP or(Object l, String op, Object r) {
		return or(new EXP(l, op, r));
	}

	public EXP or(String op, List<Object> ps) {
		return or(new EXP(op, ps));
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		toSQL(sb);
		return sb.toString();
	}

	private static String sqlFixOP(String op) {
		op = StringUtils.trim(op);
		if (op.equals("&&") || op.equals("AND") || op.equals("and")) {
			return " AND ";
		} else if (op.equals("||") || op.equals("OR") || op.equals("or")) {
			return " OR ";
		} else if (op.equals("!=") || op.equals("<>")) {
			return " <> ";
		} else if (op.equals("==") || op.equals("=")) {
			return " = ";
		} else {
			return StringUtils.join(" ", op, " ");
		}
	}

	private static String tsFixOP(String op) {
		op = StringUtils.trim(op);
		if (op.equals("&&") || op.equals("AND") || op.equals("and")) {
			return "AND";
		} else if (op.equals("||") || op.equals("OR") || op.equals("or")) {
			return "OR";
		} else if (op.equals("!=") || op.equals("<>")) {
			return "<>";
		} else if (op.equals("==") || op.equals("=")) {
			return "=";
		} else {
			return op;
		}
	}

	private Query makeQuery(String op, List<Object> ps, TSObjectMapper mapper) throws Exception {
		String newop = tsFixOP(op);

		if (newop.equals("=")) {
			// 精确匹配TermQuery
			String fieldName = (String) ps.get(0);
			fieldName = replaceJavaField2RDSField(fieldName, mapper);
			Object value = ps.get(1);

			TermQuery q = new TermQuery();
			q.setFieldName(fieldName);
			q.setTerm(ColumnBuilder.buildColumnValue(value));
			return q;
		} else if (newop.equals("<")) {
			// 范围查询RangeQuery
			String fieldName = (String) ps.get(0);
			fieldName = replaceJavaField2RDSField(fieldName, mapper);
			Object value = ps.get(1);

			RangeQuery q = new RangeQuery(); // 设置查询类型为RangeQuery
			q.setFieldName(fieldName); // 设置针对哪个字段
			q.lessThan(ColumnBuilder.buildColumnValue(value)); // 结束位置值
			return q;
		} else if (newop.equals(">")) {
			// 范围查询RangeQuery
			String fieldName = (String) ps.get(0);
			fieldName = replaceJavaField2RDSField(fieldName, mapper);
			Object value = ps.get(1);

			RangeQuery q = new RangeQuery(); // 设置查询类型为RangeQuery
			q.setFieldName(fieldName); // 设置针对哪个字段
			q.greaterThan(ColumnBuilder.buildColumnValue(value)); // 结束位置值
			return q;
		} else if (newop.equals("<=")) {
			// 范围查询RangeQuery
			String fieldName = (String) ps.get(0);
			fieldName = replaceJavaField2RDSField(fieldName, mapper);
			Object value = ps.get(1);

			RangeQuery q = new RangeQuery(); // 设置查询类型为RangeQuery

			q.setFieldName(fieldName); // 设置针对哪个字段
			q.lessThanOrEqual(ColumnBuilder.buildColumnValue(value)); // 结束位置值
			return q;
		} else if (newop.equals(">=")) {
			// 范围查询RangeQuery
			String fieldName = (String) ps.get(0);
			fieldName = replaceJavaField2RDSField(fieldName, mapper);
			Object value = ps.get(1);

			RangeQuery q = new RangeQuery(); // 设置查询类型为RangeQuery
			q.setFieldName(fieldName); // 设置针对哪个字段
			q.greaterThanOrEqual(ColumnBuilder.buildColumnValue(value)); // 结束位置值
			return q;
		} else if (newop.equals("<>")) {
			// 精确查询TermQuery，然后非查询BoolQuery.setMustNotQueries
			String fieldName = (String) ps.get(0);
			fieldName = replaceJavaField2RDSField(fieldName, mapper);
			Object value = ps.get(1);

			TermQuery q = new TermQuery();
			q.setFieldName(fieldName);
			q.setTerm(ColumnBuilder.buildColumnValue(value));

			BoolQuery bq = new BoolQuery();
			bq.setMustNotQueries(Arrays.asList(q));

			return bq;
		} else {
			throw new ServerException(BaseRC.REPOSITORY_SIMPLE_QUERY_TS_QUERY_UNSPORTED_ERROR, op);
		}
	}

	/**
	 * 修复表达式参数中的java变量{{}}
	 */
	private static String fixJavaFieldInPS(String ps) {
		String ret = ps.replace("{{", "");
		ret = ret.replace("}}", "");
		return ret;
	}

	private static String replaceJavaField2RDSField(String where, TSObjectMapper mapper) throws Exception {
		StringBuffer sb = new StringBuffer();
		int ind = 0;
		int start = 0;
		int end = 0;
		ArrayList<String> ret = new ArrayList<>();
		while (true) {
			start = where.indexOf("{{", ind);
			if (start < ind) {
				// 没有找到新的{，结束
				sb.append(where.substring(ind));
				break;
			} else {
				// 找到{，开始找配对的}
				end = where.indexOf("}}", start);
				if (end > start + 3) {
					// 找到结束符号
					sb.append(where.substring(ind, start));

					ind = end + 2;// 记录下次位置
					String javaField = where.substring(start + 2, end);
					String alias = mapper.getAliasByJavaFieldName(javaField);
					if (StringUtils.isBlank(alias)) {
						throw new ServerException(BaseRC.REPOSITORY_SIMPLE_QUERY_FIELD_ERROR, javaField);
					} else {
						sb.append(alias);
					}
				} else {
					// 没有找到匹配的结束符号，终止循环
					sb.append(where.substring(ind));
					break;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 */
	public Query toTSQuery(TSObjectMapper mapper) throws Exception {
		if (t.equals(TYPE_EXP)) {
			// 二元表达式
			Object left = ps.get(0);
			Object right = ps.get(1);
			String newop = tsFixOP(op);
			if (left instanceof EXP || right instanceof EXP) {
				Query lquery = ((EXP) left).toTSQuery(mapper);
				Query rquery = ((EXP) right).toTSQuery(mapper);

				BoolQuery boolQuery = new BoolQuery();
				if (newop.equals("AND")) {
					boolQuery.setMustQueries(Arrays.asList(lquery, rquery));
					return boolQuery;
				} else if (newop.equals("OR")) {
					boolQuery.setMinimumShouldMatch(1);// 至少满足一个条件
					boolQuery.setShouldQueries(Arrays.asList(lquery, rquery));
					return boolQuery;
				} else {
					throw new ServerException(BaseRC.REPOSITORY_SIMPLE_QUERY_TS_QUERY_UNSPORTED_ERROR, op);
				}
			} else {
				// left和right都不是表达式，开始分析并构建当前表达式的TSQuery
				return makeQuery(newop, ps, mapper);
			}
		} else {
			// 函数或其它
		}
		return null;
	}

	/**
	 * JSONObject格式的EXP表达式的解析，用于接口调用
	 */
	public static void jsonEXP2VirtualTableSQL(JSONObject exp, String jsonColumn, StringBuffer sb) throws Exception {
		String op = exp.getString("op");
		String t = exp.getString("t");
		List<Object> ps = exp.getJSONArray("ps");

		if (t.equals(TYPE_EXP)) {
			// 表达式
			Object left = ps.get(0);
			Object right = ps.get(1);

			if (left instanceof JSONObject) {
				// 递归
				sb.append('(');
				jsonEXP2VirtualTableSQL((JSONObject) left, jsonColumn, sb);
				sb.append(')');
			} else {
				// JSON虚拟表中的字段名，无需进行java转换，直接取出{{}}即可
				String temp = StringUtils.trim(left.toString());
				String ll = fixJavaFieldInPS(temp);
				if (temp.equals(ll)) {
					// 没有java变量
					sb.append(ll);
				} else {
					// 有java变量
					// data->'$.c_data.COL5' = 2
					sb.append(jsonColumn).append("->'$.c_data.").append(ll).append("'");
				}
			}
			sb.append(sqlFixOP(op));
			if (right instanceof JSONObject) {
				sb.append('(');
				jsonEXP2VirtualTableSQL((JSONObject) right, jsonColumn, sb);
				sb.append(')');
			} else {
				// JSON虚拟表中的字段名，无需进行java转换，直接取出{{}}即可
				String temp = StringUtils.trim(right.toString());
				String rr = fixJavaFieldInPS(temp);
				if (temp.equals(rr)) {
					// 没有java变量
					sb.append(rr);
				} else {
					// 有java变量
					// data->'$.c_data.COL5' = 2
					sb.append(jsonColumn).append("->'$.c_data.").append(rr).append("'");
				}
			}
		} else {
			// 方法
			sb.append(op).append('(');
			for (int i = 0; i < ps.size(); i++) {
				sb.append(ps.get(i));
				if (i < ps.size() - 1) {
					sb.append(',');
				}
			}
			sb.append(')');
		}

	}

	public void toSQL(StringBuffer sb) {
		if (t.equals(TYPE_EXP)) {
			// 二元表达式
			Object left = ps.get(0);
			Object right = ps.get(1);
			if (left instanceof EXP) {
				sb.append('(');
				((EXP) left).toSQL(sb);
				sb.append(')');
			} else {
				sb.append(left);
			}
			sb.append(sqlFixOP(op));
			if (right instanceof EXP) {
				sb.append('(');
				((EXP) right).toSQL(sb);
				sb.append(')');
			} else {
				sb.append(right);
			}
		} else {
			// 函数或其它
			sb.append(op).append('(');
			for (int i = 0; i < ps.size(); i++) {
				Object p = ps.get(i);
				sb.append(p);
				if (i < ps.size() - 1) {
					sb.append(',');
				}
			}
			sb.append(')');
		}
	}

	public static void main(String[] args) {

		EXP exp = new EXP("a", "=", "1").and("b", "<", 123);

		EXP subExp = new EXP("q", "==", "").or("max", Arrays.asList(123, 234));

		exp.and(subExp);

		System.out.println("<<<" + JSON.toJSONString(exp,true));

		StringBuffer sb = new StringBuffer();
		exp.toSQL(sb);
		System.out.println(sb.toString());
	}

}
