package zyxhj.utils.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

import zyxhj.utils.api.ServerException;

public class EXPTest {

	@Test
	public void testParams() {

		// 参数测试，注意顺序

		EXP e1;
		try {
			e1 = EXP.INS().exp("t1", "=", "?", 1).and("t2", "=", "?", 2).and("t5", "LIKE", "%abc%");

			EXP e2 = EXP.INS().exp("tt1", "<>", "?", "a").or("tt2", "=", "?", "b");

			EXP e3 = EXP.INS().exp("t3", "=", "?", 3).and("t4", "=", "?", 4);

			EXP exp = EXP.INS().exp(e1, "AND", e3).and(e2);

			StringBuffer sb = new StringBuffer();
			ArrayList<Object> params = new ArrayList<>();
			exp.toSQL(sb, params);

			String str = sb.toString();
			String pstr = JSON.toJSONString(params);
			System.out.println("===" + str);
			System.out.println(">>>" + pstr);

			// 断言是否正确
			Assert.assertEquals(pstr, "[1,2,3,4,\"a\",\"b\"]");

		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testExact() {
		// 参数严格性测试
		// true时，参数为空照传
		// false时，参数为空则去除表达式

		Object obj = null;

		try {
			EXP e1 = EXP.INS().exp("t1", "=", "?", 1).and("t2", "=", "?", obj).or("t3", "=", "?", obj);
		} catch (ServerException e) {
			// e.printStackTrace();
			Assert.assertTrue(true);
		}

		try {
			EXP e2 = EXP.INS(false).exp("t1", "=", "?", obj).and("t2", "=", "?", "t2").or("t3", "=", "?", obj);

			StringBuffer sb = new StringBuffer();
			ArrayList<Object> params = new ArrayList<>();

			params = new ArrayList<>();
			e2.toSQL(sb, params);
			System.out.println(">>>" + sb.toString());

			Assert.assertEquals(sb.toString(), "t2 = ?");
		} catch (ServerException e) {
			Assert.assertTrue(true);
		}

	}

	@Test
	public void testLike() {

		try {
			EXP e = EXP.INS().exp("t1", "=", "?", 1).and(EXP.LIKE("name", "namestr"));

			StringBuffer sb = new StringBuffer();
			ArrayList params = new ArrayList<>();

			e.toSQL(sb, params);
			System.out.println(">>>" + sb.toString());
			Assert.assertEquals(sb.toString(), "t1 = ? AND name LIKE '%namestr%'");
		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void testIn() {
		try {
			EXP e = EXP.INS().exp("t1", "=", "?", 1).and(EXP.IN("name", 123, 234, "sdf", 3534, 3453, 334));

			StringBuffer sb = new StringBuffer();
			ArrayList params = new ArrayList<>();

			e.toSQL(sb, params);
			System.out.println(">>>" + sb.toString());
			Assert.assertEquals(sb.toString(), "t1 = ? AND name IN(?,?,?,?,?,?)");
		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void testInOrdered() {
		try {
			EXP e = EXP.INS().exp("t1", "=", "?", 1).and(EXP.IN_ORDERED("name", 123, 234, "sdf", 3534, 3453, 334));

			StringBuffer sb = new StringBuffer();
			ArrayList params = new ArrayList<>();

			e.toSQL(sb, params);
			System.out.println(">>>" + sb.toString());
			Assert.assertEquals(sb.toString(),
					"t1 = ? AND name IN(?,?,?,?,?,?) ORDER BY FIND_IN_SET(name,'123,234,sdf,3534,3453,334')");
		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void testRemove() {

		try {
			EXP tt = EXP.INS().exp("this is shit", null).or("this is fuck", null);

			EXP exp = EXP.INS().exp("t1", "=", "?", 1).and("t2", "=", "?", 2).and(tt);

			EXP exp2 = EXP.INS().exp("t1", "=", "?", 1).and("t2", "=", "?", 2).and(tt);

			EXP expMax = EXP.INS().exp(exp).and(exp2);

			StringBuffer sb = new StringBuffer();
			ArrayList<Object> params = new ArrayList<>();
			expMax.toSQL(sb, params);

			System.out.println(JSON.toJSONString(expMax, true));
			String str = sb.toString();
			String pstr = JSON.toJSONString(params);
			System.out.println("===" + str);
			System.out.println(">>>" + pstr);

			Assert.assertEquals(str,
					"t1 = ? AND t2 = ? AND (this is shit OR this is fuck) AND (t1 = ? AND t2 = ? AND (this is shit OR this is fuck))");
		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void testANDKey() {
		try {
			{
				EXP e = EXP.INS().key("id", 123).andKey("name", "小四");

				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str, "id = ? AND name = ?");
			}

			{
				EXP e = EXP.INS(false).key("id", null).andKey("name", "小四").andKey("sex", null);
				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str, "name = ?");
			}

		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void testORKey() {
		try {
			{
				EXP e = EXP.INS().key("id", 123).orKey("name", "小四");

				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str, "id = ? OR name = ?");
			}

			{
				EXP e = EXP.INS(false).key("id", null).orKey("name", "小四").orKey("sex", null);
				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str, "name = ?");
			}

		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void testJsonArrayAppend() {
		try {
			{
				// 字符 可重复
				EXP e = EXP.JSON_ARRAY_APPEND("arrays", "tag4", true);

				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str,
						"arrays = IF((ISNULL(arrays) || LENGTH(trim(arrays))<1), JSON_ARRAY('tag4'), JSON_ARRAY_APPEND(arrays,'$','tag4'))");
			}

			{
				// 字符 不可重复
				EXP e = EXP.JSON_ARRAY_APPEND("arrays", "tag4", false);

				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str,
						"arrays = IF((ISNULL(arrays) || LENGTH(trim(arrays))<1), JSON_ARRAY('tag4'), IF(JSON_CONTAINS(arrays,'\"tag4\"','$'),arrays,JSON_ARRAY_APPEND(arrays,'$','tag4')))");
			}

			{
				// 数字 可重复
				EXP e = EXP.JSON_ARRAY_APPEND("arrays", 123, true);

				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str,
						"arrays = IF((ISNULL(arrays) || LENGTH(trim(arrays))<1), JSON_ARRAY(123), JSON_ARRAY_APPEND(arrays,'$',123))");
			}

			{
				// 数字 不可重复
				EXP e = EXP.JSON_ARRAY_APPEND("arrays", 123, false);

				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str,
						"arrays = IF((ISNULL(arrays) || LENGTH(trim(arrays))<1), JSON_ARRAY(123), IF(JSON_CONTAINS(arrays,'123','$'),arrays,JSON_ARRAY_APPEND(arrays,'$',123)))");
			}
		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void testJsonArrayAppendOnKey() {
		try {
			{
				// 字符 可重复
				EXP e = EXP.JSON_ARRAY_APPEND_ONKEY("tags", "type", "tag4", true);

				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str,
						"tags = IF((ISNULL(tags) || LENGTH(trim(tags))<1),JSON_OBJECT('type',JSON_ARRAY('tag4')),IF(JSON_CONTAINS_PATH(tags,'one','$.type'),JSON_ARRAY_APPEND(tags, '$.type' ,'tag4'),JSON_SET(tags,'$.type',JSON_ARRAY('tag4'))))");
			}

			{
				// 字符 不可重复
				EXP e = EXP.JSON_ARRAY_APPEND_ONKEY("tags", "type", "tag4", false);

				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str,
						"tags = IF((ISNULL(tags) || LENGTH(trim(tags))<1),JSON_OBJECT('type',JSON_ARRAY('tag4')),IF(JSON_CONTAINS_PATH(tags,'one','$.type'),IF(JSON_CONTAINS(tags,'\"tag4\"','$.type'),tags,JSON_ARRAY_APPEND(tags, '$.type' ,'tag4')),JSON_SET(tags,'$.type',JSON_ARRAY('tag4'))))");
			}

			{
				// 数字 可重复
				EXP e = EXP.JSON_ARRAY_APPEND_ONKEY("tags", "type", 123, true);

				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str,
						"tags = IF((ISNULL(tags) || LENGTH(trim(tags))<1),JSON_OBJECT('type',JSON_ARRAY(123)),IF(JSON_CONTAINS_PATH(tags,'one','$.type'),JSON_ARRAY_APPEND(tags, '$.type' ,123),JSON_SET(tags,'$.type',JSON_ARRAY(123))))");
			}

			{
				// 数字 不可重复
				EXP e = EXP.JSON_ARRAY_APPEND_ONKEY("tags", "type", 123, false);

				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str,
						"tags = IF((ISNULL(tags) || LENGTH(trim(tags))<1),JSON_OBJECT('type',JSON_ARRAY(123)),IF(JSON_CONTAINS_PATH(tags,'one','$.type'),IF(JSON_CONTAINS(tags,'123','$.type'),tags,JSON_ARRAY_APPEND(tags, '$.type' ,123)),JSON_SET(tags,'$.type',JSON_ARRAY(123))))");
			}

		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void testJsonArrayRemove() {
		try {
			EXP e = EXP.JSON_ARRAY_REMOVE("arrays", "$", 1);

			StringBuffer sb = new StringBuffer();
			ArrayList<Object> params = new ArrayList<>();
			e.toSQL(sb, params);

			String str = sb.toString();
			String pstr = JSON.toJSONString(params);
			System.out.println("===" + str);
			System.out.println(">>>" + pstr);

			Assert.assertEquals(str, "arrays = JSON_REMOVE(arrays,'$[1]')");

		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void testTest() {
		try {
			EXP e = EXP.INS().key("id", 123).and(EXP.INS().key("name", "xs").or("age", "<", 18));

			StringBuffer sb = new StringBuffer();
			ArrayList<Object> params = new ArrayList<>();
			e.toSQL(sb, params);

			String str = sb.toString();
			String pstr = JSON.toJSONString(params);
			System.out.println("===" + str);
			System.out.println(">>>" + pstr);

			Assert.assertEquals(str, "id = ? AND (name = ? OR age < 18)");

		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void tsa() {
		try {
			EXP e1 = EXP.INS().exp("t1", "=", "?", 1).and("t2", "=", "?", 2).and("t5", ">", 456456);
			StringBuffer sb = new StringBuffer();
			ArrayList<Object> args = new ArrayList<Object>();
			e1.toSQL(sb, args);
			System.out.println(sb.toString());
			System.out.println(args.get(0));
			System.out.println(args.get(1));
			// System.out.println(args.get(2));
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testJsonContains() {
		try {
			{
				EXP e = EXP.JSON_CONTAINS("tags", "$.group1", "temp");
				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str, "JSON_CONTAINS(tags, '\"temp\"','$.group1')");
			}

			{
				EXP e = EXP.JSON_CONTAINS("tags", "$.group1", 234);
				StringBuffer sb = new StringBuffer();
				ArrayList<Object> params = new ArrayList<>();
				e.toSQL(sb, params);

				String str = sb.toString();
				String pstr = JSON.toJSONString(params);
				System.out.println("===" + str);
				System.out.println(">>>" + pstr);

				Assert.assertEquals(str, "JSON_CONTAINS(tags, '234','$.group1')");
			}

		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testScript() throws ServerException {

		{
			EXP e = EXP.INS().exp(EXP.INS().exp("getTableField", Arrays.asList("tableId", "fieldName")), ">", "?", 3)
					.and(EXP.INS().exp("getTableField", Arrays.asList("tableId", "fieldName")), "LIKE", "?", "4");
			StringBuffer sb = new StringBuffer();
			e.toEXP(sb);

			Object ret = e.compute();
			System.out.println(ret);

			String str = sb.toString();
			System.out.println("===" + str);

			Assert.assertEquals(str,
					"getTableField(tableId,fieldName) > 3 && (getTableField(tableId,fieldName) LIKE 4)");
			Assert.assertEquals(0, ret);
		}

		{
			EXP e = EXP.INS().exp(EXP.INS().exp("getTableField", Arrays.asList("tableId", "fieldName")), ">", "?", 3)
					.and(EXP.INS().exp("getTableField", Arrays.asList("tableId", "fieldName")), "LIKE", "?", "23");
			StringBuffer sb = new StringBuffer();
			e.toEXP(sb);

			Object ret = e.compute();
			System.out.println(ret);

			String str = sb.toString();
			System.out.println("===" + str);

			Assert.assertEquals(str,
					"getTableField(tableId,fieldName) > 3 && (getTableField(tableId,fieldName) LIKE 23)");
			Assert.assertEquals(1, ret);
		}
	}

	@Test
	public void testAppend() throws ServerException {
		EXP e = EXP.INS().key("id", 123).append("ORDER BY timestamp DESC");
		StringBuffer sb = new StringBuffer();
		ArrayList<Object> params = new ArrayList<>();
		e.toSQL(sb, params);

		String str = sb.toString();
		String pstr = JSON.toJSONString(params);
		System.out.println("===" + str);
		System.out.println(">>>" + pstr);

		Assert.assertEquals(str, "id = ? ORDER BY timestamp DESC");
	}

	@Test
	public void testAppend2() throws Exception {
		StringBuffer sb = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		EXP e = EXP.INS().key("123", "456").andKey("456", "456").append("group by name");
		e.toSQL(sb, params);

		String str = sb.toString();
		String pstr = JSON.toJSONString(params);
		System.out.println("===" + str);
		System.out.println(">>>" + pstr);

		Assert.assertEquals(str, "123 = ? AND 456 = ? group by name");

	}
}
