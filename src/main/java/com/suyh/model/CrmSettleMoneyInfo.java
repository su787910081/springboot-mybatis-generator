package com.suyh.model;

public class CrmSettleMoneyInfo {
    /**
     * Column: customer_id
     *   主键：映射到客户基础信息表客户ID
     */
    private String customerId;

    /**
     * Column: settle_type
     *   结算方式
     */
    private String settleType;

    /**
     * Column: settle_day
     *   月结日，以0 表示自然月
     */
    private Integer settleDay;

    /**
     * Column: tax_rate
     *   开票税点(%)
     */
    private String taxRate;

    /**
     * Column: enable_delay_days
     *   允许延期天数
     */
    private Integer enableDelayDays;

    /**
     * Column: max_arrears_money
     *   欠款上限(元)
     */
    private Integer maxArrearsMoney;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    public Integer getSettleDay() {
        return settleDay;
    }

    public void setSettleDay(Integer settleDay) {
        this.settleDay = settleDay;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public Integer getEnableDelayDays() {
        return enableDelayDays;
    }

    public void setEnableDelayDays(Integer enableDelayDays) {
        this.enableDelayDays = enableDelayDays;
    }

    public Integer getMaxArrearsMoney() {
        return maxArrearsMoney;
    }

    public void setMaxArrearsMoney(Integer maxArrearsMoney) {
        this.maxArrearsMoney = maxArrearsMoney;
    }
}