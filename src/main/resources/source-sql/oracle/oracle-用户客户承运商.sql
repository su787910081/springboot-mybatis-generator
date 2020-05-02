

-- 客户/供应商 
-- 基本信息表结构
-- 在这张表中尽量多一些类型，主要处理就在这张表中来做测试和验证
CREATE TABLE crm_customer_info (
    customer_id VARCHAR(36) NOT NULL PRIMARY KEY
);

-- 表注释
COMMENT ON TABLE crm_customer_info  IS '客户基本信息表结构：内部客户、供应商、承运商';
-- 主键列
COMMENT ON COLUMN crm_customer_info.customer_id IS '主键：客户ID';
-- 添加列
ALTER TABLE crm_customer_info ADD full_name VARCHAR2(60) NULL ;
COMMENT ON COLUMN crm_customer_info.full_name IS '客户全称';

ALTER TABLE crm_customer_info ADD short_name VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_customer_info.short_name IS '客户简称';

ALTER TABLE crm_customer_info ADD manager_name VARCHAR2(20) NULL ;
COMMENT ON COLUMN crm_customer_info.manager_name IS '负责人';

ALTER TABLE crm_customer_info ADD manager_phone VARCHAR2(20) NULL ;
COMMENT ON COLUMN crm_customer_info.manager_phone IS '联系电话';

ALTER TABLE crm_customer_info ADD inner_customer VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_customer_info.inner_customer IS '是否内部用户【radio:Y/N】';

ALTER TABLE crm_customer_info ADD customer_type VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_customer_info.customer_type IS '客户类型【select:1-内部客户,2-供应商,3-承运商】';

ALTER TABLE crm_customer_info ADD address VARCHAR2(128) NULL ;
COMMENT ON COLUMN crm_customer_info.address IS '联系地址';

ALTER TABLE crm_customer_info ADD us_plat_system VARCHAR2(128) NULL ;
COMMENT ON COLUMN crm_customer_info.us_plat_system IS '是否使用平台系统【radio:1-是,2-否】';

ALTER TABLE crm_customer_info ADD platform_admin_user VARCHAR2(128) NULL ;
COMMENT ON COLUMN crm_customer_info.platform_admin_user IS '平台管理员帐号，非自有系统用户才会有。';

ALTER TABLE crm_customer_info ADD created_date DATE NULL ;
COMMENT ON COLUMN crm_customer_info.created_date IS '创建时间';

ALTER TABLE crm_customer_info ADD settle_day DECIMAL NULL ;
COMMENT ON COLUMN crm_customer_info.settle_day IS '月结日，以0 表示自然月';

ALTER TABLE crm_customer_info ADD price DECIMAL(10, 2) NULL ;
COMMENT ON COLUMN crm_customer_info.price IS '价格，10 位长度，两位小数位';

ALTER TABLE crm_customer_info ADD using_status DECIMAL NULL ;
COMMENT ON COLUMN crm_customer_info.using_status IS '启用状态【radio:1-是(启用),2-否(停用)】';



-- 客户/供应商
-- 结算信息表结构
--      一个客户对应一条基本信息和一条结算信息
-- 客户ID、结算方式、月结日、开票税点(%)、可延期天数、欠款上限(元)


CREATE TABLE crm_settle_money_info (
    customer_id VARCHAR(36) NOT NULL PRIMARY KEY
);

-- 表注释
COMMENT ON TABLE crm_settle_money_info  IS '结算信息公共表结构，客户、供应商、承运商。一条结算信息只对应一个属主客户';
-- 主键列
COMMENT ON COLUMN crm_settle_money_info.customer_id IS '主键：映射到客户基础信息表客户ID';
-- 添加列
ALTER TABLE crm_settle_money_info ADD settle_type VARCHAR2(10) NULL ;
COMMENT ON COLUMN crm_settle_money_info.settle_type IS '结算方式';

ALTER TABLE crm_settle_money_info ADD settle_day INTEGER NULL ;
COMMENT ON COLUMN crm_settle_money_info.settle_day IS '月结日，以0 表示自然月';

ALTER TABLE crm_settle_money_info ADD tax_rate NUMERIC(10,2) NULL ;
COMMENT ON COLUMN crm_settle_money_info.tax_rate IS '开票税点(%)';

ALTER TABLE crm_settle_money_info ADD enable_delay_days NUMERIC NULL ;
COMMENT ON COLUMN crm_settle_money_info.enable_delay_days IS '允许延期天数';

ALTER TABLE crm_settle_money_info ADD max_arrears_money NUMERIC NULL ;
COMMENT ON COLUMN crm_settle_money_info.max_arrears_money IS '欠款上限(元)';



-- 客户/供应商
-- 联系人信息
--      一个客户可以有多条联系人信息
-- 字段 - 表设计
--      主键ID、属主客户ID(主键)、联系类别、联系人姓名、联系人手机、联系人邮箱、QQ号、微信号、地址
--
--     联系类型：发货联系人、收货联系人、对帐联系人、结算联系人


CREATE TABLE crm_contacts_info (
    contacts_id VARCHAR(36) NOT NULL PRIMARY KEY
);

-- 表注释
COMMENT ON TABLE crm_contacts_info  IS '联系人信息表结构，多条联系人信息可以对应一个属主用户';
-- 主键列
COMMENT ON COLUMN crm_contacts_info.contacts_id IS '主键ID: 联系人ID';
-- 添加列
ALTER TABLE crm_contacts_info ADD customer_id VARCHAR2(36) NULL ;
COMMENT ON COLUMN crm_contacts_info.customer_id IS '属主ID，映射到客户基本信息表';

