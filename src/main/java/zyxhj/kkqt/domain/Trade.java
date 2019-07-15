package zyxhj.kkqt.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.rds.RDSAnnIndex;

/**
 * 支付交易
 *
 */
@RDSAnnEntity(alias = "tb_trade")
public class Trade {

	/////////////////////////////////////////////////////////////

	// 交易状态，支付中（设定支付宝链接有效时间为30分钟）
	public static final Byte TRADE_STATUS_PAYING = 0;

	// 交易状态，已通知（已收到支付宝通知，但还未验证。验证后方能确定是正确有效的通知）
	public static final Byte TRADE_STATUS_NOTIFIED = 1;

	// 交易状态，已支付（设定支付1个月后，将不支持退款，支付宝方面是3个月）
	public static final Byte TRADE_STATUS_PAID = 2;

	/////////////////////////////////////////////////////////////

	// 退款状态，空状态，未发起退款
	public static final Byte REFUND_STATUS_NULL = 0;

	// 退款状态，已退款
	public static final Byte REFUND_STATUS_REFUNDED = 1;

	/////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////

	/**
	 * 交易编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 商品订单编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long orderId;

	/**
	 * 用户编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 商品编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public long productId;

	/////////////////////////////////////////////

	/**
	 * 交易通道编号，接收前端用户选择
	 */
	@RDSAnnIndex
	@RDSAnnField(column = RDSAnnField.ID)
	public Long twId;

	/*
	 * 来自支付通道的交易编号，与id关联才可以对账
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String twTradeNo;

	/**
	 * 第三方交易信息，交易账单扩展信息（可以为空）
	 */
	@RDSAnnField(column = "VARCHAR(128)")
	public String twTradeInfo;

	/**
	 * 支付通道交易状态（如实记录避免覆盖）
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String twTradeStatus;

	/**
	 * 支付通道退款状态（如实记录避免覆盖）
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String twRefundStatus;

	/**
	 * 应用回调通知地址（订单状态变更时，我们将使用该地址回调通知应用）
	 */
	@RDSAnnField(column = "VARCHAR(128)")
	public String appNotifyUrl;

	/**
	 * 应用回调返回地址
	 */
	@RDSAnnField(column = "VARCHAR(128)")
	public String appReturnUrl;

	/**
	 * 交易金额，最小单位是分
	 */
	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double amount;

	/**
	 * 交易状态，接受支付宝回调进行状态更改
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte tradeStatus;

	/**
	 * 退款状态，接收APP申请和支付宝回调
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte refundStatus;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 支付完成时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date payTime;

	/**
	 * 退款时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date refundTime;

}
