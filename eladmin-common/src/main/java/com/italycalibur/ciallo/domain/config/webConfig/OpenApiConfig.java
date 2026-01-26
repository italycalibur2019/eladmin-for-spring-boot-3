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
package com.italycalibur.ciallo.domain.config.webConfig;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * api页面 /doc.html
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${swagger.enabled}")
    private Boolean enabled;

    @Bean
    public OpenAPI openApi() {
        OpenAPI openAPI = new OpenAPI()
                .info(apiInfo());
        if (enabled) {
            openAPI
                    .addSecurityItem(new SecurityRequirement().addList(tokenHeader))
                    .schemaRequirement(tokenHeader, new SecurityScheme()
                            .name(tokenHeader)
                            .type(SecurityScheme.Type.APIKEY)
                            .in(SecurityScheme.In.HEADER));
        }
        return openAPI;
    }

    private Info apiInfo() {
        return new Info()
                .title("ELADMIN 接口文档")
                .description("一个简单且易上手的 Spring boot 后台管理框架")
                .version("3.0.0-SNAPSHOT");
    }
}

