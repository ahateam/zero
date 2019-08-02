package zyxhj.flow.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;

import zyxhj.flow.domain.ReportSchema;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;

public class ReportService extends Controller {

	private static Logger log = LoggerFactory.getLogger(FlowService.class);
	
	private DruidDataSource ds;
	
	public ReportService(String node) {
		super(node);
		
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@POSTAPI(
			path = "getReportSchemaList",//
			des = "获取报表列表",//
			ret = "List<ReportSchema>"
			)
	public List<ReportSchema> getReportSchemaList(
			@P(t = "标签列表JSON列表，可以为空，即返回所有", r = false)JSONArray tags,//
			Integer count,//
			Integer offset
			) {
		
		return null;
	}
	

}
