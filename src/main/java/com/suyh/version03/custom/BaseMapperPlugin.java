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

public class BaseMapperPlugin extends PluginAdapter {

    private static final String PRO_MAPPERS = "mappers";

    private final Set<String> mappers = new HashSet<>();

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
     * 这里拿到的是  plugin 标签下面的 property 标签下面的属性数据
     * 所以我们可以在这里添加配置属性，比如我们要添加哪些注解。
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        String mappers = this.properties.getProperty(PRO_MAPPERS);
        Collections.addAll(this.mappers, mappers.split(","));
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        // 设置 xml文件时覆盖写
        // 默认：true [mybatis generator默认采用追加方式生成]
        sqlMap.setMergeable(false);
        return super.sqlMapGenerated(sqlMap, introspectedTable);
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



}
