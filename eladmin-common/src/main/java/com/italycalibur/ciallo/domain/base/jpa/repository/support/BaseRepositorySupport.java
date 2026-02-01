package com.italycalibur.ciallo.domain.base.jpa.repository.support;

import com.italycalibur.ciallo.domain.base.jpa.entity.SimpleBaseEntity;
import com.italycalibur.ciallo.domain.base.jpa.repository.BaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @description: Spring JPA 自定义数据访问层实现
 * @author dhr
 * @date 2026-01-27 14:09:58
 * @version 1.0
 */ 
public class BaseRepositorySupport<E extends SimpleBaseEntity, ID extends Serializable>
        extends SimpleJpaRepository<E, ID> implements BaseRepository<E, ID> {

    // 计数 SQL 格式匹配
    Predicate<String> COUNT_MATCH_PATTERN = Pattern.compile("^\\s{0,3}SELECT\\s{1,3}COUNT.+", Pattern.CASE_INSENSITIVE).asPredicate();

    private final EntityManager entityManager;

    public BaseRepositorySupport(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R nativeQuerySingle(String sql, Object... params) {
        Query query = this.createNativeQuery(sql, params);
        try {
            return (R) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> nativeQuery(String sql, Object... params) {
        Query query = this.createNativeQuery(sql, params);
        return query.getResultList();
    }

    @Override
    public Page<Object[]> nativeQuery(String sql, Pageable pageable, Object... params) {
        return this.nativeQuery(getNativeCountSql(sql), sql, pageable, params);
    }

    @Override
    public Page<Object[]> nativeQuery(String countSql, String sql, Pageable pageable, Object... params) {
        long total = 0L;

        PageQueryPreData<Object[]> pre = this.preCount(countSql, pageable, params);
        if (pre.isEmpty()) {
            return pre.emptyData();
        }
        total = pre.total();

        Query query = this.createNativeQuery(sql, params);
        return pageResult(pageable,query,total);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> nativeQueryForMap(String sql, Object... params) {
        Query query = this.createNativeQuery(Map.class, sql, params);
        return query.getResultList();
    }

    @Override
    public Page<Map<String, Object>> nativeQueryForMap(String sql, Pageable pageable, Object... params) {
        return this.nativeQueryForMap(getNativeCountSql(sql), sql, pageable, params);
    }

    @Override
    public Page<Map<String, Object>> nativeQueryForMap(String countSql, String sql, Pageable pageable, Object... params) {
        long total = 0L;

        PageQueryPreData<Map<String, Object>> pre = this.preCount(countSql, pageable, params);
        if (pre.isEmpty()) {
            return pre.emptyData();
        }
        total = pre.total();

        Query query = this.createNativeQuery(Map.class, sql, params);
        return pageResult(pageable,query,total);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> List<R> nativeQueryForVo(Class<? extends R> clazz, String sql, Object... params) {
        Query query = this.createNativeQuery(clazz, sql, params);
        return query.getResultList();
    }

    @Override
    public <R> Page<R> nativeQueryForVo(Class<? extends R> clazz, String sql, Pageable pageable, Object... params) {
        return this.nativeQueryForVo(clazz, getNativeCountSql(sql), sql, pageable, params);
    }

    @Override
    public <R> Page<R> nativeQueryForVo(Class<? extends R> clazz, String countSql, String sql, Pageable pageable, Object... params) {
        long total = 0L;

        PageQueryPreData<R> pre = this.preCount(countSql, pageable, params);
        if (pre.isEmpty()) {
            return pre.emptyData();
        }
        total = pre.total();

        Query query = this.createNativeQuery(clazz, sql, params);
        return pageResult(pageable,query,total);
    }

    @Override
    public int nativeExecute(String sql, Object... params) {
        Query query = this.createNativeQuery(sql, params);
        int num = query.executeUpdate();
        this.entityManager.flush();
        return num;
    }

    @Override
    public long nativeCount(String countSql, Object... params) {
        String realCountSql = COUNT_MATCH_PATTERN.test(countSql)
                ? countSql : getNativeCountSql(countSql);
        Number count = this.nativeQuerySingle(realCountSql, params);
        return count == null ? 0L : count.longValue();
    }

    /**
     * 预查询总数
     */
    private <R> PageQueryPreData<R> preCount(String countSql, Pageable pageable, Object... params) {
        long total = this.nativeCount(countSql, params); // 查询总数
        PageImpl<R> emptyData = null;
        if (total <= 0) {
            // 没有任何数据,不需要继续查询
            emptyData = new PageImpl<>(Collections.emptyList(), pageable, total);
        }
        return new PageQueryPreData<>(total, emptyData);
    }

    /**
     * 创建原生查询对象
     */
    private Query createNativeQuery(String sql, Object... params) {
        Query query = this.entityManager.createNativeQuery(sql);
        setQueryParameters(query, params);
        return query;
    }

    /**
     * 创建原生查询对象
     */
    @SuppressWarnings("deprecation")
    private Query createNativeQuery(Class<?> resultClass, String sql, Object... args) {
        Query query;
        if (Map.class.isAssignableFrom(resultClass)) {
            // TODO Hibernate 5.2后已经不推荐使用setResultTransformer方法,但是尚未有具体的其他方法实现.
            // 有具体实现方式后,需要修改这里的代码
            query = this.entityManager.createNativeQuery(sql);
            query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        } else {
            query = this.entityManager.createNativeQuery(sql, resultClass);
        }
        setQueryParameters(query, args);
        return query;
    }

    /**
     * 设置查询参数
     */
    private static void setQueryParameters(Query query, Object... params) {
        if (params != null && params.length > 0) {
            if (params.length == 1 && params[0] instanceof Collection) {
                params = ((Collection<?>) params[0]).toArray();
            }
            int i = 1;
            for (Object param : params) {
                query.setParameter(i++, param);
            }
        }
    }

    /**
     * 获取原生统计 SQL
     */
    private static String getNativeCountSql(String sql) {
        return "select count(*) from (" + sql + ") as tmp";
    }

    /**
     * 获取分页结果
     */
    private static <R> PageImpl<R> pageResult(Pageable pageable, Query query, long total) {
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        @SuppressWarnings("unchecked")
        List<R> rows = query.getResultList();
        total = total > 0 ? total : rows.size();
        return new PageImpl<>(rows, pageable, total);
    }

    /**
     * @description: 计数预查询返回对象
     * @author dhr
     * @date 2026-01-27 14:20:42
     * @version 1.0
     *
     * @param total 总数
     * @param emptyData 空数据，total > 0 时返回null
     * @param <T> 泛型
     */
    protected record PageQueryPreData<T> (long total, PageImpl<T> emptyData) {
        public boolean isEmpty() {
            return emptyData != null;
        }
    }
}
