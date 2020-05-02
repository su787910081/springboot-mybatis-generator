package com.suyh.version03.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * 客户基本信息表结构：内部客户、供应商、承运商 模糊查询实体
 *
 * @author suyh
 * @date 2020-05-02 22:02:19
 */
@ApiModel(value = "客户基本信息表结构：内部客户、供应商、承运商 模糊查询实体")
public class CrmCustomerInfoFilter extends CrmCustomerInfo {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "日期匹配左边界。创建时间")
    private Date createdDateBefore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "日期匹配左边界。创建时间")
    private Date createdDateAfter;

    public Date getCreatedDateBefore() {
        return createdDateBefore;
    }

    public void setCreatedDateBefore(Date createdDateBefore) {
        this.createdDateBefore = createdDateBefore;
    }

    public Date getCreatedDateAfter() {
        return createdDateAfter;
    }

    public void setCreatedDateAfter(Date createdDateAfter) {
        this.createdDateAfter = createdDateAfter;
    }
}