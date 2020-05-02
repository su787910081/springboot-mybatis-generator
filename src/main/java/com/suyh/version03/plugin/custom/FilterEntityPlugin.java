package com.suyh.version03.plugin.custom;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 尝试实现一个过滤器实体类
 */
public class FilterEntityPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 实现自己的Java 文件，扩展
     * @param introspectedTable
     * @return
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        Context context = introspectedTable.getContext();

        String basePackage = context.getProperty("baseJavaPackage");
        String apiProject = context.getProperty("apiProject");
        List<GeneratedJavaFile> list = new ArrayList<>();

        List<CompilationUnit> addDTOs = addDTOs(introspectedTable, basePackage);

        addDTOs.forEach(unit -> list.add(
                new GeneratedJavaFile(unit, apiProject, this.context.getProperty("javaFileEncoding"), this.context.getJavaFormatter())
        ));

        return list;
    }

    private List<CompilationUnit> addDTOs(IntrospectedTable introspectedTable,  String basePackage) {
        CompilationUnit reqUnit = generateReqUnit(introspectedTable, basePackage);
        CompilationUnit respUnit = generateRespUnit(introspectedTable, basePackage);
        return Arrays.asList(reqUnit, respUnit);
    }

    private CompilationUnit generateReqUnit(IntrospectedTable introspectedTable,  String basePackage) {
        String entityClazzType = introspectedTable.getBaseRecordType();
        String destPackage = basePackage + ".dto.req";

        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();

        StringBuilder builder = new StringBuilder();

        FullyQualifiedJavaType superClassType = new FullyQualifiedJavaType(
                builder.append("BaseReq<")
                        .append(entityClazzType)
                        .append(">").toString()
        );

        TopLevelClass dto = new TopLevelClass(
                builder.delete(0, builder.length())
                        .append(destPackage)
                        .append(".")
                        .append(domainObjectName)
                        .append("Req")
                        .toString()
        );

        dto.setSuperClass(superClassType);
        dto.setVisibility(JavaVisibility.PUBLIC);

//        FullyQualifiedJavaType baseReqInstance = FullyQualifiedJavaTypeProxyFactory.getBaseReqInstance();
        FullyQualifiedJavaType baseReqInstance = null;  // TODO: 暂时不清楚这个怎么处理。
        FullyQualifiedJavaType modelJavaType = new FullyQualifiedJavaType(entityClazzType);
        dto.addImportedType(baseReqInstance);
        dto.addImportedType(modelJavaType);
        dto.addJavaDocLine("/**\n" +
                " * " + getDomainName(introspectedTable) + " DTO\n" +
                " *\n" +
                " * @author " + getCurUser() + " 2019/1/8 Create 1.0  <br>\n" +
                " * @version 1.0\n" +
                " */");

        return dto;
    }

    private String getDomainName(IntrospectedTable introspectedTable) {
        return introspectedTable.getRemarks() == null ?  introspectedTable.getFullyQualifiedTable().getDomainObjectName() : introspectedTable.getRemarks();
    }

    private CompilationUnit generateRespUnit(IntrospectedTable introspectedTable, String basePackage) {
        String entityClazzType = introspectedTable.getBaseRecordType();
        String destPackage = basePackage + ".dto.resp";

        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();

        StringBuilder builder = new StringBuilder();

        FullyQualifiedJavaType superClassType = new FullyQualifiedJavaType(
                builder.append("BaseResp<")
                        .append(entityClazzType)
                        .append(">").toString()
        );

        TopLevelClass dto = new TopLevelClass(
                builder.delete(0, builder.length())
                        .append(destPackage)
                        .append(".")
                        .append(domainObjectName)
                        .append("Resp")
                        .toString()
        );

        dto.setSuperClass(superClassType);
        dto.setVisibility(JavaVisibility.PUBLIC);

//        FullyQualifiedJavaType baseReqInstance = FullyQualifiedJavaTypeProxyFactory.getBaseRespInstance();
        FullyQualifiedJavaType baseReqInstance  = null; // TODO: 暂时不清楚这个怎么处理。
        FullyQualifiedJavaType modelJavaType = new FullyQualifiedJavaType(entityClazzType);
        dto.addImportedType(baseReqInstance);
        dto.addImportedType(modelJavaType);
        dto.addJavaDocLine("/**\n" +
                " * " + getDomainName(introspectedTable) + " DTO\n" +
                " *\n" +
                " * @author " + getCurUser() + " " + getCurDate() + " Create 1.0  <br>\n" +
                " * @version 1.0\n" +
                " */");

        return dto;
    }

    private String getCurDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }


    private static String getCurUser() {
        return System.getProperty("user.name");
    }
}
