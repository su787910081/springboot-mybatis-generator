package com.suyh.version02.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
public class CrmCustomerInfo implements Serializable {
    /**
     * Column: customer_id
     *   主键：客户ID
     */
    private String customerId;

    /**
     * Column: full_name
     *   客户全称
     */
    private String fullName;

    /**
     * Column: short_name
     *   客户简称
     */
    private String shortName;

    /**
     * Column: manager_name
     *   负责人
     */
    private String managerName;

    /**
     * Column: manager_phone
     *   联系电话
     */
    private String managerPhone;

    /**
     * Column: inner_customer
     *   是否内部用户(这个字段似乎有些多余了)【radio:1-是,2-否】
     */
    private String innerCustomer;

    /**
     * Column: customer_type
     *   客户类型【select:1-内部客户,2-供应商,3-承运商】
     */
    private String customerType;

    /**
     * Column: address
     *   联系地址
     */
    private String address;

    /**
     * Column: us_plat_system
     *   是否使用平台系统(当有自有系统时则可不使用平台系统)【radio:1-是,2-否】
     */
    private String usPlatSystem;

    /**
     * Column: platform_admin_user
     *   平台管理员帐号，非自有系统用户才会有。
     */
    private String platformAdminUser;

    /**
     * Column: created_date
     *   创建时间
     */
    private Date createdDate;

    /**
     * Column: settle_day
     *   月结日，以0 表示自然月
     */
    private Integer settleDay;

    /**
     * Column: price
     *   价格，10 位长度，两位小数位
     */
    private BigDecimal price;

    /**
     * Column: using_status
     *   启用状态【radio:1-是(启用),2-否(停用)】
     */
    private Long usingStatus;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键：客户ID
     *
     * @return customer_id - 主键：客户ID
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
     * @return full_name - 客户全称
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
     * @return short_name - 客户简称
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
     * @return manager_name - 负责人
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
     * @return manager_phone - 联系电话
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
     * 获取是否内部用户(这个字段似乎有些多余了)【radio:1-是,2-否】
     *
     * @return inner_customer - 是否内部用户(这个字段似乎有些多余了)【radio:1-是,2-否】
     */
    public String getInnerCustomer() {
        return innerCustomer;
    }

    /**
     * 设置是否内部用户(这个字段似乎有些多余了)【radio:1-是,2-否】
     *
     * @param innerCustomer 是否内部用户(这个字段似乎有些多余了)【radio:1-是,2-否】
     */
    public void setInnerCustomer(String innerCustomer) {
        this.innerCustomer = innerCustomer == null ? null : innerCustomer.trim();
    }

    /**
     * 获取客户类型【select:1-内部客户,2-供应商,3-承运商】
     *
     * @return customer_type - 客户类型【select:1-内部客户,2-供应商,3-承运商】
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
     * @return address - 联系地址
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
     * 获取是否使用平台系统(当有自有系统时则可不使用平台系统)【radio:1-是,2-否】
     *
     * @return us_plat_system - 是否使用平台系统(当有自有系统时则可不使用平台系统)【radio:1-是,2-否】
     */
    public String getUsPlatSystem() {
        return usPlatSystem;
    }

    /**
     * 设置是否使用平台系统(当有自有系统时则可不使用平台系统)【radio:1-是,2-否】
     *
     * @param usPlatSystem 是否使用平台系统(当有自有系统时则可不使用平台系统)【radio:1-是,2-否】
     */
    public void setUsPlatSystem(String usPlatSystem) {
        this.usPlatSystem = usPlatSystem == null ? null : usPlatSystem.trim();
    }

    /**
     * 获取平台管理员帐号，非自有系统用户才会有。
     *
     * @return platform_admin_user - 平台管理员帐号，非自有系统用户才会有。
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
     * @return created_date - 创建时间
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
     * @return settle_day - 月结日，以0 表示自然月
     */
    public Integer getSettleDay() {
        return settleDay;
    }

    /**
     * 设置月结日，以0 表示自然月
     *
     * @param settleDay 月结日，以0 表示自然月
     */
    public void setSettleDay(Integer settleDay) {
        this.settleDay = settleDay;
    }

    /**
     * 获取价格，10 位长度，两位小数位
     *
     * @return price - 价格，10 位长度，两位小数位
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
     * @return using_status - 启用状态【radio:1-是(启用),2-否(停用)】
     */
    public Long getUsingStatus() {
        return usingStatus;
    }

    /**
     * 设置启用状态【radio:1-是(启用),2-否(停用)】
     *
     * @param usingStatus 启用状态【radio:1-是(启用),2-否(停用)】
     */
    public void setUsingStatus(Long usingStatus) {
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