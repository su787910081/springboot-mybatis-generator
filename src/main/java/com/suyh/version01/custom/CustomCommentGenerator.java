package com.suyh.version01.custom;

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
 * 自定义注释实现类
 */
public class CustomCommentGenerator implements CommentGenerator {

    private Properties properties = new Properties();
    private boolean suppressDate = false;
    private boolean suppressAllComments = false;
    private boolean addRemarkComments = false;
    private SimpleDateFormat dateFormat;

    public CustomCommentGenerator() {
    }

    public void addJavaFileComment(CompilationUnit compilationUnit) {
    }

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

    public void addRootComment(XmlElement rootElement) {
    }

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

    /**
     * suyh
     * 这个方法估计是生成字段注释的方法
     *
     * @param field
     * @param introspectedTable
     * @param introspectedColumn
     */
    public void addFieldComment(Field field,
                                IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
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

    public void addFieldComment(Field field,
                                IntrospectedTable introspectedTable) {
        if (!this.suppressAllComments) {
            StringBuilder sb = new StringBuilder();
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * This field was generated by MyBatis Generator.");
            sb.append(" * This field corresponds to the database table ");
            sb.append(introspectedTable.getFullyQualifiedTable());
            field.addJavaDocLine(sb.toString());
            this.addJavadocTag(field, false);
            field.addJavaDocLine(" */");
        }
    }
	
    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
//        if (!this.suppressAllComments) {
//            StringBuilder sb = new StringBuilder();
//            method.addJavaDocLine("/**");
//            method.addJavaDocLine(" * This method was generated by MyBatis Generator.");
//            sb.append(" * This method corresponds to the database table ");
//            sb.append(introspectedTable.getFullyQualifiedTable());
//            method.addJavaDocLine(sb.toString());
//            this.addJavadocTag(method, false);
//            method.addJavaDocLine(" */");
//        }
    }

    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
//        if (!this.suppressAllComments) {
//            StringBuilder sb = new StringBuilder();
//            method.addJavaDocLine("/**");
//            method.addJavaDocLine(" * This method was generated by MyBatis Generator.");
//            sb.append(" * This method returns the value of the database column ");
//            sb.append(introspectedTable.getFullyQualifiedTable());
//            sb.append('.');
//            sb.append(introspectedColumn.getActualColumnName());
//            method.addJavaDocLine(sb.toString());
//            method.addJavaDocLine(" *");
//            sb.setLength(0);
//            sb.append(" * @return the value of ");
//            sb.append(introspectedTable.getFullyQualifiedTable());
//            sb.append('.');
//            sb.append(introspectedColumn.getActualColumnName());
//            method.addJavaDocLine(sb.toString());
//            this.addJavadocTag(method, false);
//            method.addJavaDocLine(" */");
//        }
    }

    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
//        if (!this.suppressAllComments) {
//            StringBuilder sb = new StringBuilder();
//            method.addJavaDocLine("/**");
//            method.addJavaDocLine(" * This method was generated by MyBatis Generator.");
//            sb.append(" * This method sets the value of the database column ");
//            sb.append(introspectedTable.getFullyQualifiedTable());
//            sb.append('.');
//            sb.append(introspectedColumn.getActualColumnName());
//            method.addJavaDocLine(sb.toString());
//            method.addJavaDocLine(" *");
//            Parameter parm = (Parameter)method.getParameters().get(0);
//            sb.setLength(0);
//            sb.append(" * @param ");
//            sb.append(parm.getName());
//            sb.append(" the value for ");
//            sb.append(introspectedTable.getFullyQualifiedTable());
//            sb.append('.');
//            sb.append(introspectedColumn.getActualColumnName());
//            method.addJavaDocLine(sb.toString());
//            this.addJavadocTag(method, false);
//            method.addJavaDocLine(" */");
//        }
    }

    public void addGeneralMethodAnnotation(
            Method method,
            IntrospectedTable introspectedTable,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString();
        method.addAnnotation(this.getGeneratedAnnotation(comment));
    }

    public void addGeneralMethodAnnotation(
            Method method,
            IntrospectedTable introspectedTable,
            IntrospectedColumn introspectedColumn,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source field: " + introspectedTable.getFullyQualifiedTable().toString() + "." + introspectedColumn.getActualColumnName();
        method.addAnnotation(this.getGeneratedAnnotation(comment));
    }

    public void addFieldAnnotation(
            Field field,
            IntrospectedTable introspectedTable,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString();
        field.addAnnotation(this.getGeneratedAnnotation(comment));
    }

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

    public void addClassAnnotation(
            InnerClass innerClass,
            IntrospectedTable introspectedTable,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString();
        innerClass.addAnnotation(this.getGeneratedAnnotation(comment));
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
