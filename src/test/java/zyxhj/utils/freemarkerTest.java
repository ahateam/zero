package zyxhj.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import sun.misc.BASE64Encoder;

public class freemarkerTest {

	public String getImageStr() {  
        String imgFile = "C:\\Users\\Admin\\Desktop\\微信截图_20191115110447.png";  
        InputStream in = null;  
        byte[] data = null;  
        try {  
            in = new FileInputStream(imgFile);  
            data = new byte[in.available()];  
            in.read(data);  
            in.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        BASE64Encoder encoder = new BASE64Encoder();  
        return encoder.encode(data);  
    }
	
	
	@Test
	public void testsss(){
        Map<String,Object> dataMap = new HashMap<String, Object>();
        try {
        	dataMap.put("name", "JXians");
        	dataMap.put("sex", "男");
        	dataMap.put("imgstr", getImageStr());
            //编号
//            dataMap.put("id", "123456");
            //日期
//            dataMap.put("date", new SimpleDateFormat("yyyy年MM月dd日").format(new SimpleDateFormat("yyyy-MM-dd").parse("2018-09-19")));
            //附件张数
//            dataMap.put("number", 1);
            //受款人
//            dataMap.put("payee", "张三");
            //付款用途
//            dataMap.put("use_of_payment", "test");
            //大写金额
//            dataMap.put("capitalization_amount", "123");
            //小写金额
//            dataMap.put("lowercase_amount", "100");
            //Configuration 用于读取ftl文件
            Configuration configuration = new Configuration(new Version("2.3.0"));
            configuration.setDefaultEncoding("utf-8");
 
            /**
             * 以下是两种指定ftl文件所在目录路径的方式，注意这两种方式都是
             * 指定ftl文件所在目录的路径，而不是ftl文件的路径
             */
            //指定路径的第一种方式（根据某个类的相对路径指定）
                configuration.setClassForTemplateLoading(this.getClass(), "");
 
            //指定路径的第二种方式，我的路径是C：/a.ftl
            configuration.setDirectoryForTemplateLoading(new File("C:\\Users\\Admin\\Desktop\\"));
 
            //输出文档路径及名称
            File outFile = new File("D:/报销信息导出.pdf");
 
            //以utf-8的编码读取ftl文件
            Template template = configuration.getTemplate("姓名.ftl", "utf-8");
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"), 10240);
            template.process(dataMap, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}
