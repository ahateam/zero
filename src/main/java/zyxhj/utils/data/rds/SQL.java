package zyxhj.utils.data.rds;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

public class SQL {

	private static final String OR = "OR";
	private static final String AND = "AND";
	private static final String EMPTY = "";// 第一个节点

	/**
	 * 表达式连接条件(AND，OR等)
	 */
	protected ArrayList<String> nexuses;

	/**
	 * 表达式数组
	 */
	private ArrayList<SQLEx> exs;

	public SQL() {
		nexuses = new ArrayList<>();
		exs = new ArrayList<>();
	}

	/**
	 * 添加表达式
	 */
	public SQL addEx(SQLEx ex) {
		nexuses.add(EMPTY);
		exs.add(ex);
		return this;
	}

	/**
	 * 添加表达式
	 */
	public SQL addEx(String ex, Object... params) {
		return addEx(SQLEx.ex(ex, params));
	}

	/**
	 * 添加子表达式
	 */
	public SQL addSubEx(SQL subEx) {
		return addEx(SQLEx.subEx(subEx));
	}

	/**
	 * 验证参数并添加表达式</br>
	 * 如果参数为空，则本表达式不被添加
	 */
	public SQL addExValid(String ex, Object param) {
		if (null != param) {
			addEx(ex, param);
		}
		return this;
	}

	/**
	 * AND连接表达式
	 */
	public SQL AND(SQLEx ex) {
		if (exs.size() == 0) {
			// 是第一个节点，连接条件变为EMPTY
			nexuses.add(EMPTY);
		} else {
			nexuses.add(AND);
		}
		exs.add(ex);
		return this;
	}

	/**
	 * AND连接表达式
	 */
	public SQL AND(String ex, Object... params) {
		return AND(SQLEx.ex(ex, params));
	}

	/**
	 * AND连接表达式
	 */
	public SQL AND(SQL subEx) {
		return AND(SQLEx.subEx(subEx));
	}

	/**
	 * 验证参数并AND连接表达式</br>
	 * 如果参数为空，则本表达式不被添加
	 */
	public SQL ANDValid(String ex, Object param) {
		if (null != param) {
			AND(ex, param);
		}
		return this;
	}

	/**
	 * OR连接表达式
	 */
	public SQL OR(SQLEx ex) {
		if (exs.size() == 0) {
			// 是第一个节点，连接条件变为EMPTY
			nexuses.add(EMPTY);
		} else {
			nexuses.add(OR);
		}
		exs.add(ex);
		return this;
	}

	/**
	 * OR连接表达式
	 */
	public SQL OR(String ex, Object... params) {
		return OR(SQLEx.ex(ex, params));
	}

	/**
	 * OR连接表达式
	 */
	public SQL OR(SQL subEx) {
		return OR(SQLEx.subEx(subEx));
	}

	/**
	 * 验证参数并OR连接表达式</br>
	 * 如果参数为空，则本表达式不被添加
	 */
	public SQL ORValid(String ex, Object param) {
		if (null != param) {
			OR(ex, param);
		}
		return this;
	}

	/**
	 * 将sql填充到StringBuffer中
	 */
	public void fillSQL(StringBuffer sb) {
		for (int i = 0; i < exs.size(); i++) {
			String nexus = nexuses.get(i);
			SQLEx ex = exs.get(i);

			if (i > 0) {// 第一个条件不加空格
				sb.append(' ');
			}
			if (StringUtils.isBlank(nexus)) {// 没有连接条件
				ex.toSQL(sb);
			} else {// 有连接条件
				sb.append(nexus).append(' ');
				ex.toSQL(sb);
			}
		}
	}

	/**
	 * 将参数填充到ArrayList中
	 */
	public void fillParams(ArrayList<Object> params) {
		for (SQLEx ex : exs) {
			Object[] ps = ex.getParams();
			if (ps != null && ps.length > 0) {
				for (Object p : ps) {
					params.add(p);
				}
			}
		}
	}

	public String getSQL() {
		StringBuffer sb = new StringBuffer();
		this.fillSQL(sb);
		return sb.toString();
	}

	public Object[] getParams() {
		ArrayList<Object> objs = new ArrayList<>();
		fillParams(objs);
		if (objs == null || objs.size() <= 0) {
			return null;
		} else {
			return objs.toArray();
		}
	}

	public static void main(String[] args) {
		{
			System.out.println("-------------------");

			SQL sql = new SQL();

			sql.addEx("id=?", 1).AND("age>?", 10).OR("sex=?", "男");

			SQL subsql = new SQL();
			for (int i = 0; i < 3; i++) {
				if (i == 0) {
					subsql.addEx("JSON_CONTAINS(tags,'$.tags',sdfsdf)");
				} else {
					subsql.OR("JSON_CONTAINS(tags,'$.tags',sdfsdf)");
				}
			}
			sql.AND(subsql);

			StringBuffer sb = new StringBuffer();
			sql.fillSQL(sb);
			System.out.println(sb);
		}

		{
			System.out.println("-------------------");
			SQL sql = new SQL();

			sql.addEx("id=?", 1).AND(SQLEx.exIn("sub", 1, 2, 3, 4, 5));

			sql.AND(SQLEx.exANDKeys(new String[] { "id", "name", "status" }, new Object[] { 123, "王超", 3 }));

			sql.AND(SQLEx.exORKeys(new String[] { "id", "name", "status" }, new Object[] { 123, "王超", 3 }));

			StringBuffer sb = new StringBuffer();

			sql.addEx("GROUP BY name");

			sql.fillSQL(sb);

			System.out.println(sb);
		}

		{

			System.out.println("-------------------");
			String type = "type";
			String status = "status";
			String up_user_id = "uid";

			SQL sql = new SQL();
			sql.ANDValid("type=?", type).ANDValid("status=?", status);

			sql.ANDValid("up_user_id=?", up_user_id);

			sql.AND(SQLEx.exInOrdered("sub", 1, 2, 3, 4, 5));

			StringBuffer sb = new StringBuffer();
			ArrayList<Object> params = new ArrayList<>();

			sql.fillSQL(sb);
			sql.fillParams(params);

			System.out.println(sb);

			System.out.println(JSON.toJSONString(params));
		}
	}

}
