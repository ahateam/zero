package zyxhj.flow.domain;

/**
 * 资产需求描述
 */
public class ProcessAssetDesc {

	// public static final String TYPE_ANNEX = "annex";
	public static final String TYPE_TABLE = "table";
	public static final String TYPE_REPORT = "report";
	public static final String TYPE_FILE = "file";

	public Long id;
	public String type;// 资产类型
	public String name;// 名称
	public String remark;// 备注
	public Boolean necessary;// 是否必须
	public String template;// 模版文件地址
	public String ext;// 模版文件地址

}
