package zyxhj.utils.data.rds;

import java.util.List;

public class SQLEx {

	private String ex;
	private Object[] params;

	private SQL subEx;

	private SQLEx(String ex, Object... params) {
		this.ex = ex;
		this.params = params;
	}

	private SQLEx(SQL subEx) {
		this.subEx = subEx;
	}
	
	/**
	 * 普通表达式</br>
	 * 如：id=?
	 */
	public static SQLEx ex(String ex, Object... params) {
		return new SQLEx(ex, params);
	}

	/**
	 * 嵌套子表达式
	 */
	public static SQLEx subEx(SQL subEx) {
		return new SQLEx(subEx);
	}
	
	/**
	 * In表达式</br>
	 * 如：id IN(?,?,?)
	 */
	public static SQLEx exIn(String columnName, Object... params) {
		StringBuffer sb = new StringBuffer(columnName);
		sb.append(" IN(");
		int len = params.length;
		for (int i = 0; i < len; i++) {
			if (i < len - 1) {
				sb.append("?,");
			} else {
				sb.append('?');
			}
		}
		sb.append(')');
		return new SQLEx(sb.toString(), params);
	}
	
	/**
	 * In表达式，有序的</br>
	 * 如：id IN(?,?,?) ORDER BY FIND_IN_SET(id,p1,p2,p3)
	 */
	public static SQLEx exInOrdered(String columnName, Object... params) {
		StringBuffer sb = new StringBuffer(columnName);
		sb.append(" IN(");
		int len = params.length;
		for (int i = 0; i < len; i++) {
			if (i < len - 1) {
				sb.append("?,");
			} else {
				sb.append('?');
			}
		}
		sb.append(") ORDER BY FIND_IN_SET(").append(columnName).append(',');

		for (int i = 0; i < len; i++) {
			Object o = params[i];
			if (i < len - 1) {
				sb.append(o).append(',');
			} else {
				sb.append(o);
			}
		}
		sb.append(')');
		return new SQLEx(sb.toString(), params);
	}

	/**
	 * “与”键值表达式</br>
	 * 如：(id=? AND name=? AND status=?)
	 */
	public static SQLEx exANDKeys(String[] keys, Object[] params) {
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		for (int i = 0; i < keys.length; i++) {
			sb.append(keys[i]).append("=?");
			if (i < keys.length - 1) {
				sb.append(" AND ");
			}
		}
		sb.append(')');
		return new SQLEx(sb.toString(), params);
	}

	/**
	 * “或”键值表达式</br>
	 * 如：(id=? OR name=? OR status=?)
	 */
	public static SQLEx exORKeys(String[] keys, Object[] params) {
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		for (int i = 0; i < keys.length; i++) {
			sb.append(keys[i]).append("=?");
			if (i < keys.length - 1) {
				sb.append(" OR ");
			}
		}
		sb.append(')');
		return new SQLEx(sb.toString(), params);
	}

	public void toSQL(StringBuffer sb) {
		if (null == subEx) {
			// 是正常表达式
			sb.append(ex);
		} else {
			// 递归包含子表达式
			sb.append("( ");// 前后加括号
			subEx.fillSQL(sb);
			sb.append(" )");// 前后加括号
		}
	}

	public Object[] getParams() {
		return params;
	}
}
