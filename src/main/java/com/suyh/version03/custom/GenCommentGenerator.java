package com.suyh.version03.custom;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * 保留一下，现在我使用的是我写的那个自定义注释生成器，如果有问题的话还需要使用这一个。
 */
public class GenCommentGenerator implements CommentGenerator {

    private Properties properties = new Properties();
    private boolean suppressDate = false;
    private boolean suppressAllComments = false;
    private boolean addRemarkComments = false;
    private SimpleDateFormat dateFormat;

    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        this.suppressDate = StringUtility.isTrue(properties.getProperty("suppressDate"));
        this.suppressAllComments = StringUtility.isTrue(properties.getProperty("suppressAllComments"));
        this.addRemarkComments = StringUtility.isTrue(properties.getProperty("addRemarkComments"));
        String dateFormatString = properties.getProperty("dateFormat");
        if (StringUtility.stringHasValue(dateFormatString)) {
            this.dateFormat = new SimpleDateFormat(dateFormatString);
        }
    }

    /**
     * 给字段添加数据库备注
     *
     * @param field
     * @param introspectedTable
     * @param introspectedColumn
     */
    @Override
    public void addFieldComment(Field field,
                                IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (!this.suppressAllComments) {
            field.addJavaDocLine("/**");


            StringBuilder sb = new StringBuilder();
            sb.append(" * Column: ");
            sb.append(introspectedColumn.getActualColumnName());
            field.addJavaDocLine(sb.toString());

            String remarks = introspectedColumn.getRemarks();
            field.addJavaDocLine(" *   " + remarks);

            // 字段注释上面不添加作者时间标签
            // this.addJavadocTag(field, false);
            field.addJavaDocLine(" */");
        }
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        // suyh: 这个似乎像是对静态变量做注释的一个方法
    }


    @Override
    public void addClassComment(
            InnerClass innerClass,
            IntrospectedTable introspectedTable) {
        if (!this.suppressAllComments) {
            StringBuilder sb = new StringBuilder();
            innerClass.addJavaDocLine("/**");
            innerClass.addJavaDocLine(" * This class was generated by MyBatis Generator.");
            sb.append(" * This class corresponds to the database table ");
            sb.append(introspectedTable.getFullyQualifiedTable());
            innerClass.addJavaDocLine(sb.toString());
            this.addJavadocTag(innerClass, false);
            innerClass.addJavaDocLine(" */");
        }
    }





    /**
     * setter方法注释
     *
     * @param method
     * @param introspectedTable
     * @param introspectedColumn
     */
    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**");
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
            sb.append(" * 设置");
            sb.append(introspectedColumn.getRemarks());
            method.addJavaDocLine(sb.toString());
            method.addJavaDocLine(" *");
        }
        Parameter parm = method.getParameters().get(0);
        sb.setLength(0);
        sb.append(" * @param ");
        sb.append(parm.getName());
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
            sb.append(" ");
            sb.append(introspectedColumn.getRemarks());
        }
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */");
    }


    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

    }

    @Override
    public void addComment(XmlElement xmlElement) {
        if (!this.suppressAllComments) {
            xmlElement.addElement(new TextElement("<!--"));
            StringBuilder sb = new StringBuilder();
            sb.append("  WARNING - ");
            sb.append("@mbg.generated");
            xmlElement.addElement(new TextElement(sb.toString()));
            xmlElement.addElement(new TextElement("  This element is automatically generated by MyBatis Generator, do not modify."));
            String s = this.getDateString();
            if (s != null) {
                sb.setLength(0);
                sb.append("  This element was generated on ");
                sb.append(s);
                sb.append('.');
                xmlElement.addElement(new TextElement(sb.toString()));
            }

            xmlElement.addElement(new TextElement("-->"));
        }
    }

    @Override
    public void addRootComment(XmlElement xmlElement) {

    }

    @Override
    public void addGeneralMethodAnnotation(
            Method method,
            IntrospectedTable introspectedTable,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString();
        method.addAnnotation(this.getGeneratedAnnotation(comment));

    }

    @Override
    public void addGeneralMethodAnnotation(
            Method method,
            IntrospectedTable introspectedTable,
            IntrospectedColumn introspectedColumn,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source field: " + introspectedTable.getFullyQualifiedTable().toString() + "." + introspectedColumn.getActualColumnName();
        method.addAnnotation(this.getGeneratedAnnotation(comment));

    }

    @Override
    public void addFieldAnnotation(
            Field field,
            IntrospectedTable introspectedTable,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString();
        field.addAnnotation(this.getGeneratedAnnotation(comment));

    }

    @Override
    public void addFieldAnnotation(
            Field field,
            IntrospectedTable introspectedTable,
            IntrospectedColumn introspectedColumn,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source field: " + introspectedTable.getFullyQualifiedTable().toString() + "." + introspectedColumn.getActualColumnName();
        field.addAnnotation(this.getGeneratedAnnotation(comment));
        if (!this.suppressAllComments && this.addRemarkComments) {
            String remarks = introspectedColumn.getRemarks();
            if (this.addRemarkComments && StringUtility.stringHasValue(remarks)) {
                field.addJavaDocLine("/**");
                field.addJavaDocLine(" * Database Column Remarks:");
                String[] remarkLines = remarks.split(System.getProperty("line.separator"));
                String[] var8 = remarkLines;
                int var9 = remarkLines.length;

                for (int var10 = 0; var10 < var9; ++var10) {
                    String remarkLine = var8[var10];
                    field.addJavaDocLine(" *   " + remarkLine);
                }

                field.addJavaDocLine(" */");
            }
        }

    }

    @Override
    public void addClassAnnotation(
            InnerClass innerClass,
            IntrospectedTable introspectedTable,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString();
        innerClass.addAnnotation(this.getGeneratedAnnotation(comment));

    }

    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        javaElement.addJavaDocLine(" *");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append("@author su.yunhong");
        if (markAsDoNotDelete) {
            sb.append(" do_not_delete_during_merge");
        }

        javaElement.addJavaDocLine(sb.toString());

        String s = this.getDateString();
        if (s != null) {
            StringBuilder sbDate = new StringBuilder();
            sbDate.append(" * @Date ");
            sbDate.append(s);
            javaElement.addJavaDocLine(sbDate.toString());
        }
    }

    protected String getDateString() {
        if (this.suppressDate) {
            return null;
        } else {
            return this.dateFormat != null ? this.dateFormat.format(new Date()) : (new Date()).toString();
        }
    }


    @Override
    public void addClassComment(
            InnerClass innerClass,
            IntrospectedTable introspectedTable,
            boolean markAsDoNotDelete) {
        if (!this.suppressAllComments) {
            StringBuilder sb = new StringBuilder();
            innerClass.addJavaDocLine("/**");
            innerClass.addJavaDocLine(" * This class was generated by MyBatis Generator.");
            sb.append(" * This class corresponds to the database table ");
            sb.append(introspectedTable.getFullyQualifiedTable());
            innerClass.addJavaDocLine(sb.toString());
            this.addJavadocTag(innerClass, markAsDoNotDelete);
            innerClass.addJavaDocLine(" */");
        }
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (!this.suppressAllComments && this.addRemarkComments) {
            topLevelClass.addJavaDocLine("/**");
            FullyQualifiedTable tableName = introspectedTable.getFullyQualifiedTable();
            topLevelClass.addJavaDocLine(" * Table: " + tableName);
            String remarks = introspectedTable.getRemarks();
            topLevelClass.addJavaDocLine(" *   " + remarks);
            this.addJavadocTag(topLevelClass, false);

            topLevelClass.addJavaDocLine(" */");
        }
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        if (!this.suppressAllComments) {
            StringBuilder sb = new StringBuilder();
            innerEnum.addJavaDocLine("/**");
            innerEnum.addJavaDocLine(" * This enum was generated by MyBatis Generator.");
            sb.append(" * This enum corresponds to the database table ");
            sb.append(introspectedTable.getFullyQualifiedTable());
            innerEnum.addJavaDocLine(sb.toString());
            this.addJavadocTag(innerEnum, false);
            innerEnum.addJavaDocLine(" */");
        }
    }
    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {

    }
    /**
     * getter方法注释
     *
     * @param method
     * @param introspectedTable
     * @param introspectedColumn
     */
    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**");
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
            sb.append(" * 获取");
            sb.append(introspectedColumn.getRemarks());
            method.addJavaDocLine(sb.toString());
            method.addJavaDocLine(" *");
        }
        sb.setLength(0);
        sb.append(" * @return ");
        sb.append(introspectedColumn.getActualColumnName());
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
            sb.append(" - ");
            sb.append(introspectedColumn.getRemarks());
        }
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */");
    }
    private String getGeneratedAnnotation(String comment) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("@Generated(");
        if (this.suppressAllComments) {
            buffer.append('"');
        } else {
            buffer.append("value=\"");
        }

        buffer.append(MyBatisGenerator.class.getName());
        buffer.append('"');
        if (!this.suppressDate && !this.suppressAllComments) {
            buffer.append(", date=\"");
            buffer.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
            buffer.append('"');
        }

        if (!this.suppressAllComments) {
            buffer.append(", comments=\"");
            buffer.append(comment);
            buffer.append('"');
        }

        buffer.append(')');
        return buffer.toString();
    }
}
