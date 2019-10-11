package zyxhj.utils;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

@SuppressWarnings("deprecation")
public class TestExcelModel extends BaseRowModel {

	@ExcelProperty(value = "姓名", index = 0)
    private String name;
	
	@ExcelProperty(value = "年龄", index = 1)
	private Integer age;
	
	@ExcelProperty(value = "性别", index = 2)
	private String sex;
	
	@ExcelProperty(value = "手机号", index = 3)
	private String mobile;
	
	
	public TestExcelModel(String name, Integer age, String sex, String mobile) {
		super();
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.mobile = mobile;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getAge() {
		return age;
	}


	public void setAge(Integer age) {
		this.age = age;
	}


	public String getSex() {
		return sex;
	}


	public void setSex(String sex) {
		this.sex = sex;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public TestExcelModel() {
		
	};

}


