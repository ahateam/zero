package zyxhj.flow.domain;

/**
 * 规则表达式
 */
public class TableExp {

	/**
	 * 运算符</br>
	 * > , < , != , == , >= , <= , && , || </br>
	 */
	public String op;

	/**
	 * 左操作数，或其引用</br>
	 * val，表示值，ref，表示引用
	 */
	public TableExp left;

	/**
	 * 要比对的值，或其引用，或子表达式</br>
	 * val，表示值，ref，表示引用，exp，表示子表达式
	 */
	public TableExp right;
}
