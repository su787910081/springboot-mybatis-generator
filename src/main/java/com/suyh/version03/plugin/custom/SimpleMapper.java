package com.suyh.version03.plugin.custom;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mybatis基础DAO
 */
public interface SimpleMapper<T> {
    /**
     * 过滤查询
     * @param filter
     * @return
     */
    List<T> selectModelByFilter(@Param("filter") T filter);

    /**
     * 更新记录，指定过滤条件
     * @param model
     * @param filter
     * @return
     */
    int updateModelByFilter(@Param("model") T model, @Param("filter") T filter);
}