ALTER TABLE crm_contacts_info ADD contacts_kinds VARCHAR(20) NULL ;
COMMENT ON COLUMN crm_contacts_info.contacts_kinds IS '联系类别';

ALTER TABLE crm_contacts_info ADD contacts_name VARCHAR(30) NULL ;
COMMENT ON COLUMN crm_contacts_info.contacts_name IS '联系人姓名';

ALTER TABLE crm_contacts_info ADD phone VARCHAR(20) NULL ;
COMMENT ON COLUMN crm_contacts_info.phone IS '联系人电话';

ALTER TABLE crm_contacts_info ADD email VARCHAR(30) NULL ;
COMMENT ON COLUMN crm_contacts_info.email IS '邮箱';

ALTER TABLE crm_contacts_info ADD qq VARCHAR(30) NULL ;
COMMENT ON COLUMN crm_contacts_info.qq IS 'QQ 号';

ALTER TABLE crm_contacts_info ADD wechat VARCHAR(30) NULL ;
COMMENT ON COLUMN crm_contacts_info.wechat IS '微信';

ALTER TABLE crm_contacts_info ADD address VARCHAR(128) NULL ;
COMMENT ON COLUMN crm_contacts_info.address IS '地址';




-- 发票信息(客户)
-- 一个客户可以多条发票信息
-- 客户只有发票信息
-- 供应商只有收款信息
-- 字段 - 表设计
-- 主键ID、属主客户ID、发票类型、受票方名称、统一社会信用代码、开户行名称、注册电话、银行帐号、注册地址

CREATE TABLE crm_invoice_info (
    invoice_info_id VARCHAR(36) NOT NULL PRIMARY KEY
);

-- 表注释
COMMENT ON TABLE crm_invoice_info  IS '发票信息表结构，多条发票信息可以对应一个属主用户';
-- 主键列
COMMENT ON COLUMN crm_invoice_info.invoice_info_id IS '主键ID：发票信息ID';
-- 添加列
ALTER TABLE crm_invoice_info ADD customer_id VARCHAR2(36) NULL ;
COMMENT ON COLUMN crm_invoice_info.customer_id IS '属主ID，映射到客户基本信息表';
ALTER TABLE crm_invoice_info ADD invoice_type_id VARCHAR2(3) NULL ;
COMMENT ON COLUMN crm_invoice_info.invoice_type_id IS '发票类型【select:1-增值税普通发票（电子）,2-增值税普通发票（纸制）3-增值税专用发票,...(其他)】';
ALTER TABLE crm_invoice_info ADD drawee_name VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_invoice_info.drawee_name IS '受票方名称';
ALTER TABLE crm_invoice_info ADD unified_social_credit_code VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_invoice_info.unified_social_credit_code IS '统一社会信息代码';
ALTER TABLE crm_invoice_info ADD opening_bank VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_invoice_info.opening_bank IS '开户行名称';
ALTER TABLE crm_invoice_info ADD register_phone VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_invoice_info.register_phone IS '注册电话';
ALTER TABLE crm_invoice_info ADD bank_account_code VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_invoice_info.bank_account_code IS '银行帐号';
ALTER TABLE crm_invoice_info ADD bank_account_name VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_invoice_info.bank_account_name IS '帐户名';
ALTER TABLE crm_invoice_info ADD register_address VARCHAR2(128) NULL ;
COMMENT ON COLUMN crm_invoice_info.register_address IS '注册地址';





-- 收款信息(供应商)
-- 一个供应商可以多条收款信息
-- 客户只有发票信息
-- 供应商只有收款信息
-- 字段 - 表设计
--      主键ID、属主供应商(客户)ID、开户行、帐户名、收款帐户、操作人、操作时间
-- 问题:
--    这里的操作人和操作时间，是要记录的什么呢？


CREATE TABLE crm_payee_info (
    payee_info_id VARCHAR(36) NOT NULL PRIMARY KEY
);

-- 表注释
COMMENT ON TABLE crm_payee_info  IS '收款人信息表结构，多条收款信息可以对应一个属主用户';
-- 主键列
COMMENT ON COLUMN crm_payee_info.payee_info_id IS '主键ID：收款人信息ID';
-- 添加列
ALTER TABLE crm_payee_info ADD customer_id VARCHAR2(36) NULL ;
COMMENT ON COLUMN crm_payee_info.customer_id IS '属主ID，映射到客户基本信息表';
ALTER TABLE crm_payee_info ADD opening_bank VARCHAR2(36) NULL ;
COMMENT ON COLUMN crm_payee_info.opening_bank IS '开户行名称';
ALTER TABLE crm_payee_info ADD bank_account_code VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_payee_info.bank_account_code IS '银行帐号-收款帐户';
ALTER TABLE crm_payee_info ADD bank_account_name VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_payee_info.bank_account_name IS '帐户名';
ALTER TABLE crm_payee_info ADD operator VARCHAR2(30) NULL ;
COMMENT ON COLUMN crm_payee_info.operator IS '操作人';
ALTER TABLE crm_payee_info ADD update_time DATE NULL ;
COMMENT ON COLUMN crm_payee_info.update_time IS '操作时间';




