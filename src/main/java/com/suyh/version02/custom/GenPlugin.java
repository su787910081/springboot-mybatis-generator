package com.suyh.version02.custom;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
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
            topLevelClass.addAnnotation("@ApiModel(value = \"" + remarks + "\")");
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
        // 之前 table 配置 保留了 一个SelectByPrimaryKey 设置为true 此处删除
        List<Element> list = rootElement.getElements();
        while (list.size() > 1) {
            // 仅保留自动生成的 <resultMap> 标签，其他的 select insert update delete 全部删除。
            list.remove(list.size() - 1);
        }

        // 数据库表名
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        // 数据库全部列名
        StringBuilder columns = new StringBuilder();
        // where 子句中的条件判断语句，过滤条件。用于更新、删除时作为过滤条件使用。
        StringBuilder whereFilterIfSql = new StringBuilder();
        // update 子句的条件判断语句
        StringBuilder updateModelIfSql = new StringBuilder();

        // 插入的model 属性
        StringBuilder insertModelSql = new StringBuilder();

        // 数据库字段名
        String columnName = null;
        // java字段名
        String javaProperty = null;
        int colNum = 0;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            ++colNum;
            columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            javaProperty = introspectedColumn.getJavaProperty();

            String jdbcTypeName = introspectedColumn.getJdbcTypeName();
            FullyQualifiedJavaType javaType = introspectedColumn.getFullyQualifiedJavaType();

            // 拼接字段
            String sep = "";
            if (colNum != 1) {
                sep = ",";
            }
            columns.append(sep).append(columnName);


            // 拼接SQL
            // #{model.customerId}
            insertModelSql.append(String.format("    %s#{model.%s, jdbcType = %s}\n",
                    sep, javaProperty, jdbcTypeName));

            String ifString = "";   // 字符串类型要多处理一个''
            if (PrimitiveTypeWrapper.getStringInstance().equals(javaType)) {
                // 字符串才会做 空字符串的判断。其他类型不能做这个比较。否则会报错
                ifString = String.format("and '' != model.%s", javaProperty);
            }
            whereFilterIfSql.append(String.format(
                    "        <if test=\"null != filter.%s %s \">" +
                            " and %s = #{filter.%s, jdbcType = %s}</if>\n",
                    javaProperty, ifString, columnName, javaProperty, jdbcTypeName));

            // update 更新语句的 if 条件判断语句  如果更新的时候给了一个空字符串，那么数据一样会被修改
            // <if test="null != model.customerId">, CUSTOMER_ID = #{model.customerId, jdbcType=VARCHAR}</if>
            updateModelIfSql.append(String.format(
                    "        <if test=\"null != model.%s\">%s = #{model.%s, jdbcType = %s}, </if>\n",
                    javaProperty, columnName, javaProperty, jdbcTypeName));
        }
        rootElement.addElement(createSql("sqlColumns", columns.toString()));
        String whereSQL = String.format("<where>\n" +
                "      <if test=\"null != filter\">\n" +
                "%s" +
                "      </if>\n" +
                "    </where>", whereFilterIfSql.toString());
        rootElement.addElement(createSql("sqlWhereFilter", whereSQL));
        rootElement.addElement(createSql("sqlInsertModel", insertModelSql.toString()));
        rootElement.addElement(createSelectByFilter("selectByFilter", tableName));
        rootElement.addElement(createInsertModel("insertModel", tableName));
//        rootElement.addElement(createSelect("selectById", tableName, pkColumn));
//        rootElement.addElement(createSelect("selectOne", tableName, null));
//        rootElement.addElement(createSelect("selectList", tableName, null));
//        rootElement.addElement(createSelect("selectPage", tableName, null));
//        rootElement.addElement(createSql("sql_save_columns",
//                String.format("INSERT INTO %s(%s) values", tableName, columns.toString())));
//        rootElement.addElement(createSql("sql_save_values",
//                String.format("(\n%s)\n", saveValue.toString())));
//        rootElement.addElement(createSave("save", pkColumn));
//        rootElement.addElement(createSave("batchSave", null));

        /**
         * update sys_users
         * <set>
         * 	<if test="model != null">
         * 	   <if test="username != null and username != ''">
         * 	   	username=#{username},
         * 	   </if>
         * 	   <if test="email != null and email != ''">
         * 	   	email=#{email},
         * 	   </if>
         * 	   <if test="modifiedUser != null and modifiedUser != ''">
         * 	   	modifiedUser=#{modifiedUser},
         * 	   </if>
         * 	   <if test="mobile != null and mobile != ''">
         * 	   	mobile=#{mobile},
         * 	   </if>
         * 	   modifiedTime=now()
         * 	</if>
         * </set>
         * where id=#{id}
         */

        rootElement.addElement(createUpdateModel(updateModelIfSql.toString(), tableName));


