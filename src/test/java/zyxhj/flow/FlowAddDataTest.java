package zyxhj.flow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.service.UserRoleService;
import zyxhj.core.service.UserService;
import zyxhj.flow.domain.Annex;
import zyxhj.flow.domain.ProcessAssetDesc;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.domain.TableSchema.Column;
import zyxhj.flow.service.AnnexService;
import zyxhj.flow.service.DepartmentService;
import zyxhj.flow.service.FlowService;
import zyxhj.flow.service.TableService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class FlowAddDataTest {

	private static DruidPooledConnection conn;

	private static UserService userService;

	private static UserRoleService roleService;

	private static FlowService flowService;

	private static AnnexService annexService;

	private static TableService tableService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			userService = Singleton.ins(UserService.class);

			roleService = Singleton.ins(UserRoleService.class);

			flowService = Singleton.ins(FlowService.class, "flow");

			annexService = Singleton.ins(AnnexService.class, "annex");

			tableService = Singleton.ins(TableService.class, "table");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	@Test
	public void testAddUserData() {

		try {
			// userService.registByNameAndPwd(conn, "彭芳苓", "621024197908142703");
			// userService.registByNameAndPwd(conn, "俞秀丽", "621024198208137588");
			// userService.registByNameAndPwd(conn, "杨淑兰", "621024199002277301");
			// userService.registByNameAndPwd(conn, "曹芳洁", "621024197802259700");
			// userService.registByNameAndPwd(conn, "沈秀媚", "62102419860713830X");
			// userService.registByNameAndPwd(conn, "唐文丽", "621024197803226249");
			// userService.registByNameAndPwd(conn, "史雅娴", "621024197405245786");
			// userService.registByNameAndPwd(conn, "方清馨", "621024197107267303");
			// userService.registByNameAndPwd(conn, "郎依白", "621024197103176105");
			// userService.registByNameAndPwd(conn, "岑明煦", "370829198005211015");
			// userService.registByNameAndPwd(conn, "贺天佑", "500107198004128410");
			// userService.registByNameAndPwd(conn, "史俊伟", "500107198705167973");
			// userService.registByNameAndPwd(conn, "薛豪健", "500107198104168954");
			// userService.registByNameAndPwd(conn, "薛文敏", "500107198508223921");
			// userService.registByNameAndPwd(conn, "雷丽文", "500107198205246560");
			// userService.registByNameAndPwd(conn, "苏葛菲", "500107198303143346");
			// userService.registByNameAndPwd(conn, "谢晗玥", "500107197602108343");

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Test
	public void testUpdateUserData() throws Exception {

		// userService.editUserIdNumber(conn, 123456L, 400521485946319L,
		// "621024197908142703");
		// userService.editUserIdNumber(conn, 123456L, 400521497849952L,
		// "621024198208137588");
		// userService.editUserIdNumber(conn, 123456L, 400521497884513L,
		// "621024199002277301");
		// userService.editUserIdNumber(conn, 123456L, 400521497918306L,
		// "621024197802259700");
		// userService.editUserIdNumber(conn, 123456L, 400521497952099L,
		// "62102419860713830X");
		// userService.editUserIdNumber(conn, 123456L, 400521497986916L,
		// "621024197803226249");
		// userService.editUserIdNumber(conn, 123456L, 400521498021221L,
		// "621024197405245786");
		// userService.editUserIdNumber(conn, 123456L, 400521498058086L,
		// "621024197107267303");
		// userService.editUserIdNumber(conn, 123456L, 400521498095975L,
		// "621024197103176105");
		// userService.editUserIdNumber(conn, 123456L, 400521498130024L,
		// "370829198005211015");
		// userService.editUserIdNumber(conn, 123456L, 400521498164073L,
		// "500107198004128410");
		// userService.editUserIdNumber(conn, 123456L, 400521498198122L,
		// "500107198705167973");
		// userService.editUserIdNumber(conn, 123456L, 400521498232683L,
		// "500107198104168954");
		// userService.editUserIdNumber(conn, 123456L, 400521498268012L,
		// "500107198508223921");
		// userService.editUserIdNumber(conn, 123456L, 400521498303853L,
		// "500107198205246560");
		// userService.editUserIdNumber(conn, 123456L, 400521498341742L,
		// "500107198303143346");
		// userService.editUserIdNumber(conn, 123456L, 400521498377583L,
		// "500107197602108343");

	}

	@Test
	public void testAddRoleData() throws Exception {

		// roleService.createUserRole(conn, "职员", "普通职员");
		// roleService.createUserRole(conn, "科长", "部门科长");
		// roleService.createUserRole(conn, "副科长", "副科长");
		// roleService.createUserRole(conn, "主任", "主任");
		// roleService.createUserRole(conn, "副主任", "副主任");
		// roleService.createUserRole(conn, "局长", "局长");
		// roleService.createUserRole(conn, "副局长", "副局长");
		// roleService.createUserRole(conn, "部长", "部长");
		// roleService.createUserRole(conn, "副部长", "副部长");

	}

	@Test
	public void testAddDepartmentData() {

		// flowService.createDepartment(conn, "财政部", "国家财政部");
		// flowService.createDepartment(conn, "财政厅", "省财政厅");
		// flowService.createDepartment(conn, "财政局", "市财政局");
		// flowService.createDepartment(conn, "公安部", "国家公安部");
		// flowService.createDepartment(conn, "公安厅", "省公安厅");
		// flowService.createDepartment(conn, "公安局", "市公安局");

	}

	@Test
	public void testAddAnnexData() throws Exception {
		//
		// //市财政局附件
		// JSONObject data = new JSONObject();
		// data.put("url", "待输入");
		//
		// JSONObject tags = new JSONObject();
		// tags.put("department", "财政");
		//
		// annexService.createAnnex(400522524166839L, "请假申请表", Annex.TYPE_FILE, data,
		// tags);
		// annexService.createAnnex(400522524166839L, "出差申请表", Annex.TYPE_FILE, data,
		// tags);
		// annexService.createAnnex(400522524166839L, "外勤申请表", Annex.TYPE_FILE, data,
		// tags);
		// annexService.createAnnex(400522524166839L, "预算申请表", Annex.TYPE_FILE, data,
		// tags);
		//
		// JSONObject data = new JSONObject();
		// data.put("url", "待输入");
		//
		// JSONObject tags = new JSONObject();
		// tags.put("department", "公安");
		//
		// annexService.createAnnex(400522524221882L, "请假申请表", Annex.TYPE_FILE, data,
		// tags);
		// annexService.createAnnex(400522524221882L, "出差申请表", Annex.TYPE_FILE, data,
		// tags);
		// annexService.createAnnex(400522524221882L, "外勤申请表", Annex.TYPE_FILE, data,
		// tags);
		// annexService.createAnnex(400522524221882L, "预算申请表", Annex.TYPE_FILE, data,
		// tags);

	}

	@Test
	public void testAddTableSchemaData() throws Exception {

		JSONArray columns = new JSONArray();
		Column c = new Column();
		c.name = "applicant";
		c.alias = "申请人";
		c.columnType = Column.COLUMN_TYPE_DATA;
		c.dataType = Column.DATA_TYPE_STRING;
		c.necessary = true;

		Column c1 = new Column();
		c1.name = "reviewer";
		c1.alias = "审核人";
		c1.columnType = Column.COLUMN_TYPE_DATA;
		c1.dataType = Column.DATA_TYPE_STRING;
		c1.necessary = true;

		Column c2 = new Column();
		c2.name = "applicantTime";
		c2.alias = "申请时间";
		c2.columnType = Column.COLUMN_TYPE_DATA;
		c2.dataType = Column.DATA_TYPE_TIME;
		c2.necessary = true;

		columns.add(c);
		columns.add(c1);
		columns.add(c2);

		tableService.createTableSchema("外勤申请表", TableSchema.TYPE.VIRTUAL_QUERY_TABLE.v(), columns, null);
	}

	@Test
	public void testAddTableData() throws Exception {

		JSONObject data = new JSONObject();
		data.put("applicant", "谢晗玥");
		data.put("reviewer", "苏葛菲");
		data.put("applicant_time", new SimpleDateFormat("yyyy-MM-dd").parse("2019-07-20"));
		tableService.insertTableData(400526607053613L, data);

	}

	@Test
	public void testJSON() {

		// List<ProcessAssetDesc> a = new ArrayList<ProcessAssetDesc>();
		//
		// ProcessAssetDesc p = new ProcessAssetDesc();
		// p.id = 400526607053613L;
		// p.name = "file";
		// p.type = ProcessAssetDesc.TYPE_FILE;
		// p.ownerId = 400526607053613L;
		// p.necessary = true;
		// p.template = "url:123456";
		// p.remark = "File";
		// p.ext = "url:12345646456";
		//
		// ProcessAssetDesc p1 = new ProcessAssetDesc();
		// p1.id = 400526607053613L;
		// p1.name = "table";
		// p1.type = ProcessAssetDesc.TYPE_TABLE;
		// p1.ownerId = 400526607053613L;
		// p1.necessary = true;
		// p1.template = "url:123456";
		// p1.remark = "table";
		// p1.ext = "url:12345646456";
		//
		// ProcessAssetDesc p2 = new ProcessAssetDesc();
		// p2.id = 400526607053613L;
		// p2.name = "report";
		// p2.type = ProcessAssetDesc.TYPE_REPORT;
		// p2.ownerId = 400526607053613L;
		// p2.necessary = true;
		// p2.template = "url:123456";
		// p2.remark = "report";
		// p2.ext = "url:12345646456";
		//
		// a.add(p1);
		// a.add(p2);
		// a.add(p);
		//
		// String js = JSON.toJSONString(a);
		//
		// System.out.println(js);

		String jss = "{\"table\":[{\"ext\":\"url:12345646456\",\"id\":400526607053613,\"name\":\"table\",\"necessary\":true,\"ownerId\":400526607053613,\"remark\":\"table\",\"template\":\"url:123456\",\"type\":\"table\"},{\"ext\":\"url:12345646456\",\"id\":400526607053613,\"name\":\"table\",\"necessary\":true,\"ownerId\":400526607053613,\"remark\":\"table\",\"template\":\"url:123456\",\"type\":\"table\"}],\"report\":[{\"ext\":\"url:12345646456\",\"id\":400526607053613,\"name\":\"report\",\"necessary\":true,\"ownerId\":400526607053613,\"remark\":\"report\",\"template\":\"url:123456\",\"type\":\"report\"},{\"ext\":\"url:12345646456\",\"id\":400526607053613,\"name\":\"report\",\"necessary\":true,\"ownerId\":400526607053613,\"remark\":\"report\",\"template\":\"url:123456\",\"type\":\"report\"}],\"file\":[{\"ext\":\"url:12345646456\",\"id\":400526607053613,\"name\":\"file\",\"necessary\":true,\"ownerId\":400526607053613,\"remark\":\"File\",\"template\":\"url:123456\",\"type\":\"file\"},{\"ext\":\"url:12345646456\",\"id\":400526607053613,\"name\":\"file\",\"necessary\":true,\"ownerId\":400526607053613,\"remark\":\"File\",\"template\":\"url:123456\",\"type\":\"file\"}]}";

		JSONObject obj = JSONObject.parseObject(jss);
		List plist = obj.getObject("table", List.class);

		for (int i = 0; i < plist.size(); i++) {
			ProcessAssetDesc p = (ProcessAssetDesc) plist.get(i);
			System.out.println(p.id);
		}

	}

}
