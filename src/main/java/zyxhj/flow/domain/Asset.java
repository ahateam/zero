package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONObject;

public class Asset {

	public static final String TYPE_FORM = "form";
	public static final String TYPE_FILE = "file";
	public static final String TYPE_PART = "part";

	/**
	 * 资产名称
	 */
	public String name;

	/**
	 * 是否必须
	 */
	public boolean necessary;

	/**
	 * 资产数据，JSON结构，{type:"form",content:"1234345"}</br>
	 * type为form表单时，存放表单编号</br>
	 * type为file文件时，存放文件地址</br>
	 * type为part附件时，存放附件编号</br>
	 */
	public JSONObject data;
}
