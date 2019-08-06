package zyxhj.utils.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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

	private static final String TYPE_EXP = "e";// 二元表达式
	private static final String TYPE_METHOD = "m";// 函数

	/**
	 * 是否严谨，true则表达式右参不允许null，false则右参null时自动不添加该表达式</br>
	 * (常用于多条件查询，右参null时该表达式无效)
	 */
	private boolean exact;

	/**
	 * 表达式类型
	 */
	private String t;
	/**
	 * 表达式操作符
	 */
	private String op;

	/**
	 * 表达式操作数</br>
	 * 例如二元表达式中，ps[0]为左参，ps[1]为右参</br>
	 * （同SQL一样，可用通配符 ? 表示需要被替换的参数，避免SQL注入）
	 */
	private List<Object> ps;

	/**
	 * 表达式参数（选填）</br>
	 * 如果表达式中存在 ? 通配符，则参数与 ? 按顺序逐一匹配
	 */
	private Object[] args;

	private EXP(boolean exact) {
		this.exact = exact;
	}

	/**
	 * 构造EXP实例
	 * 
	 * @param exact
	 *            是否严谨，true则表达式右参不允许null，false则右参null时自动不添加该表达式</br>
	 *            (常用于多条件查询，右参null时该表达式无效)
	 */
	public static EXP ins(boolean exact) {
		return new EXP(exact);
	}

	/**
	 * 构造EXP实例，默认为严谨表达式
	 */
	public static EXP ins() {
		return new EXP(true);
	}

	/**
	 * SQL的 LIKE 语句
	 */
	public static EXP like(String key, String str) {
		// LIKE语句中，不支持?号参数
		return new EXP(false).exp(StringUtils.join(key, " LIKE '%", str, "%'"), null);
	}

	/**
	 * SQL的 IN 语句，例如 where id IN(1,2,3)
	 */
	public static EXP in(String key, Object... args) {
		StringBuffer sb = new StringBuffer(key);
		sb.append(" IN(");
		for (Object arg : args) {
			sb.append("?,");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");

		return new EXP(false).exp(sb.toString(), null, args);
	}

	/**
	 * SQL的 IN 语句（带排序）</br>
	 */
	public static EXP inOrdered(String key, Object... args) {
		StringBuffer sb = new StringBuffer(key);
		sb.append(" IN(");
		for (Object arg : args) {
			sb.append("?,");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(") ORDER BY FIND_IN_SET(").append(key).append(",");

		for (int i = 0; i < args.length; i++) {
			Object o = args[i];
			if (i < args.length - 1) {
				sb.append(o).append(',');
			} else {
				sb.append(o);
			}
		}
		sb.append(')');

		return new EXP(false).exp(sb.toString(), null, args);
	}

	private static boolean isNumber(Object obj) {
		if (obj instanceof Integer || //
				obj instanceof Short || //
				obj instanceof Byte || //
				obj instanceof Long || //
				obj instanceof Double || //
				obj instanceof Float) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * JSON操作，添加到数组
	 * 
	 * @param column
	 *            字段列名（JSON数组字段）
	 * @param value
	 *            要添加的值，可以是数字或字符
	 * @param duplicate
	 *            数组是否允许重复
	 */
	public static EXP jsonArrayAppend(String column, Object value, boolean duplicate) {
		String temp = null;

		if (isNumber(value)) {
			// 数字，语句中不加引号，但是JSON_CONTAINS中仍然需要单引号
			if (duplicate) {
				// 允许重复
				temp = StringUtils.join(column, " = IF((ISNULL(", column, ") || LENGTH(trim(", column,
						"))<1), JSON_ARRAY(", value, "), JSON_ARRAY_APPEND(", column, ",'$',", value, "))");
			} else {
				// 不允许重复
				temp = StringUtils.join(column, " = IF((ISNULL(", column, ") || LENGTH(trim(", column,
						"))<1), JSON_ARRAY(", value, "), IF(JSON_CONTAINS(", column, ",'", value, "','$'),", column,
						",JSON_ARRAY_APPEND(", column, ",'$',", value, ")))");
			}
		} else {
			// 字符，语句中要注意引号的使用
			if (duplicate) {
				// 允许重复

				// UPDATE `tb_rds_test` SET arrays = IF ((ISNULL(arrays) ||
				// LENGTH(trim(arrays))<1), JSON_ARRAY('tag1'), JSON_ARRAY_APPEND(arrays,'$'
				// ,'tag2')) WHERE `id` = 400570032412653

				temp = StringUtils.join(column, " = IF((ISNULL(", column, ") || LENGTH(trim(", column,
						"))<1), JSON_ARRAY('", value, "'), JSON_ARRAY_APPEND(", column, ",'$','", value, "'))");
			} else {
				// 不允许重复

				// UPDATE `tb_rds_test` SET arrays = IF ((ISNULL(arrays) ||
				// LENGTH(trim(arrays))<1), JSON_ARRAY('tag1'), IF(ISNULL(JSON_SEARCH(arrays,
				// 'one', 'tag3')),JSON_ARRAY_APPEND(arrays,'$' ,'tag3'),arrays) ) WHERE `id` =
				// 400570032412653

				temp = StringUtils.join(column, " = IF((ISNULL(", column, ") || LENGTH(trim(", column,
						"))<1), JSON_ARRAY('", value, "'), IF(JSON_CONTAINS(", column, ",'\"", value, "\"','$'),",
						column, ",JSON_ARRAY_APPEND(", column, ",'$','", value, "')))");
			}
		}
		return new EXP(false).exp(temp, null);
	}

	/**
	 * JSON操作，添加到数组，数组值将添加到column.key对应的数组下
	 * 
	 * @param column
	 *            字段列名（JSON数组字段）
	 * @param key
	 *            字段内的JSON key，数组值将添加到column.key对应的数组下
	 * @param value
	 *            要添加的值，可以是数字或字符
	 * @param duplicate
	 *            数组是否允许重复
	 */
	public static EXP jsonArrayAppendOnKey(String column, String key, Object value, boolean duplicate) {
		String temp = null;
		if (isNumber(value)) {
			// 数字，语句中不加引号，但是JSON_CONTAINS中仍然需要单引号
			if (duplicate) {
				// 允许重复
				temp = StringUtils.join(column, " = IF((ISNULL(", column, ") || LENGTH(trim(", column,
						"))<1),JSON_OBJECT('", key, "',JSON_ARRAY(", value, ")),IF(JSON_CONTAINS_PATH(", column,
						",'one','$.", key, "'),JSON_ARRAY_APPEND(", column, ", '$.", key, "' ,", value, "),JSON_SET(",
						column, ",'$.", key, "',JSON_ARRAY(", value, "))))");
			} else {
				// 不允许重复
				temp = StringUtils.join(column, " = IF((ISNULL(", column, ") || LENGTH(trim(", column,
						"))<1),JSON_OBJECT('", key, "',JSON_ARRAY(", value, ")),IF(JSON_CONTAINS_PATH(", column,
						",'one','$.", key, "'),IF(JSON_CONTAINS(", column, ",'", value, "','$.", key, "'),", column,
						",JSON_ARRAY_APPEND(", column, ", '$.", key, "' ,", value, ")),JSON_SET(", column, ",'$.", key,
						"',JSON_ARRAY(", value, "))))");
			}
		} else {
			// 字符，语句中要注意引号的使用
			if (duplicate) {
				// 允许重复

				// UPDATE `tb_rds_test` SET tags = IF((ISNULL(tags) ||
				// LENGTH(trim(tags))<1),JSON_OBJECT('type',JSON_ARRAY('tag1')),IF(JSON_CONTAINS_PATH(tags,'one','$.type'),JSON_ARRAY_APPEND(tags,
				// '$.type' ,'tag8'),JSON_SET(tags,'$.type',JSON_ARRAY('tag8')))) WHERE `id` =
				// 400570032412653

				temp = StringUtils.join(column, " = IF((ISNULL(", column, ") || LENGTH(trim(", column,
						"))<1),JSON_OBJECT('", key, "',JSON_ARRAY('", value, "')),IF(JSON_CONTAINS_PATH(", column,
						",'one','$.", key, "'),JSON_ARRAY_APPEND(", column, ", '$.", key, "' ,'", value, "'),JSON_SET(",
						column, ",'$.", key, "',JSON_ARRAY('", value, "'))))");
			} else {
				// 不允许重复

				// UPDATE `tb_rds_test` SET tags = IF((ISNULL(tags) ||
				// LENGTH(trim(tags))<1),JSON_OBJECT('type',JSON_ARRAY('tag1')),IF(JSON_CONTAINS_PATH(tags,'one','$.type'),IF(JSON_CONTAINS(tags,'"tag8"','$.type'),tags,JSON_ARRAY_APPEND(tags,
				// '$.type' ,'tag8')),JSON_SET(tags,'$.type',JSON_ARRAY('tag8')))) WHERE `id` =
				// 400570032412653

				temp = StringUtils.join(column, " = IF((ISNULL(", column, ") || LENGTH(trim(", column,
						"))<1),JSON_OBJECT('", key, "',JSON_ARRAY('", value, "')),IF(JSON_CONTAINS_PATH(", column,
						",'one','$.", key, "'),IF(JSON_CONTAINS(", column, ",'\"", value, "\"','$.", key, "'),", column,
						",JSON_ARRAY_APPEND(", column, ", '$.", key, "' ,'", value, "')),JSON_SET(", column, ",'$.",
						key, "',JSON_ARRAY('", value, "'))))");
			}
		}
		return new EXP(false).exp(temp, null);
	}

	/**
	 * 移除JSON数组元素
	 * 
	 * @param column
	 *            JSON列
	 * @param path
	 *            JSON PATH
	 * @param index
	 *            数组索引编号，从0开始
	 */
	public static EXP jsonArrayRemove(String column, String path, int index) {
		String temp = StringUtils.join(column, " = JSON_REMOVE(", column, ",'", path, "[", index, "]')");
		return new EXP(false).exp(temp, null);
	}

	/**
	 * 判断JSON中是否包含指定元素的语句
	 * 
	 * @param column
	 *            字段名
	 * @param path
	 *            JSON Path，例如$.tags或$
	 * @param value
	 *            需要查找的值
	 */
	public static EXP jsonContains(String column, String path, Object value) {
		String temp = null;
		if (isNumber(value)) {
			// 数字，语句中不加引号，但是JSON_CONTAINS中仍然需要单引号
			temp = StringUtils.join("JSON_CONTAINS(", column, ", '", value, "','", path, "')");
		} else {
			// 字符，语句中要注意引号的使用
			temp = StringUtils.join("JSON_CONTAINS(", column, ", '\"", value, "\"','", path, "')");
		}
		return new EXP(false).exp(temp, null);
	}

	/**
	 * SQL的 key = value 语句（很常用）</br>
	 * 
	 */
	public EXP key(String key, Object value) throws ServerException {
		return exp(key, "=", "?", value);
	}

	/**
	 * 拷贝构造
	 * 
	 * @param exp
	 *            需要拷贝的表达式
	 */
	public EXP exp(EXP exp) {
		EXP c = exp.clone();

		this.op = c.op;
		this.ps = c.ps;
		this.t = c.t;
		this.args = c.args;

		return this;
	}

	/**
	 * 函数方法构造
	 * 
	 * @param str
	 *            表达式函数文本
	 * @param ps
	 *            表达式操作数</br>
	 *            例如二元表达式中，ps[0]为左参，ps[1]为右参</br>
	 *            （同SQL一样，可用通配符 ? 表示需要被替换的参数，避免SQL注入）
	 * @param args
	 *            表达式参数（选填）</br>
	 *            如果表达式中存在 ? 通配符，则参数与 ? 按顺序逐一匹配
	 */
	public EXP exp(String str, List<Object> ps, Object... args) {
		this.t = TYPE_METHOD;
		this.op = str;
		this.ps = ps;
		this.args = args;

		return this;
	}

	/**
	 * 二元表达式构造
	 * 
	 * @param left
	 *            表达式左操作数</br>
	 * @param op
	 *            表达式操作符
	 * @param right
	 *            表达式右操作数</br>
	 *            （同SQL一样，可用通配符 ? 表示需要被替换的参数，避免SQL注入）
	 * @param args
	 *            表达式参数（选填）</br>
	 *            如果表达式中存在 ? 通配符，则参数与 ? 按顺序逐一匹配
	 */
	public EXP exp(Object left, String op, Object right, Object... args) throws ServerException {
		if (isShit(exact, right, args)) {
			// 宽松验证参数，且当前右参为空，放弃本次添加
			return this;
		} else {
			this.t = TYPE_EXP;
			this.op = op;
			this.ps = Arrays.asList(left, right);
			this.args = args;

			return this;
		}
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

	/**
	 * AND 连接表达式
	 */
	public EXP and(EXP exp) throws ServerException {
		if (StringUtils.isBlank(this.op)) {
			// empty 相当于创建
			exp(exp);
			return this;
		} else {
			if (exp == null) {
				return this;
			} else {
				EXP left = this.clone();
				EXP right = exp;

				this.t = TYPE_EXP;
				this.op = "&&";
				this.ps = Arrays.asList(left, right);
				this.args = null;

				return this;
			}
		}
	}

	/**
	 * AND 连接表达式
	 */
	public EXP and(String op, List<Object> ps, Object... args) throws ServerException {
		if (StringUtils.isBlank(this.op)) {
			// empty 相当于创建
			exp(op, ps, args);
			return this;
		} else {
			if (StringUtils.isBlank(op)) {
				return this;
			} else {
				return and(new EXP(this.exact).exp(op, ps, args));
			}
		}
	}

	/**
	 * AND 连接表达式
	 */
	public EXP and(Object l, String op, Object r, Object... args) throws ServerException {
		if (isShit(exact, r, args)) {
			// 宽松验证参数，且当前右参为空，放弃本次添加
			return this;
		} else {
			if (StringUtils.isBlank(this.op)) {
				// empty 相当于创建
				exp(l, op, r, args);
				return this;
			} else {
				if (l == null || r == null || StringUtils.isBlank(op)) {
					return this;
				} else {
					return and(new EXP(this.exact).exp(l, op, r, args));
				}
			}
		}
	}

	/**
	 * AND 连接key表达式（很常用，相当于key = value表达式）
	 */
	public EXP andKey(String key, Object value) throws ServerException {
		return and(key, "=", "?", value);
	}

	/**
	 * OR 连接表达式
	 */
	public EXP or(EXP exp) throws ServerException {
		if (StringUtils.isBlank(this.op)) {
			// empty 相当于创建
			exp(exp);
			return this;
		} else {
			if (exp == null) {
				return this;
			} else {
				EXP left = this.clone();
				EXP right = exp;

				this.t = TYPE_EXP;
				this.op = "||";
				this.ps = Arrays.asList(left, right);
				this.args = null;

				return this;
			}
		}
	}

	/**
	 * OR 连接表达式
	 */
	public EXP or(String op, List<Object> ps, Object... args) throws ServerException {
		if (StringUtils.isBlank(this.op)) {
			// empty 相当于创建
			exp(op, ps, args);
			return this;
		} else {
			if (StringUtils.isBlank(op)) {
				return this;
			} else {
				return or(new EXP(this.exact).exp(op, ps, args));
			}
		}
	}

	/**
	 * OR 连接表达式
	 */
	public EXP or(Object l, String op, Object r, Object... args) throws ServerException {
		if (isShit(exact, r, args)) {
			// 宽松验证参数，且当前右参为空，放弃本次添加
			return this;
		} else {
			if (StringUtils.isBlank(this.op)) {
				// empty 相当于创建
				exp(l, op, r, args);
				return this;
			} else {
				if (l == null || r == null || StringUtils.isBlank(op)) {
					return this;
				} else {
					return or(new EXP(this.exact).exp(l, op, r, args));
				}
			}
		}
	}

	/**
	 * OR 连接key表达式（很常用，相当于key = value表达式）
	 */
	public EXP orKey(String key, Object value) throws ServerException {
		return or(key, "=", "?", value);
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

	/**
	 * 计算表达式，修正
	 */
	private static String fixOP(String op) {
		op = StringUtils.trim(op);
		if (op.equals("&&") || op.equals("AND") || op.equals("and")) {
			return " && ";
		} else if (op.equals("||") || op.equals("OR") || op.equals("or")) {
			return " || ";
		} else if (op.equals("!=") || op.equals("<>")) {
			return " != ";
		} else if (op.equals("==") || op.equals("=")) {
			return " == ";
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

	private static boolean isShit(boolean exact, Object right, Object[] args) throws ServerException {
		// if ("?".equals(right.toString().trim())) {
		// System.out.println("??");
		// }
		// if (args != null) {
		// System.out.println(args.length);
		// System.out.println(args[0] == null ? "yes" : "no");
		// }
		if (exact) {
			// 严谨表达式，参数不能为空，为空抛异常
			if ("?".equals(right.toString().trim()) && args != null && args.length > 0 && args[0] == null) {
				throw new ServerException(BaseRC.REPOSITORY_EXP_PARAM_NULL);
			} else {
				return false;
			}
		} else {
			// 宽松表达式，参数为空则放弃当前语句
			if ("?".equals(right.toString().trim()) && args != null && args.length > 0 && args[0] == null) {
				return true;
			} else {
				return false;
			}
		}
	}

	private static boolean isEndEXP(EXP src) {
		if (src.t.equals(TYPE_METHOD)) {
			return true;
		} else {
			Object left = src.ps.get(0);
			Object right = src.ps.get(1);
			if (left instanceof EXP || right instanceof EXP) {
				// 任意一个节点是表达式，则该表达式不是最终节点
				return false;
			} else {
				return true;
			}
		}
	}

	public void toEXP(StringBuffer sb) throws ServerException {
		if (t.equals(TYPE_EXP)) {
			// 二元表达式
			Object left = ps.get(0);
			Object right = ps.get(1);

			if (left instanceof EXP) {
				((EXP) left).toEXP(sb);
			} else {
				sb.append(left);
			}

			sb.append(fixOP(op));
			if (right instanceof EXP) {
				EXP er = (EXP) right;
				boolean isEnd = EXP.isEndEXP(er);// 下一个节点是否最终节点
				if (!isEnd) {
					sb.append("(");
				}
				er.toEXP(sb);
				if (!isEnd) {
					sb.append(")");
				}
			} else {

				if (right instanceof String) {
					if (StringUtils.trim((String) right).equals("?")) {
						// 替换？，合并参数
						sb.append(args[0]);
					} else {
						sb.append(right);
					}
				} else {
					sb.append(right);
				}
			}
		} else {
			// 函数或其它
			sb.append(op);
			if (ps != null && ps.size() > 0) {
				sb.append('(');
				int argIndex = 0;
				for (int i = 0; i < ps.size(); i++) {
					Object p = ps.get(i);
					if (StringUtils.trim((String) p).equals("?")) {
						// 替换？，合并参数
						sb.append(args[argIndex++]);
					} else {
						sb.append(p);
					}
					if (i < ps.size() - 1) {
						sb.append(',');
					}
				}
				sb.append(')');
			}
		}
	}

	private Object getValue(Object p) {
		return p;
	}

	private int comp(Object left, Object right) {
		int comp = 0;
		try {
			if (left instanceof Integer) {
				comp = ((Integer) left).compareTo((Integer) right);
			} else if (left instanceof Float) {
				comp = ((Float) left).compareTo((Float) right);
			} else if (left instanceof Double) {
				comp = ((Double) left).compareTo((Double) right);
			} else if (left instanceof Short) {
				comp = ((Short) left).compareTo((Short) right);
			} else if (left instanceof Byte) {
				comp = ((Byte) left).compareTo((Byte) right);
			} else if (left instanceof String) {
				comp = ((String) left).compareTo((String) right);
			} else {
				comp = 0;
			}
		} catch (Exception e) {
			return 0;
		}
		return comp;
	}

	private Object shit(Object left, String op, Object right) {

		if (op.equalsIgnoreCase("LIKE")) {
			// like运算符（字符串匹配）
			return (left.toString()).contains(right.toString()) ? 1 : 0;
		} else if (op.equals("<") || op.equals(">") || op.equals("==") || //
				op.equals("<=") || op.equals(">=")) {
			// 比较运算符
			int comp = comp(left, right);

			if (op.equals("<")) {
				return comp < 0 ? 1 : 0;
			} else if (op.equals(">")) {
				return comp > 0 ? 1 : 0;
			} else if (op.equals("==")) {
				return comp == 0 ? 1 : 0;
			} else if (op.equals("<=")) {
				return comp > 0 ? 0 : 1;
			} else if (op.equals(">=")) {
				return comp < 0 ? 0 : 1;
			} else if (op.equals("!=")) {
				return comp == 0 ? 0 : 1;
			} else {
				return 0;
			}
		} else if (op.equals("&&")) {
			int comp1 = comp(left, 0);
			int comp2 = comp(right, 0);
			return (comp1 > 0) && (comp2 > 0) ? 1 : 0;
		} else if (op.equals("||")) {
			int comp1 = comp(left, 0);
			int comp2 = comp(right, 0);
			return (comp1 > 0) || (comp2 > 0) ? 1 : 0;
		} else {
			return 0;
		}
	}

	public Object compute() {
		if (t.equals(TYPE_EXP)) {
			// 二元表达式
			Object left = ps.get(0);
			Object right = ps.get(1);

			Object leftValue;
			if (left instanceof EXP) {
				leftValue = ((EXP) left).compute();
			} else {
				// 最终左参数，变量表达式
				leftValue = getValue(left);
			}

			Object rightValue;
			if (right instanceof EXP) {
				rightValue = ((EXP) right).compute();
			} else {
				if (StringUtils.trim((String) right).equals("?")) {
					// 替换？，合并参数
					rightValue = getValue(args[0]);
				} else {
					rightValue = getValue(right);
				}

			}
			Object ret = shit(leftValue, op, rightValue);
			System.out.println(StringUtils.join("--- ", leftValue, op, rightValue, "=", ret));
			return ret;
		} else {
			// 函数或其它

			// 将表达式中的？号替换成参数
			List<Object> argList = new ArrayList<>();
			if (ps != null && ps.size() > 0) {
				int argIndex = 0;
				for (int i = 0; i < ps.size(); i++) {
					Object p = ps.get(i);
					if (StringUtils.trim((String) p).equals("?")) {
						// 替换？，合并参数
						argList.add(args[argIndex++]);
					} else {
						argList.add(p);
					}
				}
			}
			Object ret = execMethod(op, argList);
			System.out.println("--- " + op + "=" + ret);
			return ret;
		}
	}

	private Object execMethod(String method, List<Object> args) {
		if (method.equals("getTableField")) {
			String tableName = (String) args.get(0);
			String fieldName = (String) args.get(1);

			return getTableField(tableName, fieldName);
		} else {
			return 0;
		}
	}

	private Object getTableField(String tableName, String fieldName) {

		// System.out.println(StringUtils.join("getTableField>", tableName, ">",
		// fieldName));

		return 123;
	}

	public void toSQL(StringBuffer sb, List<Object> params) throws ServerException {
		if (t.equals(TYPE_EXP)) {
			// 二元表达式
			Object left = ps.get(0);
			Object right = ps.get(1);

			if (left instanceof EXP) {
				((EXP) left).toSQL(sb, params);
			} else {
				sb.append(left);
			}

			sb.append(sqlFixOP(op));
			if (right instanceof EXP) {
				EXP er = (EXP) right;
				boolean isEnd = EXP.isEndEXP(er);// 下一个节点是否最终节点
				if (!isEnd) {
					sb.append("(");
				}
				er.toSQL(sb, params);
				if (!isEnd) {
					sb.append(")");
				}
			} else {

				if (right instanceof String) {
					if (StringUtils.trim((String) right).equals("?")) {
						// 单个问号的字符不加单引号
						sb.append(right);
					} else {
						sb.append("'").append(right).append("'");
					}
				} else {
					sb.append(right);
				}
			}

			// 添加到参数列表
			if (args != null && args.length > 0) {
				for (Object a : args) {
					params.add(a);
				}
			}

		} else {
			// 函数或其它
			sb.append(op);
			if (ps != null && ps.size() > 0) {
				sb.append('(');
				for (int i = 0; i < ps.size(); i++) {
					Object p = ps.get(i);
					sb.append(p);
					if (i < ps.size() - 1) {
						sb.append(',');
					}
				}
				sb.append(')');
			}
			// 添加到参数列表
			if (args != null && args.length > 0) {
				for (Object a : args) {
					params.add(a);
				}
			}
		}
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
			boolean isLink = false;
			if (op.equals("&&") || op.equals("||")) {
				// 是连接符，代表最终节点，不加括号
				isLink = true;
			}

			if (isLink) {
				sb.append('(');
			}
			if (left instanceof JSONObject) {
				// 递归
				jsonEXP2VirtualTableSQL((JSONObject) left, jsonColumn, sb);
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
				jsonEXP2VirtualTableSQL((JSONObject) right, jsonColumn, sb);
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
			if (isLink) {
				sb.append(')');
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

}
