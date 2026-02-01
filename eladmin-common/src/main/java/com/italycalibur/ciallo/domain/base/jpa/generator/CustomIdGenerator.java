package com.italycalibur.ciallo.domain.base.jpa.generator;


import cn.hutool.core.lang.generator.SnowflakeGenerator;
import lombok.val;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.lang.reflect.Method;

/**
 * 自定义主键策略
 * @author dhr
 * @date 2026-02-01 14:20:09
 * @version 1.0
 */ 
public class CustomIdGenerator implements IdentifierGenerator {
    // 初始化雪花算法
    public static final SnowflakeGenerator SNOWFLAKE_GENERATOR = new SnowflakeGenerator();
    // 主键字段名
    public static final String GET_ID_METHOD_NAME = "getId";

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        try {
            // 通过反射获取 getId 方法
            Method getIdMethod = o.getClass().getMethod(GET_ID_METHOD_NAME);
            Class<?> idType = getIdMethod.getReturnType();


            if (idType == Long.class || idType == long.class) {
                // 如果是 Long 类型，使用雪花算法生成 ID
                return SNOWFLAKE_GENERATOR.next();
            } else if (idType == String.class) {
                // 如果是 String 类型，返回 null 表示需要手动赋值
                return null;
            }else {
                throw new UnsupportedOperationException("不支持的主键数据类型：" + idType.getSimpleName());
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("实体类未定义 " + GET_ID_METHOD_NAME + " 方法", e);
        }
    }
}
