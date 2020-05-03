package com.suyh.version03.plugin.custom.annotation;

import java.util.Arrays;
import java.util.List;

/**
 * 插入注解时所需要的信息
 */
public enum AnnotationEnum {
//    MAPPER("添加@Mapper 注解", Arrays.asList("org.apache.ibatis.annotations.Mapper")),
    SWAGGER("添加swagger 相关注解", Arrays.asList("io.swagger.annotations.ApiModel",
            "io.swagger.annotations.ApiModelProperty")),
    JSON_DATE("添加@JsonFormat 注解", Arrays.asList("com.fasterxml.jackson.annotation.JsonFormat")),
    ;

    /**
     * 导入实体类字符串
     */
    private final List<String> importEntities;

    /**
     * 描述
     */
    private String desc;

    AnnotationEnum(String desc, List<String> importEntities) {
        this.desc = desc;
        this.importEntities = importEntities;
    }

    public List<String> getImportEntities() {
        return importEntities;
    }
}
