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

public class OtherLoginService extends Controller{
	private static Logger log = LoggerFactory.getLogger(WxDataService.class);
	private DruidDataSource ds;
	private UserService userService;
	
	public OtherLoginService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
			userService = Singleton.ins(UserService.class);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	//头条小程序APPSecret
	private static final String tt_APPSecret = "84a1bc628d1e63983888a9b9aeb7842002d0d6d0";
	//头条小程序获取session_key和openId地址
	private static final String tt_url = "https://developer.toutiao.com/api/apps/jscode2session";
	//头条小程序appid
	private static final String tt_appid = "tt9c3aea8703101a5d";
	
//	//支付宝小程序应用公钥
//	private static final String alipay_AppPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAksvCtIGhVn1RppbNBrCP3MpMkAJ/jf/BO5p626pdzmJXtlZz7ALPTXWyKYZ5iFPhb3TnUvCZThYfTOA+f/MfRgZdXhs+5mQD9uHwERsikTUBundsHwERBMPwjFbnuTRlcNaZqib844OqV/IGnkmqn4sZFczXxD7+FS8+5aOVGGwBEEo1ZbogoS0riAawmLjHptSwVelLziUGHJlou4XAPHp9LjVyDFMcVScAY15s6+Pb/5VEWYCkGbKkT1PtrqWNQG68yGj6+/jUpOW1A+s9TX7c+mvPohds4QdM3vtll5496jd7cBJ4bGG5gUhUGcp1CL3fosKgtoKpfRu3h+FhewIDAQAB";
//	//支付宝小程序商户应用私钥
//	private static final String alipay_private_key ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCuaBNRLaLuh0rJFxMRY3SWImy+54l4QV6pL+9rUjkPaeITnh8MdBcII6wY2OiaR/9JvmtcU/HAuQ1h1TUmwK9T7SnFDbGqNbftyzl64k3YpBdyVncPRf0D26HNRjco2Hv5m8OsrHezwx1W5jhCcObU9NT/2sYHi8jfyO25Sjzo4EKFbjg7t6EfbXjGLewMON31L7T520NLC4JX9jIjUKPP4Mot315tMNJLuhndSLuclPF1rXixO7jv+taO64nab5ooPJ/l52RAOwgeXSb6nGRohkgMu0LSTYiuHy+I9CFfxT5YvXKBatoU8NNyO4Vsbpf57A5lWGQobycegeXRE8HfAgMBAAECggEAD3K+i0k8jl5DfI5jrP2fPREtirddkN5RcTECxwHQdhIN6WjvHR5HT/xA6S6FY+0OqsyIG/R/jG+XxNuqypT0sr1mHsM4wnfho9mFbOIQvSjDvufzwdGUOLxCl7kpd5+9U329llTsgWq99Y2M3C299Iz/NO5+7fouv8u/atMtk/7r1oDg0EATtcVo4pBD/vmuvlSN8PqwJ0zgAkdUO+QhlkcKG14Kbdxr54gC9W2o9TJewUQbFxGKi58V++2Cu5GXsg07Y2SHpk/zbOXNJZ7G7qwqJPtxk88vQQRzkLYu9Bi6eNqutIpmad8YKauGb3AWGIWopcZeqSpSaYyQWe+HiQKBgQDUj4QD92yAS3SlJOB05Gu4HYYkTibSA0ncnfSXyrH3CavVGValQvbZN5X1yU86FSbPWSmtNhHO3DzRDrhjyG0492cjxL3KO9/BIDsgHcF+1MDZ8TfTak3Kt2ISkUNKT0GR3YEBOGczpCsgtNzVTSYzyiCIHGUWxGqA9/5ebLmjIwKBgQDSDHZiQd9wIFkGFOG/sG/jj0gBDtBOy3ABOiVNHhVIVzlOaKBIGNuQvYhbwb6/c51pFY2dCUUY0Nvk/6BxekqPPytqjNus2sm3DqkPC/UGDx1JA1IbVzUZo323RBU57JZCQ1rNfQDftt5khFF/WW10soSaumsw1Z7L/m2STocgFQKBgQC0lZD0xbn6bSCWRPQkmwMhEoQqR/MXLRANTGzGoL/9PswraTGBiZiqBl1yySp0EBA+zGNp8qvCcL2CZt0keNzCLv4DF1yeeaupKbbRnDYNiLam8hYZmRCqDo5Vc/AVK6xrPiIhZUfYSbsSOFX9KWQxT6G/wU/ovco183q5wekcvQKBgG/E8+Wa+cNXYyeqSunOf+tN3dm6QTv8YtA0WY7smBSTXUoMwGU83PDq+bkholeLtNIrBtzuBteKo8f6Ai0ADtaknXyoEWisTzPaWDlKeopL1qB4ZONphzbqWUYAs27MJrSB1LqNbr6cN0CzbA5gzq6PI6KV2d31ffc8np+Aq8R1AoGAKX7mydt+Cj8alY5BqgYKq8ghSxqdMCzx/jF1aWQiFr730iCz0NWxrXGmocgRWaC4iE++Xto+beMwFbJqOgUq2LkoZELth7OWhReyKfsUBGJ/o4PuH2fAC03dLEHnjQzj91QzYwjWg4AIucWD4Wb40OIM11KkD8ph+M+kNFYbu/Y=";
//	//支付宝小程序appid
//	private static final String alipay_appId = "2019091867533713";
//	
	//支付宝小程序应用公钥
	private static final String alipay_AppPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkVJaxHeUllmbwkqfJQwl+dfTQ47kXP7ayFLrX04vDLGhW1/9iLb0WdmvyvicrLM/y9GCT8IToQJHMJSxmsxQbpMyvGLzFqxSIWydL7tybLeK0YRrLyzU8ELTc7dsdlm1WGV/9KFZkqes1iQbXeAA0jHP3anxd6mduhI8ezVBUMZMfs7s4vRNNwc/ZajKv8wZhiKfTH04AEVVT/WAg9DqQQKXaAWTb+Wogjs6vVwnwWefF4TBKzrywzhe5IyRbQcaB9ti7xpbhuxUNgNEiiRZCNOYU860WREqouTGmCsiZKZObkV2TwGkBPwgD5BfNpsx1aTlVk7WapC4cGek8b3cDQIDAQAB";
	//支付宝小程序商户应用私钥
	private static final String alipay_private_key ="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCWK/qv8eTNSCdTuWUyt6f5TRoNuAs3dPgyjDIDv0tXFHqhURH9BbUw7mR2h+Rsrhcrziq0nYOzFDzVjSuT7DgTeCgMxEPtwUX2cqXdbouoOHDWK+/Qrjw8MYzRvHlb94qvNgVULFRsyrkHidnUUoyy9qmJlM9KVmRSv1QOc8yG2Tca7mOaUT89745cmIxTEDIMOCiTOSp3x6jQgHd7CYzvDjxT65vovNDE6g+CCvpsmscX3uXyBO+FhxnFq/QjvC/rGQjr/Dt3AnUBq6vGOjMwPYrJ3JiG1TFdlo+wp++QBRz+tl029IMeYl5E2nQa5P3gGxLwXmWxSR4eyY6ft7cLAgMBAAECggEAES9/RNtd39A+D+rLlf5GjtUwAhnpzrLoJIC+yIb/zDtfvU5VzKr2jV76yEXM8lMJ+81gPcM46ymJvLanIgqKcE/z5WIgk/jmVMnSCYE5IkszPo9QpoUKB6l0ABydltTBXkPEYFmiX8DjY+V2tEGuMFziEgihoFQ6ru2OZQkDgvrzQ+sXn2tniZI0FBli4EJbkibeXVR8IJM3eu7IHrE2jERaSvfjsnxwUtqzvKUGAjPlHnL9C4B3xkXAumrhEhOXUUJfCOyuTtNzSZLlDTcMizYBCsAhg4qYKZUaTnSybbC3yy+ndmIQWSQQ5wOdE1JbIdd03g9D4LNvi/ahHUmrYQKBgQDnB/6gxMxte8zufxC+9jH223cCokzpAVxAhdssf4jplNU4WvEiD69V6OM5Ea34NqPjPscGJsmNqCaP+g+R+jdrUHcoliYldCnGU1PfnU0Gy46z1SGZjYrcI8PsSZnKlaqkXGJnzzUaDALd1maSboTtD5OM3+rcVedKQ6UjjYULrQKBgQCmZtL33BsKqNExvasQEkdeICaTKTbAz98MmraqO326gzhhy01CvmyMOSz6gYpiOFtm1wql0ZjurHIvZ4ubeTtD9epdj3aJ8ds0T+yHetfQbeFZLtqGsvjVJs0uRaN7UnJQXCsuaBruWv6hOxk4aImf5Q68Wk+y3T+vjwTXni+klwKBgBnAaWBwLz0w5HndDffLJ2yVr+FKR/pc3XSPtwtxt8gZf97FPEWIrAmOaO1ujA6zwHbPA44q+qJgkNPRkrS5yD2c/Xs3qU7ZcOKbXSj6tqBARkrDQfiEr2q0Td1jotHS9u9VIN7jWM7rcdJaWJqP2R83ckHsn5gptxsvCRQ6bQ8NAoGASMbZ6Cus7AmPjNiuU1YbPpCDLoOs/cEshX+4+lUQlR1E8IiRHurX4LA0+s7jLjgFycGtV36HC4QCYLdBuNrZz8xjF05igsxzmbqG8W4Jq5ATKgUy8AygheD1hhJRAOO7OWhGPoKrnKnRicSmMCSQZI0y17AMAWeArhC4BnGZ1rsCgYAJIHf0tiGiOZB+DlgvhvLsi03VLpypY4MfHDvhl5GDWxsrbnNHfKZ6KP5dMdvmQveu1kikPsLgYMpjBWE6HVPRvIjpbX+4jyWDjRzJNsgU6SAXqGVFuTb5OL2G1TMeYeYNNcmZQpWesl5otVMlYI3gFHfoLnmXrnQ05DruzAGCyw==";
	//支付宝小程序appid
	private static final String alipay_appId = "2019092067628321";
	
