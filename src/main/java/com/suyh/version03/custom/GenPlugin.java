package com.suyh.version03.custom;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;

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
            for (String im : ann.getImportEntities()) {
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
     * 生成的Mapper接口类
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
     * 是否要生成，通过主键删除记录。id: deleteByPrimaryKey
     *
     * @param element
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    /**
     * 是否生成插入mapper, id: insert
     *
     * @param element
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }

    /**
     * 是否要生成 mapper, id: selectByPrimaryKey
     * 如果返回false 则不会生成对应的SQL以及mapper
     *
     * @param element
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    /**
     * 是否要生成 mapper, id: selectAll
     * 如果返回false 则不会生成对应的SQL以及mapper
     *
     * @param element
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return super.sqlMapSelectAllElementGenerated(element, introspectedTable);
    }


    /**
     * id: updateByPrimaryKey
     *
     * @param element
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    /**
     * 拼装SQL语句生成Mapper接口映射文件
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        sqlMapSelectModelByFilterGenerated(document, introspectedTable);
        sqlMapUpdateModelByFilterGenerated(document, introspectedTable);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }


    /**
     * 自定义的，生成SQL, id: selectByFilter
     * @param document
     * @param introspectedTable
     */
    private void sqlMapSelectModelByFilterGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement rootElement = document.getRootElement();

        String baseResultMapId = introspectedTable.getBaseResultMapId();
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();

        StringBuilder allColumnName = new StringBuilder();
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (int i = 0; i < allColumns.size(); i++) {
            IntrospectedColumn introspectedColumn = allColumns.get(i);
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if (i != 0) {
                allColumnName.append(", ");
            }
            allColumnName.append(columnName);
        }

        // where 标签中的条件判断语句，过滤条件。用于更新、删除时作为过滤条件使用。
        XmlElement whereFilterElement = makeWhereFilterElement(introspectedTable);

        // select 标签，新建，并直接添加到root 中
        XmlElement selectByFilterElement = new XmlElement("select");
        selectByFilterElement.addAttribute(new Attribute("id", "selectModelByFilter"));
        selectByFilterElement.addAttribute(new Attribute("resultMap", baseResultMapId));
        selectByFilterElement.addElement(new TextElement("SELECT " + allColumnName));
        selectByFilterElement.addElement(new TextElement("FROM " + tableName));
        selectByFilterElement.addElement(whereFilterElement);
        rootElement.addElement(selectByFilterElement);
    }

    /**
     * 自定义的，生成SQL, id: updateModelByFilter
     * @param document
     * @param introspectedTable
     */
    private void sqlMapUpdateModelByFilterGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement rootElement = document.getRootElement();

        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();

        XmlElement ifModelElement = new XmlElement("if");
        ifModelElement.addAttribute(new Attribute("test", "null != model"));

        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : allColumns) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            String jdbcTypeName = introspectedColumn.getJdbcTypeName();

            XmlElement ifModelPropertyElement = new XmlElement("if");
            ifModelPropertyElement.addAttribute(new Attribute("test",
                    "null != model." + javaProperty));
            String content = String.format("%s = #{model.%s, jdbcType = %s},",
                    columnName, javaProperty, jdbcTypeName);
            ifModelPropertyElement.addElement(new TextElement(content));
            ifModelElement.addElement(ifModelPropertyElement);
        }

        // set 标签中的条件判断语句，过滤条件。用于更新
        XmlElement setModelElement = new XmlElement("set");    // where 标签
        setModelElement.addElement(ifModelElement);

        // where 标签中的条件判断语句，过滤条件。用于更新、删除时作为过滤条件使用。
        XmlElement whereFilterElement = makeWhereFilterElement(introspectedTable);

        // update 标签，新建，并直接添加到root 中
        XmlElement updateModelByFilterElement = new XmlElement("update");
        updateModelByFilterElement.addAttribute(new Attribute("id", "updateModelByFilter"));
        updateModelByFilterElement.addElement(new TextElement("UPDATE  " + tableName));
        updateModelByFilterElement.addElement(setModelElement);
        updateModelByFilterElement.addElement(whereFilterElement);
        rootElement.addElement(updateModelByFilterElement);
    }

    /**
     * 生成一个 where 标签，用于指定filter 过滤器。添加if 判断条件。
     * @param introspectedTable
     * @return
     */
    XmlElement makeWhereFilterElement(IntrospectedTable introspectedTable) {
        XmlElement ifWhereFilterElement = new XmlElement("if");
        ifWhereFilterElement.addAttribute(new Attribute("test", "null != filter"));

        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : allColumns) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            String jdbcTypeName = introspectedColumn.getJdbcTypeName();
            FullyQualifiedJavaType javaType = introspectedColumn.getFullyQualifiedJavaType();

            String ifString = "";   // 字符串类型要多处理一个''
            if (PrimitiveTypeWrapper.getStringInstance().equals(javaType)) {
                // 字符串才会做 空字符串的判断。其他类型不能做这个比较。否则会报错
                ifString = String.format("and '' != model.%s", javaProperty);
            }

            XmlElement ifFilterPropertyElement = new XmlElement("if");
            String ifPropertyValue = String.format("null != filter.%s %s ", javaProperty, ifString);
            ifFilterPropertyElement.addAttribute(new Attribute("test", ifPropertyValue));
            String content = String.format("AND %s = #{filter.%s, jdbcType = %s}",
                    columnName, javaProperty, jdbcTypeName);
            ifFilterPropertyElement.addElement(new TextElement(content));
            ifWhereFilterElement.addElement(ifFilterPropertyElement);
        }

        // where 标签中的条件判断语句，过滤条件。用于更新、删除时作为过滤条件使用。
        XmlElement whereFilterElement = new XmlElement("where");    // where 标签
        whereFilterElement.addElement(ifWhereFilterElement);

        return whereFilterElement;
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
