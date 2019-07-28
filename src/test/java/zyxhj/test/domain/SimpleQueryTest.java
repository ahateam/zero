package zyxhj.test.domain;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import zyxhj.core.domain.Module;
import zyxhj.flow.domain.ProcessDefinition;
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

//	@Test
	public void textGetByIds() {
		JSONArray keys = new JSONArray();
		JSONArray values = new JSONArray();

		keys.add("moduleId");
		keys.add("id");

		values.add("567813484");
		values.add("400416892270325");

		try {
			Object ret = simpleQuery.getByKeys("zyxhj.flow.domain.ProcessDefinition", keys, values, null);

			System.out.println(JSON.toJSONString(ret));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void textGetListById() {
		try {
			Object ret = simpleQuery.getListByKey("zyxhj.flow.domain.ProcessDefinition", "moduleId", "567813484", 10, 0, null);

			System.out.println(JSON.toJSONString(ret));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 1，根据主键查询（单主键，多主键）（查指定对象，查列表）</br>
	 * @throws Exception 
	 */
	@Test
	public void getModuleById() throws Exception {
		
		//通过moduleId查询module表数据
		String moduleId = "567813484";
		String pdId = "400416892270325";
		Integer count = 10;
		Integer offset = 0;
		
		Object obj = simpleQuery.getByKey("zyxhj.flow.domain.Module", "id", moduleId, null);
		System.out.println(JSON.toJSONString(obj));
		
		System.out.println("______________________________");
		
		//通过流程定义编号查询流程定义数据
		obj = simpleQuery.getByKey("zyxhj.flow.domain.ProcessDefinition", "id", pdId, null);
		System.out.println("______________________________");
		System.out.println(JSON.toJSONString(obj));
		System.out.println("______________________________");
		
		//通过moduleId查询流程定义数据
		Object obj1 = simpleQuery.getListByKey("zyxhj.flow.domain.ProcessDefinition", "module_id", "567813484", count, offset, null);
		System.out.println(obj1);
	}



}