	//支付宝小程序获取user_id地址
	private static final String alipay_url ="https://openapi.alipay.com/gateway.do";
	
	//百度小程序获取openid地址
	private static final String baidu_url = "https://spapi.baidu.com/oauth/jscode2sessionkey";
	//百度小程序APPId
	private static final String baidu_appid = "17082688";
	//百度小程序AppKey
	private static final String baidu_appKey = "XuWE40fWAk54RL5FBusGUaKWSMSDAy2T";
	//百度小程序AppSecret
	private static final String baidu_appSecret = "yaEDzvRSNBgpE4bseyWuOvI34vlvp0og";
	
	@POSTAPI(//
			path = "ttGetSessionkey", //
			des = " 头条获取openid和session_key", //
			ret = "用户信息"//
	)
	public APIResponse ttGetSessionkey(//
			@P(t = "头条code") String code //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			String res = getopanid(tt_url+"?appid="+tt_appid+"&secret="+tt_APPSecret+"&code="+code);
			return APIResponse.getNewSuccessResp(res);
		}
	}
	
	@POSTAPI(//
			path = "loginByTtOpenId", //
			des = " 头条登录", //
			ret = "用户信息"//
	)
	public APIResponse loginByTtOpenId(//
			@P(t = "头条OpenId") String ttOpenId, //
			@P(t = "用户名") String name, //
			@P(t = "扩展信息") String ext //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return APIResponse.getNewSuccessResp(userService.otherLoginOpenId(conn, ttOpenId, name, ext,"tt"));
		}
	}
	
	///////////////////////////////////
	///////////////////////////////////
	@POSTAPI(//
			path = "alipayGetSessionkey", //
			des = " 支付宝获取user_id和session_key", //
			ret = "用户信息"//
	)
	public APIResponse alipayGetSessionkey(//
			@P(t = "支付宝code") String code //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			AlipaySystemOauthTokenResponse res = alipayGetOpenid(code);
			System.out.println(res);
			System.out.println(res.getUserId());
			return APIResponse.getNewSuccessResp(res);
		}
	}
	
	
	@POSTAPI(//
			path = "loginByAlipayOpenId", //
			des = " 支付宝登录", //
			ret = "用户信息"//
	)
	public APIResponse loginByAlipayOpenId(//
			@P(t = "支付宝user_id") String user_id, //
			@P(t = "用户名") String name, //
			@P(t = "扩展信息") String ext //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return APIResponse.getNewSuccessResp(userService.otherLoginOpenId(conn, user_id, name, ext,"alipay"));
		}
	}
	///////////////////////////////////
	///////////////////////////////////
	
	@POSTAPI(//
			path = "baiduGetSessionkey", //
			des = " 百度获取openid和session_key", //
			ret = "用户信息"//
	)
	public APIResponse baiduGetSessionkey(//
			@P(t = "百度code") String code //
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			String res = sendPost(baidu_url, "code="+code+"&client_id="+baidu_appKey+"&sk="+baidu_appSecret);
			System.out.println(res);
			return APIResponse.getNewSuccessResp(res);
		}
	}
	
	
	@POSTAPI(//
			path = "loginByBdOpenId", //
			des = " 百度登录", //
			ret = "用户信息"//
	)
	public APIResponse loginByBdOpenId(//
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
	
	//支付宝小程序发起请求获取user_id
	public AlipaySystemOauthTokenResponse alipayGetOpenid(String code) {
		AlipayClient alipayClient = new DefaultAlipayClient(alipay_url,alipay_appId,alipay_private_key,"json","utf-8",alipay_AppPublicKey,"RSA2");
		AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
		request.setGrantType("authorization_code");
		request.setCode(code);
		//request.setRefreshToken("201208134b203fe6c11548bcabd8da5bb087a83b");
		AlipaySystemOauthTokenResponse response;
		try {
			response = alipayClient.execute(request);
			if(response.isSuccess()){
				System.out.println("调用成功");
				return response;
			} else {
				System.out.println("调用失败");
			}
		} catch (AlipayApiException e) {
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
