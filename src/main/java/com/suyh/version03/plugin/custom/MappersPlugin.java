package com.suyh.version03.plugin.custom;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * mapper 插件由我现在的理解，有且仅有一个为好。
 * 如果有两个的话，那一个表生成两个mapper 接口文件吗？
 * 这样似乎不行的呀。
 */
public class MappersPlugin extends PluginAdapter {

    private static final String PRO_MAPPERS = "mappers";

    private String dateFormat = Constants.DEFAULT_DATE_FORMAT;
    private String javaFileEncoding;

    private static final FullyQualifiedJavaType simpleMapperType;
    private static final FullyQualifiedJavaType filterMapperType;

    static {
        simpleMapperType = new FullyQualifiedJavaType(SimpleMapper.class.getCanonicalName());
        filterMapperType = new FullyQualifiedJavaType(LikeMapper.class.getCanonicalName());
    }

    // 配置文件中配置好的需要添加的注解
    private final Set<AnnotationEnum> annotations = new HashSet<>();

    private final Set<FullyQualifiedJavaType> mapperTypes = new HashSet<>();
    // 过滤器实体，模糊查询时必须要true
    private boolean filterEntity = false;

    @Override
    public void setContext(Context context) {
        super.setContext(context);
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);

        Context context = introspectedTable.getContext();
        javaFileEncoding = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING);
        String proDateFormat = context.getProperty(Constants.PRO_DATE_FORMAT);
        if (StringUtils.isEmpty(proDateFormat)) {
            dateFormat = proDateFormat;
        }
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

        String mappers = (String) this.properties.getOrDefault(PRO_MAPPERS, "");
        for (String m : mappers.split(",")) {
            if (!StringUtils.isEmpty(m)) {
                mapperTypes.add(new FullyQualifiedJavaType(m));
            }
        }
        if (mapperTypes.contains(filterMapperType)) {
            // 模糊查询时必须要实现这个实体类
            filterEntity = true;
        }
        String bEnable = (String) properties.getOrDefault(Constants.ANN_SWAGGER, "false");
        if ("true".equals(bEnable)) {
            annotations.add(AnnotationEnum.SWAGGER);
        }
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
        String modelName = introspectedTable.getBaseRecordType();
        FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(modelName);
        String shortModelName = modelType.getShortName();

        // import实体类
        interfaze.addImportedType(modelType);

        if (mapperTypes.contains(simpleMapperType)) {
            // 基本Mapper 接口类
            String shortName = simpleMapperType.getShortName();
            interfaze.addSuperInterface(new FullyQualifiedJavaType(
                    shortName + "<" + shortModelName + ">"));
            interfaze.addImportedType(simpleMapperType);
        }
        if (mapperTypes.contains(filterMapperType)) {
            // 过滤器Mapper 接口类
            String filterName = getFilterClassShortName(modelName);
            FullyQualifiedJavaType filterType = new FullyQualifiedJavaType(filterName);
            String shortFilterName = filterType.getShortName();

            String shortName = filterMapperType.getShortName();
            String superClass = String.format("%s<%s, %s>",
                    shortName, shortModelName, shortFilterName);
            interfaze.addSuperInterface(new FullyQualifiedJavaType(superClass));
            interfaze.addImportedType(filterType);
            interfaze.addImportedType(filterMapperType);
        }

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
     * 生成Mapper接口映射文件
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        if (mapperTypes.contains(simpleMapperType)) {
            // 创建 SimpleMapper 中定义的接口所对应的SQL
            sqlMapSelectModelByFilterGenerated(document, introspectedTable);
            sqlMapUpdateModelByFilterGenerated(document, introspectedTable);
        }
        if (mapperTypes.contains(filterMapperType)) {
            sqlMapSelectByFilterLikeGenerated(document, introspectedTable);
        }

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * 实现自己的Java 文件，扩展
     *
     * @param introspectedTable
     * @return
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(
            IntrospectedTable introspectedTable) {

        Context context = introspectedTable.getContext();

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration
                = context.getJavaModelGeneratorConfiguration();
        String targetPackage = javaModelGeneratorConfiguration.getTargetPackage();
        String targetProject = javaModelGeneratorConfiguration.getTargetProject();

        List<GeneratedJavaFile> list = new ArrayList<>();

        List<CompilationUnit> javaClass = new ArrayList<>();
        if (filterEntity) {
            // 创建filter 实体类对象
            javaClass.add(generateFilterEntity(
                    introspectedTable, targetPackage));
        }

        for (CompilationUnit unit : javaClass) {
            GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
                    unit, targetProject, javaFileEncoding, context.getJavaFormatter());
            list.add(generatedJavaFile);
        }

        return list;
    }

    /**
     * 自定义一个生成过滤器的一个实体类
     * 有问题可以找到工具实现的类 SimpleModelGenerator.getCompilationUnits() 的方法作参考
     * org.mybatis.generator.codegen.mybatis3.model.SimpleModelGenerator
     *
     * @param introspectedTable
     * @param basePackage
     * @return
     */
    private CompilationUnit generateFilterEntity(IntrospectedTable introspectedTable, String basePackage) {
        // 这里可以得到实体类的完整类名
        String entityClazzType = introspectedTable.getBaseRecordType();

        // 这里可以得到实体类的短名，仅类名：  CrmCustomerInfo
        String modelClassName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();

        // 父类类型，我这里直接使用实体类
        FullyQualifiedJavaType superClassType = new FullyQualifiedJavaType(entityClazzType);

        // 拼接完整类名
        String filterShortName = getFilterClassShortName(modelClassName);

        TopLevelClass filterClass = new TopLevelClass(
                String.format("%s.%s", basePackage, filterShortName));

        filterClass.setSuperClass(superClassType);
        filterClass.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType modelJavaType = new FullyQualifiedJavaType(entityClazzType);
        filterClass.addImportedType(modelJavaType);

        String remarksTable = introspectedTable.getRemarks();
        String remarkModel = remarksTable + " 模糊查询实体";
        if (annotations.contains(AnnotationEnum.SWAGGER)) {
            for (String str : AnnotationEnum.SWAGGER.getImportEntities()) {
                filterClass.addImportedType(str);
            }
            filterClass.addAnnotation("@ApiModel(value = \"" + remarkModel + "\")");
        }

        filterClass.addJavaDocLine("/**");
        filterClass.addJavaDocLine(" * " + remarkModel);
        filterClass.addJavaDocLine(" *");
        filterClass.addJavaDocLine(" * @author " + getCurUser());
        filterClass.addJavaDocLine(" * @date " + getDateString());
        filterClass.addJavaDocLine(" */");

        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : allColumns) {
            FullyQualifiedJavaType fullyQualifiedJavaType = introspectedColumn.getFullyQualifiedJavaType();
            if (fullyQualifiedJavaType.equals(FullyQualifiedJavaType.getDateInstance())) {
                String javaProperty = introspectedColumn.getJavaProperty();
                String remarks = introspectedColumn.getRemarks();
                // 添加两个字段，分别拼接 Before 和 After
                addDateFilterField(filterClass, javaProperty + "Before", remarks);
                addDateFilterField(filterClass, javaProperty + "After", remarks);
            }
        }

        return filterClass;
    }

    /**
     * 过滤器类，是在实体类的类名后面添加一个 Filter 字符串
     *
     * @param modelShortName
     * @return
     */
    private String getFilterClassShortName(String modelShortName) {
        if (StringUtils.isEmpty(modelShortName)) {
            return null;
        }

        return modelShortName + "Filter";
    }

    /**
     * 对日期类型的字段，添加过滤字段
     *
     * @param filterClass
     * @param fieldName
     */
    private void addDateFilterField(TopLevelClass filterClass, String fieldName, String remarks) {
        Field field = new Field(fieldName, FullyQualifiedJavaType.getDateInstance());

        // 这些要自己处理，其他插件是不会走这里的。
        // 日期类我们要添加时间的序列化注解
        String dateJsonFormat = "@JsonFormat(pattern = \""
                + dateFormat + "\", timezone = \"GMT+8\")";
        for (String entity : AnnotationEnum.JSON_DATE.getImportEntities()) {
            filterClass.addImportedType(entity);
        }
        field.setVisibility(JavaVisibility.PRIVATE);
        field.addAnnotation(dateJsonFormat);

        if (annotations.contains(AnnotationEnum.SWAGGER)) {
            // 添加Swagger 注解
            String strAnnotation = String.format(
                    "@ApiModelProperty(value = \"日期匹配左边界。%s\")", remarks);
            field.addAnnotation(strAnnotation);
        }

        filterClass.addField(field);
        filterClass.addImportedType(field.getType());
        filterClass.addMethod(makeJavaGetterMethod(field));
        filterClass.addMethod(makeJavaSetterMethod(field));
    }

    private static Method makeJavaSetterMethod(Field field) {
        String property = field.getName();
        FullyQualifiedJavaType fqjt = field.getType();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(JavaBeansUtil.getSetterMethodName(property));
        method.addParameter(new Parameter(fqjt, property));
        method.addBodyLine("this." + property + " = " + property + ';');

        return method;
    }

    private static Method makeJavaGetterMethod(Field field) {
        String property = field.getName();
        Method method = new Method();
        String getterBeforeMethodName = JavaBeansUtil.getGetterMethodName(
                property, field.getType());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.setName(getterBeforeMethodName);
        method.addBodyLine(String.format("return %s;", property));

        return method;
    }

    protected String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
        return sdf.format(new Date());
    }

    private static String getCurUser() {
        return System.getProperty("user.name");
    }

    /**
     * 自定义的，生成SQL, id: selectByFilter
     *
     * @param document
     * @param introspectedTable
     */
    private void sqlMapSelectModelByFilterGenerated(
            Document document, IntrospectedTable introspectedTable) {

        // where 标签中的条件判断语句，过滤条件。用于更新、删除时作为过滤条件使用。
        XmlElement whereFilterElement = makeQueryWhereFilterElement(introspectedTable);
        sqlMapSelectGenerated(document, introspectedTable,
                "selectModelByFilter", whereFilterElement);
    }

    /**
     * 自定义的，生成SQL, id: updateModelByFilter
     *
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
        XmlElement whereFilterElement = makeQueryWhereFilterElement(introspectedTable);

        // update 标签，新建，并直接添加到root 中
        XmlElement updateModelByFilterElement = new XmlElement("update");
        updateModelByFilterElement.addAttribute(new Attribute("id", "updateModelByFilter"));
        updateModelByFilterElement.addElement(new TextElement("UPDATE  " + tableName));
        updateModelByFilterElement.addElement(setModelElement);
        updateModelByFilterElement.addElement(whereFilterElement);
        rootElement.addElement(updateModelByFilterElement);
    }

    /**
     * 按指定where 条件生成一条查询SQL。
     *
     * @param document
     * @param introspectedTable
     * @param selectMapperId     sql id, 对应mapper 接口的方法名
     * @param whereFilterElement where 条件的Element，若为null 就是没有查询条件(即：全表)。
     */
    private void sqlMapSelectGenerated(Document document, IntrospectedTable introspectedTable,
                                       String selectMapperId, XmlElement whereFilterElement) {
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

        // select 标签，新建，并直接添加到root 中
        XmlElement selectByFilterElement = new XmlElement("select");
        selectByFilterElement.addAttribute(new Attribute("id", selectMapperId));
        selectByFilterElement.addAttribute(new Attribute("resultMap", baseResultMapId));
        selectByFilterElement.addElement(new TextElement("SELECT " + allColumnName));
        selectByFilterElement.addElement(new TextElement("FROM " + tableName));
        selectByFilterElement.addElement(whereFilterElement);
        rootElement.addElement(selectByFilterElement);
    }

    /**
     * 自定义，生成模糊查询SQL，id: selectByFilterLike
     *
     * @param document
     * @param introspectedTable
     */
    private void sqlMapSelectByFilterLikeGenerated(Document document, IntrospectedTable introspectedTable) {
        // where 标签中的条件判断语句，过滤条件。用于更新、删除时作为过滤条件使用。
        XmlElement whereLikeFilterElement = makeQueryLikeWhereFilterElement(introspectedTable);
        sqlMapSelectGenerated(document, introspectedTable,
                "selectModelByFilterLike", whereLikeFilterElement);
    }

    /**
     * 生成一个 where 标签，用于指定filter 过滤器。添加if 判断条件。
     * 这里的filter 是作为完全匹配查询的实体对象。
     *
     * @param introspectedTable
     * @return
     */
    XmlElement makeQueryWhereFilterElement(IntrospectedTable introspectedTable) {
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

    /**
     * 生成一个 where 标签，用于指定filter 过滤器。添加if 判断条件。
     * 这里的filter 是作为模糊查询条件的实体对象。
     *
     * @param introspectedTable
     * @return
     */
    XmlElement makeQueryLikeWhereFilterElement(IntrospectedTable introspectedTable) {
        XmlElement ifWhereFilterElement = new XmlElement("if");
        ifWhereFilterElement.addAttribute(new Attribute("test", "null != filter"));

        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : allColumns) {

            FullyQualifiedJavaType javaType = introspectedColumn.getFullyQualifiedJavaType();

            if (PrimitiveTypeWrapper.getStringInstance().equals(javaType)) {
                sqlMapQueryLikeByString(introspectedColumn, ifWhereFilterElement);
            } else if (PrimitiveTypeWrapper.getDateInstance().equals(javaType)) {
                sqlMapQueryLikeByDate(introspectedColumn, ifWhereFilterElement);
            } else {
                // 其他则完全匹配
                String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
                String javaProperty = introspectedColumn.getJavaProperty();
                String jdbcTypeName = introspectedColumn.getJdbcTypeName();

                XmlElement ifLikePropertyElement = new XmlElement("if");
                String ifPropertyValue = String.format("null != filter.%s", javaProperty);
                ifLikePropertyElement.addAttribute(new Attribute("test", ifPropertyValue));

                String content = String.format("AND %s = #{filter.%s, jdbcType = %s}",
                        columnName, javaProperty, jdbcTypeName);
                ifLikePropertyElement.addElement(new TextElement(content));
                ifWhereFilterElement.addElement(ifLikePropertyElement);
            }
        }

        // where 标签中的条件判断语句，过滤条件。用于更新、删除时作为过滤条件使用。
        XmlElement whereFilterElement = new XmlElement("where");    // where 标签
        whereFilterElement.addElement(ifWhereFilterElement);

        return whereFilterElement;
    }


    /**
     * 字符串类型的模糊查询，if 标签
     *
     * @param introspectedColumn 数据库中的某一列
     */
    private void sqlMapQueryLikeByString(
            IntrospectedColumn introspectedColumn, XmlElement ifWhereFilterElement) {
        String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
        String javaProperty = introspectedColumn.getJavaProperty();
        String jdbcTypeName = introspectedColumn.getJdbcTypeName();

        // 字符串字段的模糊查询。字符串的拼接要分mysql 和oracle了。
        XmlElement ifLikePropertyElement = new XmlElement("if");
        String ifPropertyValue = String.format(
                "null != filter.%s and '' != model.%s", javaProperty, javaProperty);
        ifLikePropertyElement.addAttribute(new Attribute("test", ifPropertyValue));

        String content = null;
        String dbSource = "oracle";
        if ("mysql".equals(dbSource)) {
            // TODO: mysql 还没有实现。模糊查询字符串拼接。
            throw new RuntimeException("mysql 还没有实现。模糊查询字符串拼接。");
        } else if ("oracle".equals(dbSource)) {
            content = String.format("AND %s LIKE '%%' || #{filter.%s, jdbcType = %s} || '%%'",
                    columnName, javaProperty, jdbcTypeName);
        } else {
            throw new RuntimeException("未知(未实现模糊查询)数据库类型: " + dbSource);
        }
        ifLikePropertyElement.addElement(new TextElement(content));

        ifWhereFilterElement.addElement(ifLikePropertyElement);
    }


    private void sqlMapQueryLikeByDate(
            IntrospectedColumn introspectedColumn, XmlElement ifWhereFilterElement) {
        // 约定：左闭右开, filter 类中必须要有对这个时间变量的扩展字段。
        //      并且扩展字段，分别要加上 Before 和After
        // 即: createdTime 有扩展字段  createdTimeBefore 和createdTimeAfter
        String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
        String javaProperty = introspectedColumn.getJavaProperty();
        String jdbcTypeName = introspectedColumn.getJdbcTypeName();

        // Before
        String javaPropertyBefore = javaProperty + "Before";
        String contentBefore = String.format("<![CDATA[  AND #{filter.%s, jdbcType = %s} <= %s  ]]>",
                javaPropertyBefore, jdbcTypeName, columnName);

        XmlElement ifLikePropertyElementBefore = new XmlElement("if");
        String ifPropertyValueBefore = String.format("null != filter.%s", javaPropertyBefore);
        ifLikePropertyElementBefore.addAttribute(new Attribute("test", ifPropertyValueBefore));
        ifLikePropertyElementBefore.addElement(new TextElement(contentBefore));

        // After
        String javaPropertyAfter = javaProperty + "After";
        String contentAfter = String.format("<![CDATA[  AND %s < #{filter.%s, jdbcType = %s}  ]]>",
                columnName, javaPropertyAfter, jdbcTypeName);

        XmlElement ifLikePropertyElementAfter = new XmlElement("if");
        String ifPropertyValueAfter = String.format("null != filter.%s", javaPropertyAfter);
        ifLikePropertyElementAfter.addAttribute(new Attribute("test", ifPropertyValueAfter));
        ifLikePropertyElementAfter.addElement(new TextElement(contentAfter));

        // 添加两个if 标签
        ifWhereFilterElement.addElement(ifLikePropertyElementBefore);
        ifWhereFilterElement.addElement(ifLikePropertyElementAfter);
    }

}
