package zyxhj.flow.repository;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.flow.domain.ProcessAsset;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.rds.RDSRepository;

public class ProcessAssetRepository extends RDSRepository<ProcessAsset> {

	public ProcessAssetRepository() {
		super(ProcessAsset.class);
	}
	
	/**
	 * 
	 * @param processId
	 * 			当前流程编号
	 * @param descIds
	 * 			当前流程已经提交的资源定义编号
	 * @param count
	 * @param offset
	 * @return
	 * @throws Exception
	 * 该方法用于查询ProcessAsset列表，使用IN ORDER BY 方法查询，
	 */

	public List<ProcessAsset> getProcessAssetByDescIds(DruidPooledConnection conn, Long processId, JSONArray descIds,
			Integer count, Integer offset) throws Exception {

		//得到JSON字符串，并去除左右两边的括号
		String descIdsJSON = descIds.toJSONString();
		String newDescIdsJSON = descIdsJSON.substring(1, descIdsJSON.length() - 1);
		
		EXP exp = EXP.INS().key("process_id", processId).and(EXP.IN_ORDERED("desc_id", newDescIdsJSON));

		StringBuffer sb = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		exp.toSQL(sb, params);
		
		params.remove(1);
		String w = "";
		for(Object o : descIds) {
			w = w + "?,";
			params.add(o);			
		}
		String sbStr = sb.toString();
		
		//从sql 中分割出 IN ORDER 语句
		String inOrderSql = sbStr.substring(sbStr.indexOf("IN"), sbStr.length());
//		System.out.println("inOrderSql-->>>>"+inOrderSql);
		
		//将 分割出来的　IN　ORDER 语句中的一个？替换为需要的多个？
		String newInOrderSql = inOrderSql.replace("?", w.substring(0, w.length()-1));
//		System.out.println("newInOrderSql-->>>>"+newInOrderSql);
		
		//将原sql中的IN ORDER 语句替换为修改后的IN ORDER语句
		String newSql = sbStr.replace(inOrderSql, newInOrderSql);
//		System.out.println("newSql-->>>>"+newSql);
		
		
		return this.getList(conn, newSql, params, 100, 0);
	}
}
