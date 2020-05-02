package com.suyh.version03.plugin.custom;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 尝试实现一个过滤器实体类
 */
public class FilterEntityPlugin extends PluginAdapter {

    private String dateFormat = Constans.DEFAULT_DATE_FORMAT;
    private String javaFileEncoding;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);

        Context context = introspectedTable.getContext();
        javaFileEncoding = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING);
    }

    /**
     * 实现自己的Java 文件，扩展
     *
     * @param introspectedTable
     * @return
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        Context context = introspectedTable.getContext();

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration
                = context.getJavaModelGeneratorConfiguration();
        String targetPackage = javaModelGeneratorConfiguration.getTargetPackage();
        String targetProject = javaModelGeneratorConfiguration.getTargetProject();

        List<GeneratedJavaFile> list = new ArrayList<>();

        CompilationUnit filterEntity = generateFilterEntity(introspectedTable, targetPackage);
        List<CompilationUnit> javaClass = Arrays.asList(filterEntity);

        for (CompilationUnit unit : javaClass) {
            GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(unit, targetProject,
                    javaFileEncoding, this.context.getJavaFormatter());
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
        // suyh: 这里可以得到实体类的完整类名
        String entityClazzType = introspectedTable.getBaseRecordType();

        // 这里可以得到实体类的短名，仅类名：  CrmCustomerInfo
        String modelClassName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();

        // 父类类型，我这里直接使用实体类
        FullyQualifiedJavaType superClassType = new FullyQualifiedJavaType(entityClazzType);

        // 拼接完整类名
        TopLevelClass filterClass = new TopLevelClass(
                String.format("%s.%sFilter", basePackage, modelClassName));

        filterClass.setSuperClass(superClassType);
        filterClass.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType modelJavaType = new FullyQualifiedJavaType(entityClazzType);
        filterClass.addImportedType(modelJavaType);
        for (String str : AnnotationEnum.SWAGGER.getImportEntities()) {
            filterClass.addImportedType(str);
        }
        filterClass.addAnnotation("@ApiModel");

        filterClass.addJavaDocLine("/**");
        filterClass.addJavaDocLine(" * 实体过滤器类");
        filterClass.addJavaDocLine(" *");
        filterClass.addJavaDocLine(" * @author " + getCurUser());
        filterClass.addJavaDocLine(" * @date " + getDateString());
        filterClass.addJavaDocLine(" */");

        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : allColumns) {
            String javaProperty = introspectedColumn.getJavaProperty();

            FullyQualifiedJavaType fullyQualifiedJavaType = introspectedColumn.getFullyQualifiedJavaType();
            if (fullyQualifiedJavaType.equals(FullyQualifiedJavaType.getDateInstance())) {
                // 添加两个字段，分别拼接 Before 和 After
                addDateFilterField(filterClass, javaProperty + "Before");
                addDateFilterField(filterClass, javaProperty + "After");
            }
        }

        return filterClass;
    }

    /**
     * 对日期类型的字段，添加过滤字段
     *
     * @param filterClass
     * @param fieldName
     */
    private void addDateFilterField(TopLevelClass filterClass, String fieldName) {
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
        SimpleDateFormat sdf = new SimpleDateFormat(Constans.DEFAULT_DATE_FORMAT);
        return sdf.format(new Date());
    }

    private static String getCurUser() {
        return System.getProperty("user.name");
    }
}
