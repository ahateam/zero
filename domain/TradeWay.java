package zyxhj.kkqt.domain;

public class TradeWay {

	/////////////////////////////////////////////////////////////

	// 交易通道类型，支付宝网关，WAP移动站点，app
	public static final Byte TYPE_ALIPAY_GAGEWAY_WAP = 10;

	// 交易通道类型，支付宝网关，WEB站点
	public static final Byte TYPE_ALIPAY_GAGEWAY_WEB = 11;

	// 交易通道类型，topoints网关，WAP移动站点
	public static final Byte TYPE_TPOINT_GATEWAY_WAP = 20;

	// 交易通道类型，威富通网关，WAP移动站点，app
	public static final Byte TYPE_WFT_GATEWAY_WAP = 30;

	/////////////////////////////////////////////////////////////

	// 支付通道编号
	public String id;

	// 支付通道名称
	public String name;

	// 支付通道类型（微信，wap浏览器，web浏览器，android客户端，pc客户端）
	public Byte type;

	// 商户编号，支付服务提供方提供给我们的识别代码
	public String pid;

	// 商户密钥
	public String key;

	// 卖家Email（支付宝退款时会用到）
	public String sellerEmail;

	// 备用扩展信息
	public String ext;

	// 启用或禁用
	public Boolean active;

}
