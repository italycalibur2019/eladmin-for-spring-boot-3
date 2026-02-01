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
package com.italycalibur.ciallo.domain.modules.maint.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.italycalibur.ciallo.domain.base.jpa.annotation.CustomIdGeneratorType;
import com.italycalibur.ciallo.domain.base.jpa.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

/**
* @author zhanghouying
* @date 2019-08-24
*/
@Entity
@Getter
@Setter
@Table(name="td_deploy", schema = "mnt")
public class Deploy extends BaseEntity implements Serializable {

	@Id
	@CustomIdGeneratorType
	@Column(name = "deploy_id")
	@Schema(description = "ID", hidden = true)
    private Long id;

	@ManyToMany
	@Schema(description = "服务器", hidden = true)
	@JoinTable(name = "td_deploy_server", schema = "mnt",
			joinColumns = {@JoinColumn(name = "deploy_id",referencedColumnName = "deploy_id")},
			inverseJoinColumns = {@JoinColumn(name = "server_id",referencedColumnName = "server_id")})
	private Set<ServerDeploy> deploys;

	@ManyToOne
    @JoinColumn(name = "app_id")
	@Schema(description = "应用编号")
    private App app;

    public void copy(Deploy source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
