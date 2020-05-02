package com.suyh.version03.plugin.custom;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模糊查询 接口 mapper
 *
 * @param <Model>  实体类
 * @param <Filter> 扩展实体类，对日期类型需要扩展Before字段和After 字段
 */
public interface LikeMapper<Model, Filter> {
    /**
     * 过滤(模糊)查询
     * @param filter
     * @return
     */
    List<Model> selectModelByFilterLike(@Param("filter") Filter filter);

}
