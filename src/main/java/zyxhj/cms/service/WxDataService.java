package zyxhj.cms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import zyxhj.core.service.UserService;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;

public class WxDataService extends Controller{

	private static Logger log = LoggerFactory.getLogger(WxDataService.class);
	private DruidDataSource ds;
	private UserService userService;

	private WxMaInMemoryConfig wxMaInMemoryConfig;
	private WxMaService wxMaService;
//	private WxPayService wxPayService;

	public WxDataService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
			userService = Singleton.ins(UserService.class);
			// 微信参数配置
			wxMaInMemoryConfig = new WxMaInMemoryConfig();
			wxMaInMemoryConfig.setAppid(WxDataService.APPID);// APPid
			wxMaInMemoryConfig.setSecret(WxDataService.APPSECRET);// AppSecret
			wxMaService = new WxMaServiceImpl();
			wxMaService.setWxMaConfig(wxMaInMemoryConfig);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static final String APPID = "wxbe41dad7130b6dcf";
	public static final String APPSECRET = "0487f9aab9d0863ac001bd76a9030987";
	public static final String MCHID = "3150";// 商户账号
	public static final String Key = "ZHwfiHhZmfLFt8MTP7flBpW8pWZpF8f7";// 商户密钥

	public WxMaInMemoryConfig getWxMaInMemoryConfig() {
		return wxMaInMemoryConfig;
	}

	public WxMaService getWxMaService() {
		return wxMaService;
	}
	
	/*
	 * 根据用户反馈授权获取对应token
	 */
	@POSTAPI(path = "getAccessToken", //
			des = "根据用户反馈授权，获取token"//
	)
	public APIResponse getAccessToken(//
			@P(t = "code") String code //
	) throws Exception {
		System.out.println(code);
		WxMaJscode2SessionResult jsCode2SessionInfo = getWxMaService().jsCode2SessionInfo(code);
//		wxDataService.getWxMaService().getUserService().getUserInfo(sessionKey, encryptedData, ivStr);
		// WxMpUser wxMpUser =
		// wxDataService.getWxMpService().oauth2getUserInfo(wxMpOAuth2AccessToken,
		// null);
		// System.err.println(wxMpUser.getOpenId());
		return APIResponse.getNewSuccessResp(jsCode2SessionInfo);
	}

//	public WxPayUnifiedOrderResult WxpayUnifiedOrder(String body, Integer money, String spbillCreateIp,
//			String notifyUrl, String openId) throws Exception {
//		WxPayUnifiedOrderRequest request = WxPayUnifiedOrderRequest.newBuilder().body(body).totalFee(money)
//				.spbillCreateIp(spbillCreateIp).notifyUrl(notifyUrl).tradeType(TradeType.JSAPI).openid(openId)
//				.outTradeNo(String.valueOf(IDUtils.getSimpleId())).build();
//		request.setSignType(SignType.HMAC_SHA256);
//
//		WxPayUnifiedOrderResult unifiedOrder = wxPayService.unifiedOrder(request);
//		System.out.println(unifiedOrder);
//		return unifiedOrder;
//	}
	
	
	
	
	/////////////////////////////////////////
	
	//微信登录

			/**
			 * 
			 */
			@POSTAPI(//
					path = "loginByWxOpenId", //
					des = " 微信号登录", //
					ret = "用户信息"//
			)
			public APIResponse loginByWxOpenId(//
					@P(t = "微信openId") String wxOpenId, //
					@P(t = "用户名") String name, //
					@P(t = "扩展信息") String ext //

			) throws Exception {
				try (DruidPooledConnection conn = ds.getConnection()) {

					return APIResponse.getNewSuccessResp(userService.loginByWxOpenId(conn, wxOpenId, name, ext));
				}
			}

			/**
			 * 修改用户的身份证
			 */
			@POSTAPI(//
					path = "editUserIdNumber", //
					des = "修改用户的身份证", //
					ret = "返回修改信息")
			public APIResponse editUserIdNumber(//
					@P(t = "管理员编号") Long adminUsreId, //
					@P(t = "用户编号") Long userId, //
					@P(t = "用户身份证号码(已添加索引，无需查重）") String IdNumber //
			) throws Exception {
				try (DruidPooledConnection conn = ds.getConnection()) {
					return APIResponse.getNewSuccessResp(userService.editUserIdNumber(conn, adminUsreId, userId, IdNumber));
				}
			}

			/**
			 * 修改用户的身份证
			 */
			@POSTAPI(//
					path = "editUserInfo", //
					des = "修改用户的信息", //
					ret = "返回修改信息")
			public APIResponse editUserInfo(//
					@P(t = "用户编号") Long userId, //
					@P(t = "用户名", r = false) String name, //
					@P(t = "电话号码", r = false) String mobile, //
					@P(t = "邮箱", r = false) String email //
			) throws Exception {
				try (DruidPooledConnection conn = ds.getConnection()) {
					return APIResponse.getNewSuccessResp(userService.editUserInfo(conn, userId, name, mobile, email));
				}
			}

			/**
			 * 获取用户
			 */
			@POSTAPI(//
					path = "getUserById", //
					des = "根据id用户的信息", //
					ret = "返回用户信息")
			public APIResponse getUserById(//
					@P(t = "用户编号") Long userId //
			) throws Exception {
				try (DruidPooledConnection conn = ds.getConnection()) {
					return APIResponse.getNewSuccessResp(userService.getUserById(conn, userId));
				}
			}
			

}
