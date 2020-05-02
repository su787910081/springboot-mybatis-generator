package com.suyh.version03.plugin.custom;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class AnnotationPlugin extends PluginAdapter {

    private String dateFormat = "";

    // 配置文件中配置好的需要添加的注解
    private final Set<AnnotationEnum> annotations = new HashSet<>();

    @Override
    public void setContext(Context context) {
        super.setContext(context);
    }

    // validate方法调用，该方法一般用于验证传给参数的正确性，如果该方法返回false，则该插件结束执行
    @Override
    public boolean validate(List<String> list) {
        // 这里需要打开，如果返回false ，则类注解和注释都生成不了。不知道是怎么走的流程。
        return true;
    }

    /**
     * 初始化
     * @param introspectedTable
     */
    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);

        Context context = introspectedTable.getContext();
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = context.getProperty(Constans.PRO_DATE_FORMAT);
        }
    }

    /**
     * 这里拿到的是  plugin 标签下面的 property 标签下面的属性数据
     * 所以我们可以在这里添加配置属性，比如我们要添加哪些注解。
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        if (!StringUtils.isEmpty(dateFormat)) {
            dateFormat = (String) properties.getOrDefault(Constans.ANN_DATE, Constans.DEFAULT_DATE_FORMAT);
        }

        // 初始化支持的所有注解
        String bEnable = null;
        bEnable = (String) properties.getOrDefault(Constans.ANN_DATE, "false");
        if ("true".equals(bEnable)) {
            annotations.add(AnnotationEnum.JSON_DATE);
        }
        bEnable = (String) properties.getOrDefault(Constans.ANN_SWAGGER, "false");
        if ("true".equals(bEnable)) {
            annotations.add(AnnotationEnum.SWAGGER);
        }
    }

    /**
     * suyh: 在类上面添加注解，以及添加注释.
     * 这里添加的是一个 @Data 的注解
     * 这里应该是要将所有需要 import 的都在这里做处理。
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 表注释
        String remarks = introspectedTable.getRemarks();

        // 添加要 import 的实体对象
        for (AnnotationEnum ann : annotations) {
            for (String im : ann.getImportEntities()) {
                topLevelClass.addImportedType(im);
            }
        }
        // 类上面要添加的注解
        if (annotations.contains(AnnotationEnum.SWAGGER)) {
            topLevelClass.addAnnotation("@ApiModel(value = \"" + remarks + "\")");
        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * 字段处理，在这里可以添加字段的注解，注释等
     * 但是需要 validate() 返回true
     *
     * @param field
     * @param topLevelClass
     * @param introspectedColumn
     * @param introspectedTable
     * @param modelClassType
     * @return
     */
    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        // 字段注释
        String remarks = introspectedColumn.getRemarks();
        if (remarks == null) {
            remarks = "";
        }

        // 日期处理，添加注释与注解
        String dateRemark = null;
        FullyQualifiedJavaType type = field.getType();
        if (type.equals(FullyQualifiedJavaType.getDateInstance())) {

            if (annotations.contains(AnnotationEnum.JSON_DATE)) {
                // 没有注解，这个格式也没用。
                dateRemark = "【格式：" + dateFormat + "】";

                // 日期类我们要添加时间的序列化注解
                String dateJsonFormat = "@JsonFormat(pattern = \""
                        + dateFormat + "\", timezone = \"GMT+8\")";
                field.addAnnotation(dateJsonFormat);
            }
        }

        if (annotations.contains(AnnotationEnum.SWAGGER)) {
            StringBuilder sbAnnotation = new StringBuilder();
            sbAnnotation.append("@ApiModelProperty(");
            sbAnnotation.append("value = ").append('"').append(remarks);
            sbAnnotation.append(dateRemark == null ? "" : dateRemark);
            sbAnnotation.append('"').append(")");

            field.addAnnotation(sbAnnotation.toString());
        }

        return true;
    }



}
