package com.italycalibur.ciallo.domain.base.jpa.repository;

import com.italycalibur.ciallo.domain.base.jpa.entity.SimpleBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @description: Spring JPA 自定义数据访问层
 * @author dhr
 * @date 2026-01-27 11:33:55
 * @version 1.0
 */
@NoRepositoryBean
public interface BaseRepository<E extends SimpleBaseEntity, ID extends Serializable>
        extends JpaRepository<E, ID>, JpaSpecificationExecutor<E>, NativeQueryInterface {
}
