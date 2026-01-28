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
package com.italycalibur.ciallo.domain.entity;

import com.italycalibur.ciallo.domain.base.jpa.SimpleBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 邮件配置类，数据存覆盖式存入数据存
 * @author Zheng Jie
 * @date 2018-12-26
 */
@Setter
@Getter
@Entity
@Table(name = "tool_email_config")
public class EmailConfig extends SimpleBaseEntity implements Serializable {

    @Id
    @Column(name = "config_id")
    @Schema(description = "ID", hidden = true)
    private Long id;

    @NotBlank
    @Schema(description = "邮件服务器SMTP地址")
    private String host;

    @NotBlank
    @Schema(description = "邮件服务器 SMTP 端口")
    private String port;

    @NotBlank
    @Schema(description = "发件者用户名")
    private String user;

    @NotBlank
    @Schema(description = "密码")
    private String pass;

    @NotBlank
    @Schema(description = "收件人")
    private String fromUser;
}
