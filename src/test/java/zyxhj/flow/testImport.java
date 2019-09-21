package zyxhj.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import zyxhj.utils.ExcelUtils;

public class testImport {

	
	@Test
	public void testdownUserList() {
		ExcelUtils excel = new ExcelUtils();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Object> map3 = new HashMap<String, Object>();
		Map<String, Object> map4 = new HashMap<String, Object>();
		Map<String, Object> map5 = new HashMap<String, Object>();
		Map<String, Object> map6 = new HashMap<String, Object>();
		map1.put("name", "张三1");
		map1.put("age", "18");
		map1.put("sex", "男");
		map2.put("name", "张三2");
		map2.put("age", "18");
		map2.put("sex", "男");
		map3.put("name", "张三3");
		map3.put("age", "18");
		map3.put("sex", "男");
		map4.put("name", "张三4");
		map4.put("age", "18");
		map4.put("sex", "男");
		map5.put("name", "张三5");
		map5.put("age", "18");
		map5.put("sex", "男");
		map6.put("name", "张三6");
		map6.put("age", "18");
		map6.put("sex", "男");
		list.add(map1);
		list.add(map2);
		list.add(map3);
		list.add(map4);
		list.add(map5);
		list.add(map6);
		int size = map1.keySet().size();
		String[] titles = new String[size];
		int i = 0;
		for (String key : map1.keySet()) {
				titles[i] = key;
				i++;
		}
		excel.downUserList(list, titles);
	}
}
