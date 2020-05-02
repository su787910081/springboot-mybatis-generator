package com.suyh.version03.mapper;

import com.suyh.version03.model.CrmCustomerInfo;
import com.suyh.version03.plugin.custom.BaseMapper;
import java.util.List;

public interface CrmCustomerInfoMapper extends BaseMapper<CrmCustomerInfo> {
    int deleteByPrimaryKey(String customerId);

    int insert(CrmCustomerInfo record);

    CrmCustomerInfo selectByPrimaryKey(String customerId);

    List<CrmCustomerInfo> selectAll();
}