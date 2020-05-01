package com.suyh.version02.custom;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
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

    // 所有支持的注解，这个注解在配置文件中使用。要与这些字符串做比较。
    private final static String ANN_SWAGGER = "swagger";
    private final static String ANN_DATE = "format_date";
    private static final String PRO_AUTHOR = "author";
    private static final String PRO_DATE_FORMAT = "dateFormat";
    private static final String PRO_MAPPERS = "mappers";

    private final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final Set<String> mappers = new HashSet<>();

    private String dateFormat = "";
    private String author = "";

    // 配置了，需要添加的注解
    private final Set<AnnotationEnum> annotations = new HashSet<>();


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

        String mappers = this.properties.getProperty(PRO_MAPPERS);
        for (String mapper : mappers.split(",")) {
            this.mappers.add(mapper);
        }

        String defaultValue = System.getProperties().getProperty("user.name");
        author = (String) properties.getOrDefault(PRO_AUTHOR, defaultValue);
        dateFormat = (String) properties.getOrDefault(PRO_DATE_FORMAT, DEFAULT_DATE_FORMAT);

        // 初始化支持的所有注解
        String bEnable = null;
        bEnable = (String) properties.getOrDefault(ANN_DATE, "false");
        if ("true".equals(bEnable)) {
            annotations.add(AnnotationEnum.JSON_DATE);
        }
        bEnable = (String) properties.getOrDefault(ANN_SWAGGER, "false");
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
            for (String im : ann.getImportEntitys()) {
                topLevelClass.addImportedType(im);
            }
        }
        // 类上面要添加的注解
        if (annotations.contains(AnnotationEnum.SWAGGER)) {
            topLevelClass.addAnnotation("@ApiModel(value = \"" +remarks + "\")");
        }

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
//        interfaze.addImportedType(new FullyQualifiedJavaType(
//                "org.apache.ibatis.annotations.Mapper"));
//        interfaze.addAnnotation("@Mapper");

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
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return sdf.format(new Date());
    }

}
