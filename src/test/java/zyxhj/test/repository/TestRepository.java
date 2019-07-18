package zyxhj.test.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.test.domain.TestDomain;
import zyxhj.utils.data.rds.RDSRepository;

public class TestRepository extends RDSRepository<TestDomain> {

	public TestRepository() {
		super(TestDomain.class);
	}

	public List<Object[]> getListAllToObject(DruidPooledConnection conn) throws Exception {
		return getObjectsList(conn, "WHERE name = ? ", new Object[] { "123" }, 10, 0, "name");
	}

	public Object[] getTestToObject(DruidPooledConnection conn) throws Exception {
		return getObjects(conn, "WHERE name = ? ", new Object[] { "123" }, "name");
	}

	public List<TestDomain> getListAll(DruidPooledConnection conn) throws Exception {
		return getList(conn, " WHERE status = ? ", new Object[] { "0" }, 10, 0, "name", "year", "id");
	}

	public TestDomain getTest(DruidPooledConnection conn) throws Exception {
		return get(conn, "WHERE id = ?", new Object[] { "398005800667477" }, "id");
	}

	public int deleteById(DruidPooledConnection conn) throws Exception {
		return delete(conn, "WHERE id = ? ", new Object[] { "11111" });
	}

	public JSONArray getJSONArray(DruidPooledConnection conn) throws Exception {
		return sqlGetJSONArray(conn, "select * from tb_rds_test where status = ?", new Object[] { "0" }, 10, 0);
	}

	public JSONObject sqlJSONObject(DruidPooledConnection conn) throws Exception {
		return sqlGetJSONObject(conn, "SELECT * FROM tb_rds_test WHERE status = ?", new Object[] { "0" });
	}

	public List<Object[]> sqlGetListObject(DruidPooledConnection conn) throws Exception {
		return sqlGetObjectsList(conn, "SELECT * FROM tb_rds_test WHERE status = ?", new Object[] { "0" }, 2, 0);
	}

	public Object[] sqlGetObjects(DruidPooledConnection conn) throws Exception {
		return sqlGetObjects(conn, "SELECT COUNT(*) FROM tb_rds_test WHERE status = ?", new Object[] { "0" });
	}

}
