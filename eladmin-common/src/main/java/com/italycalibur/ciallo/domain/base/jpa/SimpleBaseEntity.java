package com.italycalibur.ciallo.domain.base.jpa;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Field;

/**
 * @description: 通用 实体基类
 * @author dhr
 * @date 2026-01-28 10:41:56
 * @version 1.0
 */
@Getter
@Setter
@MappedSuperclass
public abstract class SimpleBaseEntity {

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        Field[] fields = this.getClass().getDeclaredFields();
        try {
            for (Field f : fields) {
                f.setAccessible(true);
                builder.append(f.getName(), f.get(this)).append("\n");
            }
        } catch (Exception e) {
            builder.append("toString builder encounter an error");
        }
        return builder.toString();
    }
}
