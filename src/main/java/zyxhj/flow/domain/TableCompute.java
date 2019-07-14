package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;

/**
 * 规则表达式
 */
public class TableCompute {

	/**
	 * 运算符</br>
	 * +加 , -减 , *乘 , /除 , ^乘方</br>
	 * sum求和 , average平均 ,
	 */
	public String op;

	// 参数列表(列引用，常量)
	public JSONArray params;
}
