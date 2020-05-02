package com.suyh.version03.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户基本信息表结构：内部客户、供应商、承运商
 * 
 * @table: CRM_CUSTOMER_INFO
 * @author: suyh
 * @date: 2020-05-02 21:06:52
 */
@ApiModel(value = "客户基本信息表结构：内部客户、供应商、承运商")
public class CrmCustomerInfo implements Serializable {
    /**
     * Column: CUSTOMER_ID
     *   主键：客户ID
     */
    @ApiModelProperty(value = "主键：客户ID")
    private String customerId;

    /**
     * Column: FULL_NAME
     *   客户全称
     */
    @ApiModelProperty(value = "客户全称")
    private String fullName;

    /**
     * Column: SHORT_NAME
     *   客户简称
     */
    @ApiModelProperty(value = "客户简称")
    private String shortName;

    /**
     * Column: MANAGER_NAME
     *   负责人
     */
    @ApiModelProperty(value = "负责人")
    private String managerName;

    /**
     * Column: MANAGER_PHONE
     *   联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String managerPhone;

    /**
     * Column: INNER_CUSTOMER
     *   是否内部用户【radio:Y/N】
     */
    @ApiModelProperty(value = "是否内部用户【radio:Y/N】")
    private String innerCustomer;

    /**
     * Column: CUSTOMER_TYPE
     *   客户类型【select:1-内部客户,2-供应商,3-承运商】
     */
    @ApiModelProperty(value = "客户类型【select:1-内部客户,2-供应商,3-承运商】")
    private String customerType;

    /**
     * Column: ADDRESS
     *   联系地址
     */
    @ApiModelProperty(value = "联系地址")
    private String address;

    /**
     * Column: US_PLAT_SYSTEM
     *   是否使用平台系统【radio:1-是,2-否】
     */
    @ApiModelProperty(value = "是否使用平台系统【radio:1-是,2-否】")
    private String usPlatSystem;

    /**
     * Column: PLATFORM_ADMIN_USER
     *   平台管理员帐号，非自有系统用户才会有。
     */
    @ApiModelProperty(value = "平台管理员帐号，非自有系统用户才会有。")
    private String platformAdminUser;

    /**
     * Column: CREATED_DATE
     *   创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间【格式：yyyy-MM-dd HH:mm:ss】")
    private Date createdDate;

    /**
     * Column: SETTLE_DAY
     *   月结日，以0 表示自然月
     */
    @ApiModelProperty(value = "月结日，以0 表示自然月")
    private BigDecimal settleDay;

    /**
     * Column: PRICE
     *   价格，10 位长度，两位小数位
     */
    @ApiModelProperty(value = "价格，10 位长度，两位小数位")
    private BigDecimal price;

    /**
     * Column: USING_STATUS
     *   启用状态【radio:1-是(启用),2-否(停用)】
     */
    @ApiModelProperty(value = "启用状态【radio:1-是(启用),2-否(停用)】")
    private BigDecimal usingStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table CRM_CUSTOMER_INFO
     *
     * @author su.yunhong
     * @Date Sat May 02 21:06:52 CST 2020
     */
    private static final long serialVersionUID = 1L;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId == null ? null : customerId.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName == null ? null : fullName.trim();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName == null ? null : shortName.trim();
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName == null ? null : managerName.trim();
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone == null ? null : managerPhone.trim();
    }

    public String getInnerCustomer() {
        return innerCustomer;
    }

    public void setInnerCustomer(String innerCustomer) {
        this.innerCustomer = innerCustomer == null ? null : innerCustomer.trim();
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType == null ? null : customerType.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getUsPlatSystem() {
        return usPlatSystem;
    }

    public void setUsPlatSystem(String usPlatSystem) {
        this.usPlatSystem = usPlatSystem == null ? null : usPlatSystem.trim();
    }

    public String getPlatformAdminUser() {
        return platformAdminUser;
    }

    public void setPlatformAdminUser(String platformAdminUser) {
        this.platformAdminUser = platformAdminUser == null ? null : platformAdminUser.trim();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public BigDecimal getSettleDay() {
        return settleDay;
    }

    public void setSettleDay(BigDecimal settleDay) {
        this.settleDay = settleDay;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getUsingStatus() {
        return usingStatus;
    }

    public void setUsingStatus(BigDecimal usingStatus) {
        this.usingStatus = usingStatus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", customerId=").append(customerId);
        sb.append(", fullName=").append(fullName);
        sb.append(", shortName=").append(shortName);
        sb.append(", managerName=").append(managerName);
        sb.append(", managerPhone=").append(managerPhone);
        sb.append(", innerCustomer=").append(innerCustomer);
        sb.append(", customerType=").append(customerType);
        sb.append(", address=").append(address);
        sb.append(", usPlatSystem=").append(usPlatSystem);
        sb.append(", platformAdminUser=").append(platformAdminUser);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", settleDay=").append(settleDay);
        sb.append(", price=").append(price);
        sb.append(", usingStatus=").append(usingStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}