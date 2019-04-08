package zyxhj.test;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.test.domain.Test;
import zyxhj.test.repository.TestRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;

public class Test1 {

	private static DruidPooledConnection conn;

	static {
		DataSourceUtils.initDataSourceConfig();
		try {
			conn = (DruidPooledConnection) DataSourceUtils.getDataSource("rdsDefault").openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static  TestRepository testRepository = new TestRepository();

	
	
	
	public static void main(String[] args) throws Exception{
		// insert();  //通过
	//	int id=123;
		//delete(id); //通过
		
		// 获取字段对象数组列表  返回查询的对象数组列表，如果查询不到，则返回空数组  
	//	getObjectsList();   // 通过
		
		//，获取一行字段对象数组
	 // 	getObjects();  //通过
		
		//获取对象集合  
	//	 getList();   //问题  selections
		
		//获取单个对象
	//	get();  //问题  selections
		
		//根据条件删除
		//delete();   //通过
		
		//原生SQL方法，获取JSONArray
		//sqlJSONArray();  //通过
		
	//	原生SQL方法，获取JSONOBJECT 只返回一条数据
	//	JSONObject();   //通过
		
		
		//原生SQL方法，根据查询获取某个表的某些字段值列表
		List<Object[]> obj = testRepository.sqlGetListObject(conn);
		System.out.println(obj.size());
		
		
		
	}



	private static void JSONObject() throws Exception {
		JSONObject js = testRepository.sqlJSONObject(conn);
		System.out.println(js);
	}



	private static void sqlJSONArray() throws Exception {
		JSONArray js = testRepository.getJSONArray(conn);
		for (Object object : js) {
			System.out.println(object);
		}
		System.out.println(js);
	}



	private static void delete() throws Exception {
		int de = testRepository.deleteById(conn);
		System.out.println(de);
	}



	private static void get() throws Exception {
		Test test = testRepository.getTest(conn);
		System.out.println(test.toString());
	}



	private static void getList() throws Exception {
		List<Test> listAll = testRepository.getListAll(conn);
		for (Test test : listAll) {
			// System.out.println(test.name);
		}
	}



	private static void getObjects() throws Exception {
		Object[] ob = testRepository.getTestToObject(conn);
		for (Object obj : ob) {
			System.out.println(obj.toString());
		}
	}



	private static void getObjectsList() throws Exception {
		List<Object[]> li = testRepository.getListAllToObject(conn);
		System.out.println(li.size());
		for (int i = 0 ; i<li.size() ; i++) {
			String b = li.get(i).toString();
			System.out.println(b);
			System.out.println(li);
			String ob = li.get(i).toString();
			System.out.println(ob);
		}
	}



		public static void delete(int id) throws Exception{
			testRepository.deleteByKey(conn, "id", id);
	}

		
		public static  void insert() throws Exception  {
			Test te = new Test();
			te.id = IDUtils.getSimpleId();
			te.name = "123";
			te.year = "234";
			te.status = 0;
			testRepository.insert(conn, te);
		}
		

	
}
