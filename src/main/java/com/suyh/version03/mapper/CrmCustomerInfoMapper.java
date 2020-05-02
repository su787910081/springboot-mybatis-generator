package com.suyh.version03.mapper;

import com.suyh.version03.model.CrmCustomerInfo;
import com.suyh.version03.plugin.custom.SimpleMapper;
import java.util.List;

public interface CrmCustomerInfoMapper extends SimpleMapper<CrmCustomerInfo> {
    int deleteByPrimaryKey(String customerId);

    int insert(CrmCustomerInfo record);

    CrmCustomerInfo selectByPrimaryKey(String customerId);

    List<CrmCustomerInfo> selectAll();
}