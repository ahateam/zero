package zyxhj.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class ExcelDataListener extends AnalysisEventListener<TestExcelModel> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataListener.class);
    /**
     *	 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    List<TestExcelModel> list = new ArrayList<TestExcelModel>();
	
	
	@Override
	public void invoke(TestExcelModel data, AnalysisContext context) {
//		 LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
	        list.add(data);
	        if (list.size() >= BATCH_COUNT) {
	            saveData();
	            list.clear();
	        }
	}


	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		saveData();
        LOGGER.info("所有数据解析完成！");
	}

	private void saveData() {
		for(TestExcelModel t : list) {
			System.out.println(t.getName()+"\t"+t.getSex()+"\t"+t.getAge()+"\t"+t.getMobile());
		}
		LOGGER.info("{}条数据，开始存储数据库！", list.size());
        LOGGER.info("存储数据库成功！");
	}

}
