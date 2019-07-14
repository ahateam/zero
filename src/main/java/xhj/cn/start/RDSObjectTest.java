package xhj.cn.start;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.RDSObject;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.repository.RDSObjectRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;

//https://SizeStore.cn-hangzhou.ots.aliyuncs.com
//LTAIJ9mYIjuW54Cj
//89EMlXLsP13H8mWKIvdr4iM1OvdVxs

public class RDSObjectTest {

	public RDSObjectTest() {
	}

	private static DruidPooledConnection conn;

	private static RDSObjectRepository testRepository;

	static {

		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			testRepository = Singleton.ins(RDSObjectRepository.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		RDSObject rr = new RDSObject();
		rr.id = IDUtils.getSimpleId();
		rr.name = "testName";

		JSONObject jo = new JSONObject();
		jo.put("jotest", "sdfsdfsdf");
		jo.put("jotest2", "123123132");
		rr.jsonObject = jo;

		JSONArray ar = new JSONArray();
		ar.add("123");
		ar.add("456");
		rr.jsonArray = ar;

		TableSchema ts = new TableSchema();
		ts.id = IDUtils.getSimpleId();
		ts.alias = "斯柯达法哈萨克";

		// rr.object = ts;

		try {
			testRepository.insert(conn, rr);

			RDSObject xxx = testRepository.getByKey(conn, "id", rr.id);

			System.out.println(JSON.toJSONString(xxx, true));

		} catch (ServerException e) {
			e.printStackTrace();
		}

	}

}
