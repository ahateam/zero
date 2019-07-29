package zyxhj.flow.domain;

/**
 * 资产需求描述
 */
public class ProcessAssetDesc {
	
	public static final String TYPE_ANNEX = "annex";
	public static final String TYPE_FORM = "form";
	public static final String TYPE_REPORT = "report";
	public static final String TYPE_FILE = "file";

	public String name;// 名称
	public String remark;// 备注
	public String type;// 资产类型
	public Boolean necessary;// 是否必须
}
