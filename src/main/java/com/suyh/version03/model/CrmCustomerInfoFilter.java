package com.suyh.version03.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * 实体过滤器类
 *
 * @author suyh
 * @date 2020-05-02 18:23:44
 */
@ApiModel
public class CrmCustomerInfoFilter extends CrmCustomerInfo {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDateBefore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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