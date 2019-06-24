package zyxhj.utils.data.ts;

import java.util.Map;
import java.util.TreeMap;

/**
 * TS对象基类，用于存储动态字段
 *
 */
public class TSEntity {

	public Map<String, Object> dynamicFields = new TreeMap<>();
}
