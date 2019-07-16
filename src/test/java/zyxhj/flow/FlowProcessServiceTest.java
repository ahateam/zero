package zyxhj.flow;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.service.FlowService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class FlowProcessServiceTest {

	private static DruidPooledConnection conn;

	private static FlowService flowService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			flowService = Singleton.ins(FlowService.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	private static final Long schemaId = 400159699711499L;

	private static final Long dataId = 400159711950692L;

	private static final Long queryId = 400159724862966L;

	@Test
	public void testCreateProcessDe() {
		Byte type = 1;

		JSONArray columns = new JSONArray();

		for (int i = 0; i < 5; i++) {
			TableSchema.Column tsc = new TableSchema.Column();
			tsc.name = "COL" + i;
			tsc.alias = "第" + i + "列";
			tsc.columnType = TableSchema.Column.COLUMN_TYPE_DATA;
			tsc.dataType = TableSchema.Column.DATA_TYPE_INTEGER;
			tsc.necessary = true;

			JSONObject jo = new JSONObject();
			jo.put(tsc.name, tsc);
			columns.add(jo);
		}

		TableSchema.Column tscTotal = new TableSchema.Column();
		tscTotal.name = "TOTAL1";
		tscTotal.alias = "合计1";
		tscTotal.columnType = TableSchema.Column.COLUMN_TYPE_COMPUTE;
		tscTotal.dataType = TableSchema.Column.DATA_TYPE_INTEGER;
		tscTotal.computeFormula = "{{COL1}} + {{COL2}} + {{COL3}} + {{COL4}} + {{COL5}}";

		JSONObject jo = new JSONObject();
		jo.put(tscTotal.name, tscTotal);
		columns.add(jo);

		try {
			flowService.createTableSchema(conn, "表的别名", type, columns);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
