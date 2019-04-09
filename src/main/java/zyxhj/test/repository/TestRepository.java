package zyxhj.test.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.test.domain.Test;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class TestRepository extends RDSRepository<Test> {

	public TestRepository() {
		super(Test.class);
	}

	public List<Object[]> getListAllToObject(DruidPooledConnection conn) throws Exception {

		List<Object[]> li = this.getObjectsList(conn, "WHERE name = ? ", new Object[] { "123" }, 10, 0,new String[] {"name"});

		return li;
	}

	public Object[] getTestToObject(DruidPooledConnection conn) throws Exception {
		Object[] objects = this.getObjects(conn, "WHERE name = ? ", new Object[] { "123" },new String[] {"name"});
		
		return objects;
	}

	public List<Test> getListAll(DruidPooledConnection conn) throws Exception {
		
		List<Test> list = this.getList(conn, " WHERE status = ? ", new Object[] { "0" }, 10, 0, new String[]{"name","year","id"});
		for (Test test : list) {
			System.out.println(test);
		}
		return list;
	}
	public Test getTest(DruidPooledConnection conn) throws Exception {
			Test test = this.get(conn, "WHERE id = ?", new Object[] {"398005800667477"},new String[] {"id"});
			return test;
	}

	public int deleteById(DruidPooledConnection conn) throws Exception {
		int delete = this.delete(conn, "WHERE id = ? ", new Object[] {"11111"});
		return delete;
	}

	public JSONArray getJSONArray(DruidPooledConnection conn) throws Exception {
		StringBuffer sql = new StringBuffer("select * from tb_rds_test where status = ?");
		JSONArray sqlGetJSONArray = RDSRepository.sqlGetJSONArray(conn, sql.toString(), new Object[] {"0"}, 10, 0);
		return sqlGetJSONArray;
	}

	public JSONObject sqlJSONObject(DruidPooledConnection conn) throws Exception {
		StringBuffer sql = new StringBuffer("select * from tb_rds_test where status = ?");
		JSONObject s = RDSRepository.sqlGetJSONObject(conn, sql.toString(),  new Object[] {"0"});
		return s;
	}

	public List<Object[]> sqlGetListObject(DruidPooledConnection conn) throws Exception {
		StringBuffer sql = new StringBuffer("select * from tb_rds_test where status = ?");
		 List<Object[]> obj = RDSRepository.sqlGetObjectsList(conn, sql.toString(),  new Object[] {"0"}, 2, 0);
		
		return obj;
	}

}
