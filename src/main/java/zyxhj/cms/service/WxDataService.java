package zyxhj.cms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;

public class WxDataService {

	private static Logger log = LoggerFactory.getLogger(WxDataService.class);

	private WxMaInMemoryConfig wxMaInMemoryConfig;
	private WxMaService wxMaService;
//	private WxPayService wxPayService;

	public WxDataService() {
		try {
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

}
