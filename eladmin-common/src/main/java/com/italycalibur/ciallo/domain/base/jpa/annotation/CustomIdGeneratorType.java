package com.italycalibur.ciallo.domain.base.jpa.annotation;


import com.italycalibur.ciallo.domain.base.jpa.generator.CustomIdGenerator;
import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义主键策略注解
 * @author dhr
 * @date 2026-02-01 18:13:51
 * @version 1.0
 */
@IdGeneratorType(CustomIdGenerator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface CustomIdGeneratorType {
}
