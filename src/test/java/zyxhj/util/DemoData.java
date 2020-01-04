package zyxhj.util;

import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;

import lombok.Data;

@Data
public class DemoData {
	//户序号
	@ExcelProperty(index = 0)
	private Integer famliyNumber;

	//姓名
	@ExcelProperty(index = 1)
	private String name;
	
	//身份证
	@ExcelProperty(index = 2)
	private String idNumber;
	
	//性別
	@ExcelProperty(index = 3)
	private String sex;
	

	//与户主关系
	@ExcelProperty(index = 4)
	private String familyRelations;
	
	//电话号码
	@ExcelProperty(index = 5)
	private String mobile;
	
	//股份
	@ExcelProperty(index = 6)
	private Double shareAmount;
	
	//资源股
	@ExcelProperty(index = 7)
	private Double resourceShares;
	
	//资产股
	@ExcelProperty(index = 8)
	private Double assetShares;
	
	//是否组织成员
	@ExcelProperty(index = 9)
	private Boolean isOrgUser;
	
	//投票权重
	@ExcelProperty(index = 10)
	private Double weight;
	
	//家庭住址
	@ExcelProperty(index = 11)
	private String address;
	
	//户主姓名
	@ExcelProperty(index = 12)
	private String familyMaster;
	
	//是否持证人
	@ExcelProperty(index = 13)
	private Boolean shareCerHolder;
	
	//股权证号
	@ExcelProperty(index = 14)
	private String shareCerNo;
	
	//股东
	@ExcelProperty(index = 15)
	private String Shareholders;
	
	//董事会
	@ExcelProperty(index = 16)
	private String directors;
	
	//监事会
	@ExcelProperty(index = 17)
	private String supervisors;
	
	//其他职位
	@ExcelProperty(index = 18)
	private String otherPosition;
	
	//分组
	@ExcelProperty(index = 19)
	private String groupss;
	
	//标签
	@ExcelProperty(index = 20)
	private String tags;

	public Integer getFamliyNumber() {
		return famliyNumber;
	}

	public void setFamliyNumber(Integer famliyNumber) {
		this.famliyNumber = famliyNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
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

	public String getFamilyRelations() {
		return familyRelations;
	}

	public void setFamilyRelations(String familyRelations) {
		this.familyRelations = familyRelations;
	}

	public Double getShareAmount() {
		return shareAmount;
	}

	public void setShareAmount(Double shareAmount) {
		this.shareAmount = shareAmount;
	}

	public Double getResourceShares() {
		return resourceShares;
	}

	public void setResourceShares(Double resourceShares) {
		this.resourceShares = resourceShares;
	}

	public Double getAssetShares() {
		return assetShares;
	}

	public void setAssetShares(Double assetShares) {
		this.assetShares = assetShares;
	}

	public Boolean getIsOrgUser() {
		return isOrgUser;
	}

	public void setIsOrgUser(Boolean isOrgUser) {
		this.isOrgUser = isOrgUser;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFamilyMaster() {
		return familyMaster;
	}

	public void setFamilyMaster(String familyMaster) {
		this.familyMaster = familyMaster;
	}

	public Boolean getShareCerHolder() {
		return shareCerHolder;
	}

	public void setShareCerHolder(Boolean shareCerHolder) {
		this.shareCerHolder = shareCerHolder;
	}

	public String getShareCerNo() {
		return shareCerNo;
	}

	public void setShareCerNo(String shareCerNo) {
		this.shareCerNo = shareCerNo;
	}

	public String getShareholders() {
		return Shareholders;
	}

	public void setShareholders(String shareholders) {
		Shareholders = shareholders;
	}

	public String getDirectors() {
		return directors;
	}

	public void setDirectors(String directors) {
		this.directors = directors;
	}

	public String getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(String supervisors) {
		this.supervisors = supervisors;
	}

	public String getOtherPosition() {
		return otherPosition;
	}

	public void setOtherPosition(String otherPosition) {
		this.otherPosition = otherPosition;
	}

	public String getGroupss() {
		return groupss;
	}

	public void setGroupss(String groupss) {
		this.groupss = groupss;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	
	
}
