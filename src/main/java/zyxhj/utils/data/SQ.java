package zyxhj.utils.data;

import java.util.List;

/**
 * SimpleQuery</br>
 * 根据基本SQL语法改编的对象查询工具</br>
 * 用于解决常见的基本查询功能，如根据id获取对象，多条件查询对象等
 */
public class SQ {

	public static final String RET_TYPE_OBJECT = "object";
	public static final String RET_TYPE_JSON = "json";

	/**
	 * 实体类名
	 */
	public String domain;

	/**
	 * 查询语句</br>
	 * {{field1}} > 3 AND {{field2}} < 0
	 */
	public String where;

	/**
	 * 要返回的列名列表
	 */
	public List<String> selections;

	/**
	 * 返回类型
	 */
	public String retType;

}
