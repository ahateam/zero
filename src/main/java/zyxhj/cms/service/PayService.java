package zyxhj.cms.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;

import zyxhj.core.service.UserService;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;

public class PayService extends Controller{
	private static Logger log = LoggerFactory.getLogger(WxDataService.class);
	private DruidDataSource ds;
	private UserService userService;
	
	public PayService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
			userService = Singleton.ins(UserService.class);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	String wx_appid = "";
	//商户号
	String wx_mch_id = "";
	//设备号
	String device_info ="";
	//随机字符串
	String nonce_str = "";
	//签名
	String sign = "";
	//签名类型
	String sign_type = "";
	//商品描述
	String body = "";
	//商品详情
	String detail = "";
	//附加数据
	String attach = "";
	//商品订单号
	String out_trade_no = "";
	//标价金额
	String total_fee = "";
	//终端IP
	String spbill_create_ip ="";
	//通知地址
	String notify_url ="";
	//交易类型
	String trade_type ="";
	//用户标识
	String openid = "";
	
	
	
	
	@POSTAPI(//
			path = "sendwxpay", //
			des = "微信支付", //
			ret = "用户"//
	)
	public APIResponse sendwxpay(//
			@P(t = "百度openId") String bdopenId, //
			@P(t = "用户名") String name, //
			@P(t = "扩展信息") String ext //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return APIResponse.getNewSuccessResp(userService.otherLoginOpenId(conn, bdopenId, name, ext,"baidu"));
		}
	}
	
	//发起get网络请求
	public String getopanid(String url) {
	    try {
	        CloseableHttpClient client = null;
	        CloseableHttpResponse response = null;
	        try {
	            HttpGet httpGet = new HttpGet(url);
	            client = HttpClients.createDefault();
	            response = client.execute(httpGet);
	            HttpEntity entity = response.getEntity();
	            String result = EntityUtils.toString(entity);
	            System.out.println(result);
	            return result;
	        } finally {
	            if (response != null) {
	                response.close();
	            }
	            if (client != null) {
	                client.close();
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	
	
	
		/**
		     * 向指定 URL 发送POST方法的请求
		  * 
		  * @param url
		     * 发送请求的 URL
		  * @param param
		     * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
		  * @return 所代表远程资源的响应结果
		  */
	    public static String sendPost(String url, String param) {
	        PrintWriter out = null;
	        BufferedReader in = null;
	        String result = "";
	        try {
	            URL realUrl = new URL(url);
	            // 打开和URL之间的连接
	            URLConnection conn = realUrl.openConnection();
	            // 设置通用的请求属性
	            conn.setRequestProperty("accept", "*/*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // 发送POST请求必须设置如下两行
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            // 获取URLConnection对象对应的输出流
	            out = new PrintWriter(conn.getOutputStream());
	            // 发送请求参数
	            out.print(param);
	            // flush输出流的缓冲
	            out.flush();
	            // 定义BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("发送 POST 请求出现异常！"+e);
	            e.printStackTrace();
	        }
	        //使用finally块来关闭输出流、输入流
	        finally{
	            try{
	                if(out!=null){
	                    out.close();
	                }
	                if(in!=null){
	                    in.close();
	                }
	            }
	            catch(IOException ex){
	                ex.printStackTrace();
	            }
	        }
	        return result;
	    }    
}
