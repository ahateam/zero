package zyxhj.flow.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.Form;
import zyxhj.flow.repository.FormRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class FormService extends Controller {
	
	private static Logger log = LoggerFactory.getLogger(FlowService.class);

	private DruidDataSource ds;
	
	private FormRepository formRepository;
	

	public FormService(String node) {
		super(node);

		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");

			formRepository = Singleton.ins(FormRepository.class);
		
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	@POSTAPI(//
			path = "createForm", //
			des = "创建表单", //
			ret = "Form实例" //
	)
	public Form createForm(//
		@P(t = "表单标题") String title,//
		@P(t = "表单数据" , r = false) JSONObject data//
			
	) throws Exception {
		Form form = new Form();
		form.id = IDUtils.getSimpleId();
		form.title = title;
		form.data = data;
		try (DruidPooledConnection conn = ds.getConnection()) {
			formRepository.insert(conn, form);
		}
		return form;
	}
	
	
	@POSTAPI(//
			path = "editForm", //
			des = "编辑表单", //
			ret = "Form实例" //
	)
	public int editForm(//
		@P(t = "表单编号") Long id,//
		@P(t = "表单数据" , r = false) JSONObject data//
	) throws Exception {
		Form form = new Form();
		form.data = data;
		try (DruidPooledConnection conn = ds.getConnection()) {
			return formRepository.update(conn,EXP.INS().key("id", id), form, true);
		}
	}
	
	
	@POSTAPI(//
			path = "getFormList", //
			des = "获取表单列表", //
			ret = "List<Form>" //
	)
	public List<Form> getFormList(//
		Integer count,
		Integer offset
			
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return formRepository.getList(conn, null, count, offset, "id", "title");
		}
	}
	
	
	
	@POSTAPI(//
			path = "getForm", //
			des = "获取表单详情", //
			ret = "<Form" //
	)
	public Form getForm(//
		@P(t = "表单编号") Long id//
			
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return formRepository.get(conn, EXP.INS().key("id", id));
		}
	}
	
	
	@POSTAPI(//
			path = "deleteForm", //
			des = "获取表单详情", //
			ret = "List<Form>" //
	)
	public int deleteForm(//
		@P(t = "表单编号") Long id//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return formRepository.delete(conn, EXP.INS().key("id", id));
		}
	}
	
	

}
