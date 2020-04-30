



-- 商品及商品类型

-- 外部商品类型与内部商品类型映射关系
-- 商品信息从外部导入 - 映射表
-- 表结构
-- 问题：
--      1. 外部系统的商品类型ID的数据类型可能会因为系统的不同而不同。比如图书ERP 系统是整形，其他ERP 系统是字符串？
--      2. 外部系统的商品类型有哪些字段呢？是跟内部系统的一样吗？

    DROP TABLE IF EXISTS fps_goods_type_outer;
    CREATE TABLE fps_goods_type_outer (
        inner_goods_type_id VARCHAR(36) NOT NULL COMMENT '内部系统商品类型ID，映射到商品类型表的ID 字段',
        outer_goods_type_id VARCHAR(36) NOT NULL COMMENT '外部系统商品类型ID',
        owner_system_id VARCHAR(36) NOT NULL COMMENT '所属外部系统', 
        goods_type_name VARCHAR(60) NOT NULL COMMENT '商品类型名称' 
    ) ENGINE = INNODB COMMENT '外部系统导入的商品类型信息表结构';

-- fps_goods_kinds 商品类别表
-- 商品类别: 一般货物。这个应该在玫举管理里面有定义才对，如果没有。则需要添加一个表。
-- ** 暂时先不用吧
    -- CREATE TABLE fps_goods_kinds (
    --     goods_kinds_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键：商品类别ID',
    --     goods_kinds_name VARCHAR(30) NOT NULL COMMENT '商品类别'
    -- ) ENGINE = INNODB COMMENT '商品类别表';




-- 商品类型表结构
-- 字段说明
--      存储环境: 常温常湿、保鲜、冷冻
--      保质期计算类型: 生产日期、失效日期
--      SN扫码管控: 全部不扫描、进出扫描、入库扫描、出库扫描
--      可新增商品: 如果ERP 系统里面有的(比如图书)就是不允许新增商品，ERP 系统里面没有的才允许新增。
--      需要书籍字段: 图书相关专有的字段。比如版次、出版社
-- #### 问题
--      1. 过期提醒字段，是从生产日期开始讲。还是从到期日那天倒数。

    DROP TABLE IF EXISTS fps_goods_type;
    CREATE TABLE fps_goods_type (
        goods_type_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键：商品类型ID',
        goods_type_name VARCHAR(256) NOT NULL COMMENT '商品类型名称', 
        parent_goods_type_id VARCHAR(36) NULL COMMENT '父ID-对应当前表的goods_type_id字段', 
        batch_ctrl VARCHAR(3) NOT NULL COMMENT '是否批次管控【radio:1-是,2-否】', 
        expire_ctrl VARCHAR(3) NOT NULL COMMENT '是否保质期管控【radio:1-是,2-否】',
        expire_type VARCHAR(3) NULL COMMENT '当保质期管控时有效。保质期计算类型【select:1-生产日期(production date),2-到期日期(expiry date)】', 
        valid_days INTEGER NULL COMMENT '保质期计算类型为生产日期时有效。有效天数',
        warnning_days INTEGER NULL COMMENT '当保质期管控时有效。(警告天数-过期提醒)',
        store_env VARCHAR(20) NOT NULL COMMENT '存储环境-管理定义好的玫举值',
        sn_ctrl VARCHAR(3) NOT NULL COMMENT '序列号扫描管控【radio:1-是,2-否】',
        enable_add VARCHAR(3) NOT NULL COMMENT '是否可新增商品【radio:1-是,2-否】',
        books VARCHAR(3) NOT NULL COMMENT '是否为书籍类商品-是否需要书籍字段【radio:1-是,2-否】',
        goods_kinds_id VARCHAR(20) NOT NULL COMMENT '商品类别：一般货物。这个需要有玫举值定义，或者另外的表结构',
        using_status VARCHAR(3) NOT NULL COMMENT '状态【radio:1-是(启用),2-否(停用)】',
        update_user_id VARCHAR(64) NOT NULL COMMENT '最后修改人',
        update_time DATETIME NOT NULL COMMENT '最后修改时间',
        current_level INTEGER NULL COMMENT '当前商品类型是在当前树结构的第几层。(可能不再需要了)',
        leaf_node INTEGER NULL COMMENT '是否叶子节点-最小分类(这个不确认是否需要)'
    ) ENGINE = INNODB COMMENT '商品类型表';









