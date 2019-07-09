package zyxhj.flow.domain;

public class Rule {

	/**
	 * 数据源
	 */
	public String src;

	/**
	 * 操作符</br>
	 * > , < , != , == , >= , <= </br>
	 */
	public String op;

	/**
	 * 要比对的值
	 */
	public String value;
}
