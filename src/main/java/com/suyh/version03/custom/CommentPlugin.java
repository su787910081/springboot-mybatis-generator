package com.suyh.version03.custom;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 添加片注释的专门插件
 */
public class CommentPlugin extends PluginAdapter {

    // 所有支持的注解，这个注解在配置文件中使用。要与这些字符串做比较。
    private static final String PRO_AUTHOR = "author";

    private final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String author = "";

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        // 设置自定义的注释生成器: GenCommentGenerator
        // 注释生成器
        CommentGeneratorConfiguration commentCfg = new CommentGeneratorConfiguration();
        commentCfg.setConfigurationType(GenCommentGenerator.class.getCanonicalName());
        context.setCommentGeneratorConfiguration(commentCfg);
    }

    // validate方法调用，该方法一般用于验证传给参数的正确性，如果该方法返回false，则该插件结束执行
    @Override
    public boolean validate(List<String> list) {
        // 这里需要打开，如果返回false ，则类注解和注释都生成不了。不知道是怎么走的流程。
        return true;
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

        String defaultValue = System.getProperties().getProperty("user.name");
        author = (String) properties.getOrDefault(PRO_AUTHOR, defaultValue);
    }

    /**
     * suyh: 在类上面添加注解，以及添加注释.
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

        // 下面是添加类注释
        topLevelClass.addJavaDocLine("/**");

        if (StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for (String remarkLine : remarkLines) {
                topLevelClass.addJavaDocLine(" * " + remarkLine);
            }
        }

        StringBuilder sb = new StringBuilder();
        topLevelClass.addJavaDocLine(" * ");
        sb.append(" * @table: ").append(introspectedTable.getFullyQualifiedTable());
        topLevelClass.addJavaDocLine(sb.toString());
        sb.setLength(0);
        sb.append(" * @author: ").append(author);
        topLevelClass.addJavaDocLine(sb.toString());
        sb.setLength(0);
        sb.append(" * @date: ");
        sb.append(getDateString());
        topLevelClass.addJavaDocLine(sb.toString());
        topLevelClass.addJavaDocLine(" */");
        return true;
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
    public boolean modelFieldGenerated(
            Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
            IntrospectedTable introspectedTable, ModelClassType modelClassType) {

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn,
                introspectedTable, modelClassType);
    }

    protected String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return sdf.format(new Date());
    }

}
