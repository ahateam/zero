package zyxhj.cms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;

public class WxOAuth2Controller extends Controller {

	private static Logger log = LoggerFactory.getLogger(WxOAuth2Controller.class);

	private WxDataService wxDataService;
	// private WxMpMessageRouter wxMpMessageRouter;
//	private WxPaycService wxPaycService;

	public WxOAuth2Controller(String node) {
		super(node);
		try {
			wxDataService = Singleton.ins(WxDataService.class);
//			wxPaycService = Singleton.ins(WxPaycService.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

////
////	/*
////	 * 添加子菜单test
////	 */
////	@POSTAPI(path = "addMenu", //
////			des = "添加子菜单"//
////	)
////	public APIResponse addMenu(//
////			@P(t = "地址") String url //
////	) throws Exception {
////		// WxMpUserList wxUserList = wxMpService.getUserService().userList(null);
////		// ret(resp, wxUserList.toString());
////		wxFuncService.addMenu(wxDataService.getWxMpService(), url);
////		return APIResponse.getNewSuccessResp();
////	}
////
////	/*
////	 * 消息群发
////	 */
////	@POSTAPI(path = "messageToMany", //
////			des = "消息群发"//
////	)
////	public void messageToMany(HttpServerRequest req, HttpServerResponse resp, RoutingContext context) throws Exception {
////		WxMpUserList userList = wxFuncService.getTest(wxDataService.getWxMpService());
////		wxFuncService.messageToMany(wxDataService.getWxMpService(), userList.getOpenids());
////	}
////
////	/*
////	 * 模板消息发送测试
////	 */
////	@GET(path = "templateMessage", //
////			des = "模版消息发送"//
////	)
////	public void templateMessage(HttpServerRequest req, HttpServerResponse resp, RoutingContext context)
////			throws Exception {
////		// wxFuncService.templateMessageTest(wxDataService.getWxMpService());
////	}
//
////	// 回复编码
////	private void ret(HttpServerResponse resp, String str) {
////		int len = str.getBytes(CodecUtils.CHARSET_UTF8).length;
////		resp.putHeader("content-type", "text/html;charset=utf-8");
////		resp.putHeader("content-length", Integer.toString(len));
////		resp.write(str);
////	}
//
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
		WxMaJscode2SessionResult jsCode2SessionInfo = wxDataService.getWxMaService().jsCode2SessionInfo(code);
//		wxDataService.getWxMaService().getUserService().getUserInfo(sessionKey, encryptedData, ivStr);
		// WxMpUser wxMpUser =
		// wxDataService.getWxMpService().oauth2getUserInfo(wxMpOAuth2AccessToken,
		// null);
		// System.err.println(wxMpUser.getOpenId());
		return APIResponse.getNewSuccessResp(jsCode2SessionInfo);
	}

//	/*
//	 * 支付
//	 */
//	@POSTAPI(path = "pay", //
//			des = "支付"//
//	)
//	public APIResponse pay(//
//			@P(t = "价格") Float price, //
//			@P(t = "哪个平台支付 1支付宝 2微信支付") Integer istype, //
//			@P(t = "商品编号") String orderUid, //
//			@P(t = "商品名称") String goodsnmae //
//	) throws Exception {
//
//		return APIResponse.getNewSuccessResp(wxPaycService.pay(price, istype, orderUid, goodsnmae));
//	}
//
//	/*
//	 * 支付
//	 */
//	@POSTAPI(path = "notifyPay", //
//			des = "notifyPay"//
//	)
//	public APIResponse notifyPay(//
//			@P(t = "回调") GLpayApi payAPI //
//	) throws Exception {
//
//		return APIResponse.getNewSuccessResp(wxPaycService.notifyPay(payAPI));
//	}

////
////	/*
////	 * 获取二维码
////	 */
////	@GET(path = "getTicket", //
////			des = "获取二维码"//
////	)
////	public APIResponse getTicket(HttpServerRequest req, HttpServerResponse resp, RoutingContext context)
////			throws Exception {
////		File ticket = wxFuncService.getTicket(wxDataService.getWxMpService(), "123456");
////		return APIResponse.getNewSuccessResp(ticket);
////	}
//
}
