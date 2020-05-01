package com.suyh.version02;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GenMain {
    public static void main(String[] args) {
        //读取文件
        String filePath = "/mybatis/version02/generatorConfig-v2-oracle.xml";
        String generatorConfig = GenMain.class.getResource(filePath).getFile();
        File configFile = new File(generatorConfig);
        List<String> warnings = new ArrayList<String>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = null;
        //true:覆盖生成
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = null;
        try {
            config = cp.parseConfiguration(configFile);
            myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
            System.err.println("代码成功生成!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}