-- 外部商品详情表结构

    DROP TABLE IF EXISTS fps_goods_info_outer;
    CREATE TABLE fps_goods_info_outer (
        inner_goods_id VARCHAR(36) NOT NULL COMMENT '内部系统商品ID，映射到商品信息表记录',
        outer_goods_id VARCHAR(30) NOT NULL COMMENT '外部系统商品ID',
        owner_system_id VARCHAR(30) NOT NULL COMMENT '所属外部系统', 
        goods_name VARCHAR(60) NOT NULL COMMENT '商品名称' 
    ) ENGINE = INNODB COMMENT '外部系统导入的商品信息表结构';





-- 商品详情表结构
-- #### 问题
--      1. 每一类商品的字段都是不同的吧？如果这样的话，每一种商品都会有对应的表结构了。
--          秦丽解释说所有商品的字段只有这么多，只是除了图书类商品会有专门的字段外，所有商品的字段都是这么些
--      2.1 商品管理里面的商品，是指代的某一类商品，非具体的货物吗？ 比如说 《三国演义》这个商品，然后这个商品属于图书这个商品类型
--      2.2 如果是这样，那么这类商品与货主之间怎么会有对应关系？是指这一类商品(比如《三国演义》)的供应商、客户、承运商都是固定的吗？


    DROP TABLE IF EXISTS fps_goods_info;
    CREATE TABLE fps_goods_info (
        goods_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键：商品ID',
        goods_name VARCHAR(64) NULL COMMENT '商品名称',
        goods_type_id VARCHAR(36) NOT NULL COMMENT '商品类型ID',
        bar_code VARCHAR(30) NULL COMMENT '条形码',
        min_unit VARCHAR(10) NULL COMMENT '最小单位',
        unit_weight INTEGER NULL COMMENT '最小单位重量(单位：克)',
        package_unit VARCHAR(10) NULL COMMENT '包装单位',
        package_weight INTEGER NULL COMMENT '每包重量(单位：克)',
        batch_ctrl VARCHAR(3) NULL COMMENT '是否批次管控【radio:1-是,2-否】', 
        expire_ctrl VARCHAR(3) NULL COMMENT '是否保质期管控【radio:1-是,2-否】',
        expire_type VARCHAR(3) NULL COMMENT '保质期计算类型(当保质期管控时有效)【select:1-生产日期(production date),2-到期日期(expiry date)】', 
        valid_days INTEGER NULL COMMENT '有效天数(保质期计算类型为生产日期时有效)',
        warnning_days INTEGER NULL COMMENT '警告天数-过期提醒(当保质期管控时有效)',
        store_env VARCHAR(20) NULL COMMENT '存储环境-管理定义好的玫举值',
        sn_ctrl VARCHAR(3) NULL COMMENT '序列号扫描管控【radio:1-是,2-否】',
        source_from VARCHAR(3) NULL COMMENT '来源【select:1-人工录入,2-大中专ERP系统,...(等其他外部系统)】',
        outer_goods_id VARCHAR(36) NULL COMMENT '外部系统对应ID',

        -- 图书相关的字段
        version VARCHAR(30) NULL COMMENT '版别',
        format VARCHAR(30) NULL COMMENT '开本',
        std_price VARCHAR(30) NULL COMMENT '定价',
        tax_rate VARCHAR(30) NULL COMMENT '税率',
        publish_date VARCHAR(20) NULL COMMENT '出版日期',  -- 是否有必要使用 DATE 类型来存储这个字段
        edition INTEGER NULL COMMENT '版次'
    ) ENGINE = INNODB COMMENT '商品信息表';







