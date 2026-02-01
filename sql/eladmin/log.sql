-- =============================================
-- ElAdmin 系统 PostgreSQL logback模式 初始化脚本
-- 兼容 PostgreSQL 17+
-- 生成时间：2026-01-31
-- =============================================

begin;

-- drop schema if exists logback;
create schema logback;

-- logback.td_system_log
-- drop table if exists logback.td_system_log;
CREATE TABLE logback.td_system_log
(
    log_id           BIGINT PRIMARY KEY,
    description      VARCHAR(255),
    log_type         VARCHAR(10) NOT NULL,
    method           VARCHAR(255),
    params           TEXT,
    request_ip       VARCHAR(255),
    time             BIGINT,
    username         VARCHAR(255),
    address          VARCHAR(255),
    browser          VARCHAR(255),
    exception_detail TEXT,
    create_time      TIMESTAMP   NOT NULL
);
CREATE INDEX idx_sys_log_create_time ON logback.td_system_log (create_time);
CREATE INDEX idx_sys_log_log_type ON logback.td_system_log (log_type);
COMMENT
    ON TABLE logback.td_system_log IS '系统日志';
COMMENT
    ON COLUMN logback.td_system_log.log_id IS 'ID';
COMMENT
    ON COLUMN logback.td_system_log.description IS '操作描述';
COMMENT
    ON COLUMN logback.td_system_log.log_type IS '日志类型';
COMMENT
    ON COLUMN logback.td_system_log.method IS '方法名';
COMMENT
    ON COLUMN logback.td_system_log.params IS '参数';
COMMENT
    ON COLUMN logback.td_system_log.request_ip IS '请求ip';
COMMENT
    ON COLUMN logback.td_system_log.time IS '执行时长';
COMMENT
    ON COLUMN logback.td_system_log.username IS '操作用户';
COMMENT
    ON COLUMN logback.td_system_log.address IS '操作地址';
COMMENT
    ON COLUMN logback.td_system_log.browser IS '浏览器';
COMMENT
    ON COLUMN logback.td_system_log.exception_detail IS '异常详情';
COMMENT
    ON COLUMN logback.td_system_log.create_time IS '创建日期';

-- logback.td_quartz_log
CREATE TABLE logback.td_quartz_log
(
    log_id           BIGINT PRIMARY KEY,
    bean_name        VARCHAR(255),
    cron_expression  VARCHAR(255),
    is_success       BOOLEAN,
    job_name         VARCHAR(255),
    method_name      VARCHAR(255),
    params           VARCHAR(255),
    time             BIGINT,
    exception_detail TEXT,
    create_time      TIMESTAMP
);
COMMENT
    ON TABLE logback.td_quartz_log IS '定时任务日志';
COMMENT
    ON COLUMN logback.td_quartz_log.log_id IS 'ID';
COMMENT
    ON COLUMN logback.td_quartz_log.bean_name IS 'Spring Bean名称';
COMMENT
    ON COLUMN logback.td_quartz_log.cron_expression IS 'cron 表达式';
COMMENT
    ON COLUMN logback.td_quartz_log.is_success IS '执行结果';
COMMENT
    ON COLUMN logback.td_quartz_log.job_name IS '任务名称';
COMMENT
    ON COLUMN logback.td_quartz_log.method_name IS '方法名称';
COMMENT
    ON COLUMN logback.td_quartz_log.params IS '参数';
COMMENT
    ON COLUMN logback.td_quartz_log.time IS '耗时（毫秒）';
COMMENT
    ON COLUMN logback.td_quartz_log.exception_detail IS '异常详情';
COMMENT
    ON COLUMN logback.td_quartz_log.create_time IS '创建日期';

-- ========================
-- 插入初始数据
-- ========================

commit;