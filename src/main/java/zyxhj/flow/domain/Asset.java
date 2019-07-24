package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONObject;

public class Asset {

	public static final String TYPE_FORM = "form"; // 表单
	public static final String TYPE_FILE = "file"; // 文件
	public static final String TYPE_PART = "part"; // 附件
	
	public String module;
	public Long activityId;
	public Long id;

	public String sn;// 编号，在ProcessDefinition中不可重复
	public String title;// 标题
	public boolean necessary;// 是否必须

	/**
	 * 资产数据，JSON结构，{type:"form",content:"1234345"}</br>
	 * type为form表单时，存放表单编号</br>
	 * type为file文件时，存放文件地址</br>
	 * type为part附件时，存放附件编号</br>
	 */
	public JSONObject data;
}
