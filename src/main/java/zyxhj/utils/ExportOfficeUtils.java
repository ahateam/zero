package zyxhj.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import sun.misc.BASE64Encoder;

/**
 * 
 * @author JXians 模板导出word文档与PDF文档
 *
 */
public class ExportOfficeUtils {

	private static Configuration config = null;
	static {
		config = new Configuration(new Version("2.3.0"));
//		config.setClassForTemplateLoading(ExportOfficeUtils.class, "/template/");
		try {
			config.setDirectoryForTemplateLoading(new File("/contractDate"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		config.setDefaultEncoding("UTF-8");
	}

	/**
	 * 获取配置对象
	 * 
	 * @return
	 */
	public static Configuration getConfiguration() {
		return config;
	}

	/**
	 * 合成数据与模板
	 * 
	 * @param template 模板名称
	 * @param obj      模板需要填入的值
	 */
	public static String generate(String templateName, Object obj) throws IOException, TemplateException {
		Configuration config = getConfiguration();
		Template template = config.getTemplate(templateName, "utf-8");
		StringWriter stringWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(stringWriter);
		template.process(obj, writer);
		String htmlStr = stringWriter.toString();
		writer.flush();
		writer.close();
		return htmlStr;
	}

	/**
	 * 根据模板生成word文档
	 * 
	 * @param templateName   模板名称
	 * @param dataMap        模板需求数据
	 * @param OutputFilePath 生成文件存放地址
	 */
	public static void generateWORD(String templateName, Map<String, Object> dataMap, String OutputFilePath) {
		try {
			// Configuration 用于读取ftl文件
			Configuration configuration = getConfiguration();
			// 输出文档路径及名称
			File outFile = new File(OutputFilePath);
			Template template = configuration.getTemplate(templateName, "utf-8");
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"), 10240);
			template.process(dataMap, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成pdf文档
	 * 
	 * @param templateName 模版文件
	 * @param obj          数据
	 * @param os           输出流
	 * @throws Exception 
	 */
	public static void generatePDF(String templateName, Map<String, Object> dataMap, String PDFoutputFilePath)
			throws Exception {
		// 合并模板和数据模型 word doc os = ftl + obj
		String generate = ExportOfficeUtils.generate(templateName, dataMap);
		ByteArrayInputStream in = new ByteArrayInputStream(generate.getBytes());
		// 文字处理对象
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(in);
		// wordMLPackage -> pdf os
		FOSettings foSettings = Docx4J.createFOSettings();
		Mapper fontMapper = new IdentityPlusMapper();
        fontMapper.put("华文楷体", PhysicalFonts.get("STKaiti"));
        fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
        fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
        fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"));
        fontMapper.put("黑体", PhysicalFonts.get("SimHei"));
        fontMapper.put("楷体", PhysicalFonts.get("KaiTi"));
        fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
        fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
        fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
        fontMapper.put("仿宋", PhysicalFonts.get("FangSong"));
        fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
        fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
        fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
        fontMapper.put("等线", PhysicalFonts.get("SimSun"));
        fontMapper.put("等线 Light", PhysicalFonts.get("SimSun"));
        fontMapper.put("华文琥珀", PhysicalFonts.get("STHupo"));
        fontMapper.put("华文隶书", PhysicalFonts.get("STLiti"));
        fontMapper.put("华文新魏", PhysicalFonts.get("STXinwei"));
        fontMapper.put("华文彩云", PhysicalFonts.get("STCaiyun"));
        fontMapper.put("方正姚体", PhysicalFonts.get("FZYaoti"));
        fontMapper.put("方正舒体", PhysicalFonts.get("FZShuTi"));
        fontMapper.put("华文细黑", PhysicalFonts.get("STXihei"));
        fontMapper.put("宋体扩展",PhysicalFonts.get("simsun-extB"));
        fontMapper.put("仿宋_GB2312",PhysicalFonts.get("FangSong_GB2312"));
        fontMapper.put("新細明體",PhysicalFonts.get("SimSun"));
        //解决宋体（正文）和宋体（标题）的乱码问题
        PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));
        PhysicalFonts.put("新細明體", PhysicalFonts.get("SimSun"));
		
        wordMLPackage.setFontMapper(fontMapper);
        
		foSettings.setWmlPackage(wordMLPackage);
		foSettings.setApacheFopMime("application/pdf");
		Docx4J.toPDF(wordMLPackage, new FileOutputStream(PDFoutputFilePath));
	}

	/**
	 * 图片转码
	 */
	public static String getImageStr(String imgpath) {
		InputStream in = null;
		byte[] data = null;
		try {
			in = new FileInputStream(imgpath);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}

}
