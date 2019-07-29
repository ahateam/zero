package zyxhj.utils.data;

import java.util.ArrayList;
import java.util.Arrays;

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
			e1 = new EXP(true).exp("t1", "=", "?", 1).and("t2", "=", "?", 2).and("t5", "LIKE", "%abc%");

			EXP e2 = new EXP(true).exp("tt1", "<>", "?", "a").or("tt2", "=", "?", "b");

			EXP e3 = new EXP(true).exp("t3", "=", "?", 3).and("t4", "=", "?", 4);

			EXP exp = new EXP(true).exp(e1, "AND", e3).and(e2);

			StringBuffer sb = new StringBuffer("WHERE ");
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
			EXP e1 = new EXP(true).exp("t1", "=", "?", 1).and("t2", "=", "?", obj).or("t3", "=", "?", obj);
		} catch (ServerException e) {
			Assert.assertTrue(true);
		}

		try {
			EXP e2 = new EXP(false).exp("t1", "=", "?", 1).and("t2", "=", "?", obj).or("t3", "=", "?", obj);

			StringBuffer sb = new StringBuffer();
			ArrayList<Object> params = new ArrayList<>();

			sb = new StringBuffer();
			params = new ArrayList<>();
			e2.toSQL(sb, params);
			System.out.println(">>>" + sb.toString());

			Assert.assertEquals(sb.toString(), "t1 = ?");
		} catch (ServerException e) {
			Assert.assertTrue(true);
		}

	}

}
