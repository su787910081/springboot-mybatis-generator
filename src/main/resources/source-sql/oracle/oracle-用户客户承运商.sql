

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




