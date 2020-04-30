package com.suyh.mapper;

import com.suyh.model.CrmCustomerInfo;
import java.util.List;

public interface CrmCustomerInfoMapper {
    int deleteByPrimaryKey(String customerId);

    int insert(CrmCustomerInfo record);

    CrmCustomerInfo selectByPrimaryKey(String customerId);

    List<CrmCustomerInfo> selectAll();

    int updateByPrimaryKey(CrmCustomerInfo record);
}