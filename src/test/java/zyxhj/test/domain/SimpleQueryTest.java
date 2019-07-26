package zyxhj.test.domain;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import zyxhj.utils.data.SimpleQuery;

public class SimpleQueryTest {

	private static SimpleQuery simpleQuery;

	/**
	 * TODO </br>
	 * 1，根据主键查询（单主键，多主键）（查指定对象，查列表）</br>
	 * 2，根据多字段条件查询（查指定对象，查列表）</br>
	 * 3，分别查询RDS和TS数据库</br>
	 * 4，六个关键接口都试用一下</br>
	 */

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {

			simpleQuery = new SimpleQuery("sq");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void textGetByIds() {
		JSONArray keys = new JSONArray();
		JSONArray values = new JSONArray();

		keys.add("moduleId");
		keys.add("id");

		values.add("567813484");
		values.add("400389367107358");

		try {
			Object ret = simpleQuery.getByKeys("zyxhj.flow.domain.ProcessDefinition", null, keys, values);

			System.out.println(JSON.toJSONString(ret));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void textGetListById() {
		try {
			Object ret = simpleQuery.getListByKey("zyxhj.flow.domain.ProcessDefinition", null, "moduleId", "567813484",
					10, 0);

			System.out.println(JSON.toJSONString(ret));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
