package com.suyh.mapper;

import com.suyh.model.CrmSettleMoneyInfo;
import java.util.List;

public interface CrmSettleMoneyInfoMapper {
    int deleteByPrimaryKey(String customerId);

    int insert(CrmSettleMoneyInfo record);

    CrmSettleMoneyInfo selectByPrimaryKey(String customerId);

    List<CrmSettleMoneyInfo> selectAll();

    int updateByPrimaryKey(CrmSettleMoneyInfo record);
}