//        rootElement.addElement(createUpdate("update"));
//        rootElement.addElement(createUpdate("batchUpdate"));
//        rootElement.addElement(createDels(tableName, pkColumn, "delArray", "array"));
//        rootElement.addElement(createDels(tableName, pkColumn, "delList", "list"));

        return super.sqlMapDocumentGenerated(document, introspectedTable);

    }

    /**
     * 创建更新通过model
     * @param updateIfSql
     * @return
     */
    private Element createUpdateModel(String updateIfSql, String tableName) {
        XmlElement insertElement = new XmlElement("update");
        insertElement.addAttribute(new Attribute("id", "updateModel"));
        String strSql =
                String.format("UPDATE %s \n", tableName) +
                        "    <set>\n" +
                        "      <if test=\"model != null\">\n" +
                        updateIfSql +
                        "      </if>\n" +
                        "    </set>\n" +
                        "    <include refid=\"sqlWhereFilter\"/>\n";

        insertElement.addElement(new TextElement(strSql));
        return insertElement;
    }

    /**
     * 创建插入一条实体的SQL 标签
     * @param insertModel
     * @param tableName
     * @return
     */
    private Element createInsertModel(String insertModel, String tableName) {
        XmlElement insertElement = new XmlElement("insert");
        insertElement.addAttribute(new Attribute("id", insertModel));
        String str = String.format("INSERT INTO %s(<include refid=\"sqlColumns\" />) \n" +
                "      VALUES(<include refid=\"sqlInsertModel\"/>)", tableName);
        insertElement.addElement(new TextElement(str));
        return insertElement;
    }

    /**
     * 创建一条SQL，用于查询。通过过滤条件查询。
     * @param selectId
     * @param tableName
     * @return
     */
    private Element createSelectByFilter(String selectId, String tableName) {
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", selectId));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));

        String selectSb = "SELECT <include refid=\"sqlColumns\" />\n" +
                String.format("    FROM %s \n", tableName) +
                "    <include refid=\"sqlWhereFilter\" />\n";
        select.addElement(new TextElement(selectSb));
        return select;
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
        StringBuilder selectStr = new StringBuilder("select <include refid=\"sqlColumns\" /> from ")
                .append(tableName);
        if (null != pkColumn) {
            selectStr.append(" where ")
                    .append(pkColumn.getActualColumnName())
                    .append(" = #{")
                    .append(pkColumn.getJavaProperty())
                    .append("}");
        } else {
            selectStr.append(" <include refid=\"sqlWhereFilter\" />");
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
            save.addAttribute(new Attribute("keyProperty", "model." + pkColumn.getJavaProperty()));
            save.addAttribute(new Attribute("useGeneratedKeys", "true"));
            save.addElement(new TextElement("<include refid=\"sql_save_columns\" /><include refid=\"sql_save_values\" />"));
        } else {
            StringBuilder saveStr = new StringBuilder(
                    "<foreach collection=\"list\" index=\"index\" model=\"model\" open=\"\" separator=\";\" close=\"\">\n    ")
                    .append("<include refid=\"sql_save_columns\" /><include refid=\"sql_save_values\" />\n  </foreach>");
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
                    "<foreach collection=\"list\" index=\"index\" model=\"model\" open=\"\" " +
                            "separator=\";\" close=\"\">\n   " +
                            " <include refid=\"sql_update\" />\n  </foreach>"));
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
                .append(" in\n  ")
                .append("<foreach collection=\"")
                .append(type)
                .append("\" index=\"index\" model=\"model\" open=\"(\" separator=\",\" close=\")\">#{model}</foreach>");
        delete.addElement(new TextElement(deleteStr.toString()));
        return delete;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        // 设置 xml文件时覆盖写
        // 默认：true [mybatis generator默认采用追加方式生成]
        sqlMap.setMergeable(false);
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }

    protected String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return sdf.format(new Date());
    }

}
