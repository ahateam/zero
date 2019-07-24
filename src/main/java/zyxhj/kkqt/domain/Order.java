package zyxhj.kkqt.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.rds.RDSAnnIndex;

/**
 * 用户已购买的内容资产
 *
 */
@RDSAnnEntity(alias = "tb_order")
public class Order {

	/**
	 * 订单类型，会员
	 */
	public static final Byte TYPE_MEMBER = 0;

	/**
	 * 订单类型，商品
	 */
	public static final Byte TYPE_PRODUCT = 1;

	/**
	 * 订单状态。已创建
	 */
	public static final Byte STATE_CREATED = 0;

	/**
	 * 正在支付
	 */
	public static final Byte STATE_PAING = 1;
	/**
	 * 订单状态已支付，虚拟商品，无需发货</br>
	 * 只要已支付即生效
	 */
	public static final Byte STATE_PAID = 2;

	/**
	 * 订单唯一编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 订单类型（目前有会员活商品）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 用户编号
	 */
	@RDSAnnIndex
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 商品编号</br>
	 * 目前仅准备支持普通商品和会员类型的商品</br>
	 */
	@RDSAnnIndex
	@RDSAnnField(column = RDSAnnField.ID)
	public Long productId;

	/**
	 * 商品名称
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String productName;

	/**
	 * 交易编号</br>
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long tradeId;

	/**
	 * 商品价格
	 */
	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double amount;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 更新时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date updateTime;

}
