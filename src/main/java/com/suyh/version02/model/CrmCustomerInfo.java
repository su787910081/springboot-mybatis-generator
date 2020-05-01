package com.suyh.version02.model;

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
 * @date: 2020-05-01 17:50:43
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间【格式：yyyy-MM-dd】")
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

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键：客户ID
     *
     * @return CUSTOMER_ID - 主键：客户ID
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * 设置主键：客户ID
     *
     * @param customerId 主键：客户ID
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId == null ? null : customerId.trim();
    }

    /**
     * 获取客户全称
     *
     * @return FULL_NAME - 客户全称
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * 设置客户全称
     *
     * @param fullName 客户全称
     */
    public void setFullName(String fullName) {
        this.fullName = fullName == null ? null : fullName.trim();
    }

    /**
     * 获取客户简称
     *
     * @return SHORT_NAME - 客户简称
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * 设置客户简称
     *
     * @param shortName 客户简称
     */
    public void setShortName(String shortName) {
        this.shortName = shortName == null ? null : shortName.trim();
    }

    /**
     * 获取负责人
     *
     * @return MANAGER_NAME - 负责人
     */
    public String getManagerName() {
        return managerName;
    }

    /**
     * 设置负责人
     *
     * @param managerName 负责人
     */
    public void setManagerName(String managerName) {
        this.managerName = managerName == null ? null : managerName.trim();
    }

    /**
     * 获取联系电话
     *
     * @return MANAGER_PHONE - 联系电话
     */
    public String getManagerPhone() {
        return managerPhone;
    }

    /**
     * 设置联系电话
     *
     * @param managerPhone 联系电话
     */
    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone == null ? null : managerPhone.trim();
    }

    /**
     * 获取是否内部用户【radio:Y/N】
     *
     * @return INNER_CUSTOMER - 是否内部用户【radio:Y/N】
     */
    public String getInnerCustomer() {
        return innerCustomer;
    }

    /**
     * 设置是否内部用户【radio:Y/N】
     *
     * @param innerCustomer 是否内部用户【radio:Y/N】
     */
    public void setInnerCustomer(String innerCustomer) {
        this.innerCustomer = innerCustomer == null ? null : innerCustomer.trim();
    }

    /**
     * 获取客户类型【select:1-内部客户,2-供应商,3-承运商】
     *
     * @return CUSTOMER_TYPE - 客户类型【select:1-内部客户,2-供应商,3-承运商】
     */
    public String getCustomerType() {
        return customerType;
    }

    /**
     * 设置客户类型【select:1-内部客户,2-供应商,3-承运商】
     *
     * @param customerType 客户类型【select:1-内部客户,2-供应商,3-承运商】
     */
    public void setCustomerType(String customerType) {
        this.customerType = customerType == null ? null : customerType.trim();
    }

    /**
     * 获取联系地址
     *
     * @return ADDRESS - 联系地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置联系地址
     *
     * @param address 联系地址
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * 获取是否使用平台系统【radio:1-是,2-否】
     *
     * @return US_PLAT_SYSTEM - 是否使用平台系统【radio:1-是,2-否】
     */
    public String getUsPlatSystem() {
        return usPlatSystem;
    }

    /**
     * 设置是否使用平台系统【radio:1-是,2-否】
     *
     * @param usPlatSystem 是否使用平台系统【radio:1-是,2-否】
     */
    public void setUsPlatSystem(String usPlatSystem) {
        this.usPlatSystem = usPlatSystem == null ? null : usPlatSystem.trim();
    }

    /**
     * 获取平台管理员帐号，非自有系统用户才会有。
     *
     * @return PLATFORM_ADMIN_USER - 平台管理员帐号，非自有系统用户才会有。
     */
    public String getPlatformAdminUser() {
        return platformAdminUser;
    }

    /**
     * 设置平台管理员帐号，非自有系统用户才会有。
     *
     * @param platformAdminUser 平台管理员帐号，非自有系统用户才会有。
     */
    public void setPlatformAdminUser(String platformAdminUser) {
        this.platformAdminUser = platformAdminUser == null ? null : platformAdminUser.trim();
    }

    /**
     * 获取创建时间
     *
     * @return CREATED_DATE - 创建时间
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * 设置创建时间
     *
     * @param createdDate 创建时间
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * 获取月结日，以0 表示自然月
     *
     * @return SETTLE_DAY - 月结日，以0 表示自然月
     */
    public BigDecimal getSettleDay() {
        return settleDay;
    }

    /**
     * 设置月结日，以0 表示自然月
     *
     * @param settleDay 月结日，以0 表示自然月
     */
    public void setSettleDay(BigDecimal settleDay) {
        this.settleDay = settleDay;
    }

    /**
     * 获取价格，10 位长度，两位小数位
     *
     * @return PRICE - 价格，10 位长度，两位小数位
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置价格，10 位长度，两位小数位
     *
     * @param price 价格，10 位长度，两位小数位
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取启用状态【radio:1-是(启用),2-否(停用)】
     *
     * @return USING_STATUS - 启用状态【radio:1-是(启用),2-否(停用)】
     */
    public BigDecimal getUsingStatus() {
        return usingStatus;
    }

    /**
     * 设置启用状态【radio:1-是(启用),2-否(停用)】
     *
     * @param usingStatus 启用状态【radio:1-是(启用),2-否(停用)】
     */
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