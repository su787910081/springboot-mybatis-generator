package com.suyh.version03.plugin.custom;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FilterMapper<Model, Filter> {
    /**
     * 过滤(模糊)查询
     * @param filter
     * @return
     */
    List<Model> selectModelByFilterLike(@Param("filter") Filter filter);

}
