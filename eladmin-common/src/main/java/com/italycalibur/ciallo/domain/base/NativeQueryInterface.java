package com.italycalibur.ciallo.domain.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @description: 原生 SQL 语句查询接口定义
 * @author dhr
 * @date 2026-01-27 11:35:17
 * @version 1.0
 */ 
public interface NativeQueryInterface {
    /**
     * 使用原生 SQL 查询单条数据
     */
    <R> R nativeQuerySingle(String sql, Object... params);

    /**
     * 使用原生 SQL 查询多条数据，返回数组列表
     */
    List<Object[]> nativeQuery(String sql, Object... params);

    /**
     * 使用原生 SQL 带分页查询多条数据，返回数组列表
     */
    Page<Object[]> nativeQuery(String sql, Pageable pageable, Object... params);

    /**
     * 使用原生 SQL 带分页查询多条数据，返回数组列表
     * 使用计数 SQL 预查询，查询数量不为0才会继续查询
     */
    Page<Object[]> nativeQuery(String countSql, String sql, Pageable pageable, Object... params);

    /**
     * 使用原生 SQL 带分页查询多条数据，返回 Map 列表
     */
    List<Map<String, Object>> nativeQueryForMap(String sql, Object... params);

    /**
     * 使用原生 SQL 带分页查询多条数据，返回 Map 列表
     */
    Page<Map<String, Object>> nativeQueryForMap(String sql, Pageable pageable, Object... params);

    /**
     * 使用原生 SQL 带分页查询多条数据，返回 Map 列表
     * 使用计数 SQL 预查询，查询数量不为0才会继续查询
     */
    Page<Map<String, Object>> nativeQueryForMap(String countSql, String sql, Pageable pageable, Object... params);

    /**
     * 使用原生 SQL 带分页查询多条数据，返回 VO 列表
     */
    <R> List<R> nativeQueryForVo(Class<? extends R> clazz, String sql, Object... params);

    /**
     * 使用原生 SQL 带分页查询多条数据，返回 VO 列表
     */
    <R> Page<R> nativeQueryForVo(Class<? extends R> clazz, String sql, Pageable pageable, Object... params);

    /**
     * 使用原生 SQL 带分页查询多条数据，返回 VO 列表
     * 使用计数 SQL 预查询，查询数量不为0才会继续查询
     */
    <R> Page<R> nativeQueryForVo(Class<? extends R> clazz, String countSql, String sql, Pageable pageable, Object... params);

    /**
     * 使用原生 SQL 执行事务操作
     */
    int nativeExecute(String sql, Object... params);

    /**
     * 使用原生 SQL 查询数量
     */
    long nativeCount(String countSql, Object... params);
}
