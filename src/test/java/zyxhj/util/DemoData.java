package zyxhj.util;

import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class DemoData {

	@ExcelProperty(value = "姓名")
	private String name;
	
	@ExcelProperty(value = "性別")
	private String sex;
	
	@ExcelProperty(value = "出生年月")
	private Date birthday;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	
}
