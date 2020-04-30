




-- 客户/供应商 
-- 基本信息表结构

    DROP TABLE IF EXISTS crm_customer_info;
    CREATE TABLE crm_customer_info (
        customer_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键：客户ID',
        full_name VARCHAR(60) NOT NULL COMMENT '客户全称',
        short_name VARCHAR(30) NULL COMMENT '客户简称',
        manager_name VARCHAR(20) NULL COMMENT '负责人',
        manager_phone VARCHAR(20) NULL COMMENT '联系电话',
        inner_customer VARCHAR(3) NOT NULL COMMENT '是否内部用户(这个字段似乎有些多余了)【radio:1-是,2-否】',
        customer_type VARCHAR(3) NULL COMMENT '客户类型【select:1-内部客户,2-供应商,3-承运商】',
        address VARCHAR(128) NULL COMMENT '联系地址',
        us_plat_system VARCHAR(3) NULL COMMENT '是否使用平台系统(当有自有系统时则可不使用平台系统)【radio:1-是,2-否】',
        platform_admin_user VARCHAR(30) NULL COMMENT '平台管理员帐号，非自有系统用户才会有。',
        -- settle_money_id VARCHAR(36) NULL COMMENT '结算信息ID，映射到结算信息表',
        using_status VARCHAR(3) NOT NULL COMMENT '启用状态【radio:1-是(启用),2-否(停用)】'
    ) ENGINE = INNODB COMMENT '客户基本信息表结构：内部客户、供应商、承运商';




-- 客户/供应商
-- 结算信息表结构
--      一个客户对应一条基本信息和一条结算信息
-- 客户ID、结算方式、月结日、开票税点(%)、可延期天数、欠款上限(元)

    DROP TABLE IF EXISTS crm_settle_money_info;
    CREATE TABLE crm_settle_money_info (
        customer_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键：映射到客户基础信息表客户ID',
        settle_type VARCHAR(10) NULL COMMENT '结算方式',
        settle_day INTEGER COMMENT '月结日，以0 表示自然月',
        tax_rate VARCHAR(10) COMMENT '开票税点(%)',
        enable_delay_days INTEGER COMMENT '允许延期天数',
        max_arrears_money INTEGER COMMENT '欠款上限(元)'
    ) ENGINE = INNODB COMMENT '结算信息公共表结构，客户、供应商、承运商。一条结算信息只对应一个属主客户';



-- 客户/供应商
-- 联系人信息
--      一个客户可以有多条联系人信息
-- 字段 - 表设计
--      主键ID、属主客户ID(主键)、联系类别、联系人姓名、联系人手机、联系人邮箱、QQ号、微信号、地址
-- 
--     联系类型：发货联系人、收货联系人、对帐联系人、结算联系人

    DROP TABLE IF EXISTS crm_contacts_info;
    CREATE TABLE crm_contacts_info (
        contacts_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键ID: 联系人ID',
        customer_id VARCHAR(36) NOT NULL COMMENT '属主ID，映射到客户基本信息表',
        contacts_kinds VARCHAR(20) NULL COMMENT '联系类别',
        contacts_name VARCHAR(30) NULL COMMENT '联系人姓名',
        phone VARCHAR(20) NULL COMMENT '电话',
        email VARCHAR(30) NULL COMMENT '邮箱',
        qq VARCHAR(20) NULL COMMENT 'QQ 号',
        wechat VARCHAR(30) NULL COMMENT '微信',
        address VARCHAR(128) NULL COMMENT '地址'
    ) ENGINE = INNODB COMMENT '联系人信息表结构，多条联系人信息可以对应一个属主用户';






-- 发票信息(客户)
-- 一个客户可以多条发票信息
-- 客户只有发票信息
-- 供应商只有收款信息
-- 字段 - 表设计
-- 主键ID、属主客户ID、发票类型、受票方名称、统一社会信用代码、开户行名称、注册电话、银行帐号、注册地址

    DROP TABLE IF EXISTS crm_invoice_info;
    CREATE TABLE crm_invoice_info (
        invoice_info_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键ID：发票信息ID',
        customer_id VARCHAR(36) NOT NULL COMMENT '属主ID，映射到客户基本信息表',
        invoice_type_id VARCHAR(3) NULL COMMENT '发票类型【select:1-增值税普通发票（电子）,2-增值税普通发票（纸制）3-增值税专用发票,...(其他)】',
        drawee_name VARCHAR(30) NULL COMMENT '受票方名称',
        unified_social_credit_code VARCHAR(30) NULL COMMENT '统一社会信息代码',
        opening_bank VARCHAR(30) NULL COMMENT '开户行名称',
        register_phone VARCHAR(30) NULL COMMENT '注册电话',
        bank_account_code VARCHAR(30) NULL COMMENT '银行帐号',
        bank_account_name VARCHAR(20) NULL COMMENT '帐户名',
        register_address VARCHAR(128) NULL COMMENT '注册地址'
    ) ENGINE = INNODB COMMENT '发票信息表结构，多条发票信息可以对应一个属主用户';



-- 收款信息(供应商)
-- 一个供应商可以多条收款信息
-- 客户只有发票信息
-- 供应商只有收款信息
-- 字段 - 表设计
--      主键ID、属主供应商(客户)ID、开户行、帐户名、收款帐户、操作人、操作时间
-- 问题: 
--    这里的操作人和操作时间，是要记录的什么呢？

    DROP TABLE IF EXISTS crm_payee_info;
    CREATE TABLE crm_payee_info (
        payee_info_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键ID：收款人信息ID',
        customer_id VARCHAR(36) NOT NULL COMMENT '属主ID，映射到客户基本信息表',
        opening_bank VARCHAR(30) NULL COMMENT '开户行名称',
        bank_account_code VARCHAR(30) NULL COMMENT '银行帐号-收款帐户',
        bank_account_name VARCHAR(20) NULL COMMENT '帐户名',
        operator VARCHAR(20) NULL COMMENT '操作人',
        update_time DATETIME NULL COMMENT '操作时间'
    ) ENGINE = INNODB  COMMENT '收款人信息表结构，多条收款信息可以对应一个属主用户';






-- 承运商
-- 承运商基本信息
--    1. 当承运商有自有系统时，由后面的【是否使用平台系统】、【平台系统管理员账号】两个字段不可见
--    2. 当承运商没有自有系统时，则需要选择是否使用平台系统，则选择为【是】时，则需要录入系统管理员账号
-- 表结构 - 字段设计
--       承运商ID、承运商名称、简称、是否自有系统、负责人、负责人联系电话、是否使用平台系统、平台系统管理员帐号、地址
-- 问题：
--     1. 这里的地址是负责人的地址，还是什么地址？
--          好像是承运商公司的地址
-- 与客户表共用，详见表： crm_customer_info 


-- 承运商
-- 承运商结算信息
-- 表结构 - 字段设计
--      承运商ID(主键)、结算方式、月结日(自然月或指定某一天)、开票税点(%)、可延期天数、欠款上限(元)
-- 与结算信息表共用，详见表： crm_settle_money_info 


-- 承运商
-- 承运商联系人信息
-- 表结构 - 字段设计
-- 与联系人信息表共用，详见表: crm_contacts_info


-- 承运商
-- 承运商收款人信息
-- 表结构 - 字段设计
-- 与收款人信息表共用，详见表: crm_payee_info


