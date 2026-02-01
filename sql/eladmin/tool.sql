-- =============================================
-- ElAdmin 系统 PostgreSQL tool模式 初始化脚本
-- 兼容 PostgreSQL 17+
-- 生成时间：2026-01-31
-- =============================================

BEGIN;

-- drop schema if exists tool;
create schema tool;

-- tool.td_local_storage
-- drop table if exists tool.td_local_storage;
CREATE TABLE tool.td_local_storage
(
    storage_id  BIGINT PRIMARY KEY,
    real_name   VARCHAR(255),
    name        VARCHAR(255),
    suffix      VARCHAR(255),
    path        VARCHAR(255),
    type        VARCHAR(255),
    size        VARCHAR(100),
    create_by   VARCHAR(255),
    update_by   VARCHAR(255),
    create_time TIMESTAMP,
    update_time TIMESTAMP
);
COMMENT ON TABLE tool.td_local_storage IS '本地存储';
COMMENT ON COLUMN tool.td_local_storage.storage_id IS 'ID';
COMMENT ON COLUMN tool.td_local_storage.real_name IS '文件真实名称';
COMMENT ON COLUMN tool.td_local_storage.name IS '文件名';
COMMENT ON COLUMN tool.td_local_storage.suffix IS '后缀';
COMMENT ON COLUMN tool.td_local_storage.path IS '路径';
COMMENT ON COLUMN tool.td_local_storage.type IS '类型';
COMMENT ON COLUMN tool.td_local_storage.size IS '大小';
COMMENT ON COLUMN tool.td_local_storage.create_by IS '创建者';
COMMENT ON COLUMN tool.td_local_storage.update_by IS '更新者';
COMMENT ON COLUMN tool.td_local_storage.create_time IS '创建日期';
COMMENT ON COLUMN tool.td_local_storage.update_time IS '更新时间';

-- tool.td_s3_storage
-- drop table if exists tool.td_s3_storage;
CREATE TABLE tool.td_s3_storage
(
    storage_id     BIGINT PRIMARY KEY,
    file_name      VARCHAR(255) NOT NULL,
    file_real_name VARCHAR(255) NOT NULL,
    file_size      VARCHAR(100) NOT NULL,
    file_mime_type VARCHAR(50)  NOT NULL,
    file_type      VARCHAR(50)  NOT NULL,
    file_path      TEXT         NOT NULL,
    create_by      VARCHAR(255) NOT NULL,
    update_by      VARCHAR(255) NOT NULL,
    create_time    TIMESTAMP    NOT NULL,
    update_time    TIMESTAMP    NOT NULL
);
COMMENT ON TABLE tool.td_s3_storage IS 's3 协议对象存储';
COMMENT ON COLUMN tool.td_s3_storage.storage_id IS 'ID';
COMMENT ON COLUMN tool.td_s3_storage.file_name IS '文件名';
COMMENT ON COLUMN tool.td_s3_storage.file_real_name IS '文件真实名称';
COMMENT ON COLUMN tool.td_s3_storage.file_size IS '文件大小';
COMMENT ON COLUMN tool.td_s3_storage.file_mime_type IS 'MIME类型';
COMMENT ON COLUMN tool.td_s3_storage.file_type IS '文件类型';
COMMENT ON COLUMN tool.td_s3_storage.file_path IS '文件路径';
COMMENT ON COLUMN tool.td_s3_storage.create_by IS '创建者';
COMMENT ON COLUMN tool.td_s3_storage.update_by IS '更新者';
COMMENT ON COLUMN tool.td_s3_storage.create_time IS '创建日期';
COMMENT ON COLUMN tool.td_s3_storage.update_time IS '更新时间';

-- tool.td_alipay_config
-- drop table if exists tool.td_alipay_config;
CREATE TABLE tool.td_alipay_config
(
    config_id               BIGINT PRIMARY KEY,
    app_id                  VARCHAR(255),
    charset                 VARCHAR(255),
    format                  VARCHAR(255),
    gateway_url             VARCHAR(255),
    notify_url              VARCHAR(255),
    private_key             TEXT,
    public_key              TEXT,
    return_url              VARCHAR(255),
    sign_type               VARCHAR(255),
    sys_service_provider_id VARCHAR(255)
);
COMMENT
    ON TABLE tool.td_alipay_config IS '支付宝配置类';
COMMENT
    ON COLUMN tool.td_alipay_config.config_id IS 'ID';
COMMENT
    ON COLUMN tool.td_alipay_config.app_id IS '应用ID';
COMMENT
    ON COLUMN tool.td_alipay_config.charset IS '编码';
COMMENT
    ON COLUMN tool.td_alipay_config.format IS '类型';
COMMENT
    ON COLUMN tool.td_alipay_config.gateway_url IS '网关地址';
COMMENT
    ON COLUMN tool.td_alipay_config.notify_url IS '异步回调';
COMMENT
    ON COLUMN tool.td_alipay_config.private_key IS '私钥';
COMMENT
    ON COLUMN tool.td_alipay_config.public_key IS '公钥';
COMMENT
    ON COLUMN tool.td_alipay_config.return_url IS '回调地址';
COMMENT
    ON COLUMN tool.td_alipay_config.sign_type IS '签名方式';
COMMENT
    ON COLUMN tool.td_alipay_config.sys_service_provider_id IS '服务商ID';

-- tool.td_email_config
-- drop table if exists tool.td_email_config;
CREATE TABLE tool.td_email_config
(
    config_id BIGINT PRIMARY KEY,
    from_user VARCHAR(255),
    host      VARCHAR(255),
    pass      VARCHAR(255),
    port      VARCHAR(255),
    username  VARCHAR(255)
);
COMMENT
    ON TABLE tool.td_email_config IS '邮箱配置';
COMMENT
    ON COLUMN tool.td_email_config.config_id IS 'ID';
COMMENT
    ON COLUMN tool.td_email_config.from_user IS '收件人';
COMMENT
    ON COLUMN tool.td_email_config.host IS '邮件服务器SMTP地址';
COMMENT
    ON COLUMN tool.td_email_config.pass IS '密码';
COMMENT
    ON COLUMN tool.td_email_config.port IS '端口';
COMMENT
    ON COLUMN tool.td_email_config.username IS '发件者用户名';

-- ========================
-- 插入初始数据
-- ========================
INSERT INTO tool.td_s3_storage (storage_id, file_name, file_real_name, file_size, file_mime_type, file_type, file_path,
                             create_by, update_by, create_time, update_time)
VALUES (4, 'tx.jpg', '2ca1de24d8fa422eae4ede30e97c46d8.jpg', '29.67KB', 'image/jpeg', 'jpg',
        '2025-06/2ca1de24d8fa422eae4ede30e97c46d8.jpg', 'admin', 'admin', '2025-06-25 15:48:22', '2025-06-25 15:48:22');
COMMIT;