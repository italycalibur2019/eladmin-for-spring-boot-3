/*
 *  Copyright 2019-2025 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.italycalibur.ciallo.domain.modules.system.repository;

import com.italycalibur.ciallo.domain.base.jpa.repository.BaseRepository;
import com.italycalibur.ciallo.domain.modules.system.entity.Dept;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
* @author Zheng Jie
* @date 2019-03-25
*/
public interface DeptRepository extends BaseRepository<Dept, Long> {

    /**
     * 根据 PID 查询
     * @param id pid
     * @return /
     */
    List<Dept> findByPid(Long id);

    /**
     * 获取顶级部门
     * @return /
     */
    List<Dept> findByPidIsNull();

    /**
     * 根据角色ID 查询
     * @param roleId 角色ID
     * @return /
     */
    @Query(value = "select d.* from sys.td_dept d, sys_roles_depts r where " +
            "d.dept_id = r.dept_id and r.role_id = ?1", nativeQuery = true)
    Set<Dept> findByRoleId(Long roleId);

    /**
     * 判断是否存在子节点
     * @param pid /
     * @return /
     */
    int countByPid(Long pid);

    /**
     * 根据ID更新sub_count
     * @param count /
     * @param id /
     */
    @Modifying
    @Query(value = " update sys.td_dept set sub_count = ?1 where dept_id = ?2 ",nativeQuery = true)
    void updateSubCntById(Integer count, Long id);
}