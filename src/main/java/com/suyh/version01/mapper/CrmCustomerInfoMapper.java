package com.suyh.version01.mapper;

import com.suyh.version01.model.CrmCustomerInfo;
import java.util.List;

public interface CrmCustomerInfoMapper {
    int deleteByPrimaryKey(String customerId);

    int insert(CrmCustomerInfo record);

    CrmCustomerInfo selectByPrimaryKey(String customerId);

    List<CrmCustomerInfo> selectAll();

    int updateByPrimaryKey(CrmCustomerInfo record);
}