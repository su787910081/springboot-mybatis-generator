package com.suyh.version02.custom;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenPlugin extends PluginAdapter {

    // 注释生成器
    private CommentGeneratorConfiguration commentCfg;
    private Set<String> mappers = new HashSet<String>();

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        // 设置默认的注释生成器
        commentCfg = new CommentGeneratorConfiguration();
        commentCfg.setConfigurationType(GenCommentGenerator.class.getCanonicalName());
        context.setCommentGeneratorConfiguration(commentCfg);
        // 支持oracle获取注释#114
        context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");
    }

    // validate方法调用，该方法一般用于验证传给参数的正确性，如果该方法返回false，则该插件结束执行
    @Override
    public boolean validate(List<String> list) {
        // 这里需要打开
        return true;
    }

    @Override
    public void setProperties(Properties properties) {

        super.setProperties(properties);
        String mappers = this.properties.getProperty("mappers");
        for (String mapper : mappers.split(",")) {
            this.mappers.add(mapper);
        }
    }

    /**
     * suyh: 在类上面添加注解，以及添加注释.
     * 这里添加的是一个 @Data 的注解
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 添加要 import 的实体对象
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
        // 添加在类上面的注解
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@ApiModel");

        // 下面是添加类注释
        topLevelClass.addJavaDocLine("/**");

        String remarks = introspectedTable.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for (String remarkLine : remarkLines) {
                topLevelClass.addJavaDocLine(" * " + remarkLine);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(" * ").append(introspectedTable.getFullyQualifiedTable());
        topLevelClass.addJavaDocLine(sb.toString());
        sb.setLength(0);
        sb.append(" * @author ").append(System.getProperties().getProperty("user.name"));
        topLevelClass.addJavaDocLine(sb.toString());
        sb.setLength(0);
        sb.append(" * @date ");
        sb.append(getDateString());
        topLevelClass.addJavaDocLine(sb.toString());
        topLevelClass.addJavaDocLine(" */");
        return true;
    }

    /**
     * 生成的Mapper接口
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */

    @Override
    public boolean clientGenerated(
            Interface interfaze, TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        // 获取实体类
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
        // import接口
        for (String mapper : mappers) {
            interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(
                    mapper + "<" + entityType.getShortName() + ">"));
        }
        // import实体类
        interfaze.addImportedType(entityType);

        // suyh: 新添加，看类头上会不会有这个。
        // 这里似乎添加到mapper 接口文件上面去了。
        interfaze.addImportedType(new FullyQualifiedJavaType(
                "org.apache.ibatis.annotations.Mapper"));
        interfaze.addAnnotation("@Mapper");

        return true;
    }

    /**
     * 拼装SQL语句生成Mapper接口映射文件
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement rootElement = document.getRootElement();
        // 数据库表名
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        // 主键
        IntrospectedColumn pkColumn = introspectedTable.getPrimaryKeyColumns().get(0);
        // 公共字段
        StringBuilder columnSQL = new StringBuilder();
        // IF判断语句
        StringBuilder ifSQL = new StringBuilder();
        // 要插入的字段(排除自增主键)
        StringBuilder saveColumn = new StringBuilder("insert into ").append(tableName).append("(\n");
        // 要保存的值
        StringBuilder saveValue = new StringBuilder("(\n");
        // 拼装更新字段
        StringBuilder updateSQL = new StringBuilder("update ")
                .append(tableName).append(" set ")
                .append(pkColumn.getActualColumnName())
                .append(" = #{item.")
                .append(pkColumn.getJavaProperty())
                .append("}\n");

        // 数据库字段名
        String columnName = null;
        // java字段名
        String javaProperty = null;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            javaProperty = introspectedColumn.getJavaProperty();
            // 拼接字段
            columnSQL.append(columnName).append(",");
            // 拼接IF语句
            ifSQL.append("      <if test=\"null != item.")
                    .append(javaProperty).append(" and '' != item.")
                    .append(javaProperty)
                    .append("\">");
            ifSQL.append("and ")
                    .append(columnName)
                    .append(" = #{item.")
                    .append(javaProperty)
                    .append("}</if>\n");
            // 拼接SQL
            if (!introspectedColumn.isAutoIncrement()) {
                saveColumn.append("\t  <if test=\"null != item.")
                        .append(javaProperty)
                        .append("\">, ")
                        .append(columnName)
                        .append("</if>\n");
                saveValue.append("\t  <if test=\"null != item.")
                        .append(javaProperty).append("\">, ")
                        .append("#{item.")
                        .append(javaProperty)
                        .append("}</if>\n");
                updateSQL.append("      <if test=\"null != item.")
                        .append(javaProperty)
                        .append("\">");
                updateSQL.append(", ")
                        .append(columnName)
                        .append(" = #{item.")
                        .append(javaProperty)
                        .append("}</if>\n");
            }
        }
        String columns = columnSQL.substring(0, columnSQL.length() - 1);
        rootElement.addElement(createSql("sql_columns", columns));
        String whereSQL = MessageFormat.format("<where>\n{0}\t</where>", ifSQL.toString());
        rootElement.addElement(createSql("sql_where", whereSQL));
        rootElement.addElement(createSelect("selectById", tableName, pkColumn));
        rootElement.addElement(createSelect("selectOne", tableName, null));
        rootElement.addElement(createSelect("selectList", tableName, null));
        rootElement.addElement(createSelect("selectPage", tableName, null));
        rootElement.addElement(createSql("sql_save_columns",
                saveColumn.append("\t) values").toString().replaceFirst(",", "")));
        rootElement.addElement(createSql("sql_save_values",
                saveValue.append("\t)").toString().replaceFirst(",", "")));
        rootElement.addElement(createSave("save", pkColumn));
        rootElement.addElement(createSave("batchSave", null));
        updateSQL.append("\twhere ")
                .append(pkColumn.getActualColumnName())
                .append(" = #{item.")
                .append(pkColumn.getJavaProperty())
                .append("}");
        rootElement.addElement(createSql("sql_update", updateSQL.toString()));
        rootElement.addElement(createUpdate("update"));
        rootElement.addElement(createUpdate("batchUpdate"));
        rootElement.addElement(createDels(tableName, pkColumn, "delArray", "array"));
        rootElement.addElement(createDels(tableName, pkColumn, "delList", "list"));

        return super.sqlMapDocumentGenerated(document, introspectedTable);

    }

    /**
     * 公共SQL
     *
     * @param id
     * @param sqlStr
     * @return
     */
    private XmlElement createSql(String id, String sqlStr) {
        XmlElement sql = new XmlElement("sql");
        sql.addAttribute(new Attribute("id", id));
        sql.addElement(new TextElement(sqlStr));
        return sql;
    }

    /**
     * 查询
     *
     * @param id
     * @param tableName
     * @param pkColumn
     * @return
     */
    private XmlElement createSelect(String id, String tableName, IntrospectedColumn pkColumn) {
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", id));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        StringBuilder selectStr = new StringBuilder("select <include refid=\"sql_columns\" /> from ")
                .append(tableName);
        if (null != pkColumn) {
            selectStr.append(" where ")
                    .append(pkColumn.getActualColumnName())
                    .append(" = #{")
                    .append(pkColumn.getJavaProperty())
                    .append("}");
        } else {
            selectStr.append(" <include refid=\"sql_where\" />");
        }
        if ("selectPage".equals(id)) {
            selectStr.append(" limit #{page.startRow}, #{page.pageSize}");
        }
        select.addElement(new TextElement(selectStr.toString()));
        return select;
    }

    /**
     * 保存
     *
     * @param id
     * @param pkColumn
     * @return
     */
    private XmlElement createSave(String id, IntrospectedColumn pkColumn) {
        XmlElement save = new XmlElement("insert");
        save.addAttribute(new Attribute("id", id));
        if (null != pkColumn) {
            save.addAttribute(new Attribute("keyProperty", "item." + pkColumn.getJavaProperty()));
            save.addAttribute(new Attribute("useGeneratedKeys", "true"));
            save.addElement(new TextElement("<include refid=\"sql_save_columns\" /><include refid=\"sql_save_values\" />"));
        } else {
            StringBuilder saveStr = new StringBuilder(
                    "<foreach collection=\"list\" index=\"index\" item=\"item\" open=\"\" separator=\";\" close=\"\">\n\t  ")
                    .append("<include refid=\"sql_save_columns\" /><include refid=\"sql_save_values\" />\n\t</foreach>");
            save.addElement(new TextElement(saveStr.toString()));
        }
        return save;
    }

    /**
     * 更新
     *
     * @param id
     * @return
     */
    private XmlElement createUpdate(String id) {
        XmlElement update = new XmlElement("update");
        update.addAttribute(new Attribute("id", id));
        if ("update".equals(id)) {
            update.addElement(new TextElement("<include refid=\"sql_update\" />"));
        } else {
            update.addElement(new TextElement(
                    "<foreach collection=\"list\" index=\"index\" item=\"item\" open=\"\" " +
                            "separator=\";\" close=\"\">\n\t " +
                            " <include refid=\"sql_update\" />\n\t</foreach>"));
        }
        return update;
    }

    /**
     * 删除
     *
     * @param tableName
     * @param pkColumn
     * @param method
     * @param type
     * @return
     */
    private XmlElement createDels(String tableName, IntrospectedColumn pkColumn, String method, String type) {
        XmlElement delete = new XmlElement("delete");
        delete.addAttribute(new Attribute("id", method));
        StringBuilder deleteStr = new StringBuilder("delete from ")
                .append(tableName)
                .append(" where ")
                .append(pkColumn.getActualColumnName())
                .append(" in\n\t")
                .append("<foreach collection=\"")
                .append(type)
                .append("\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach>");
        delete.addElement(new TextElement(deleteStr.toString()));
        return delete;
    }

    protected String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

}
