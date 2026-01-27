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
package com.italycalibur.ciallo.domain.modules.security.config;

import com.italycalibur.ciallo.domain.modules.security.component.JwtAccessDeniedHandler;
import com.italycalibur.ciallo.domain.modules.security.component.JwtAuthenticationEntryPoint;
import com.italycalibur.ciallo.domain.modules.security.component.TokenFilter;
import com.italycalibur.ciallo.domain.modules.security.component.TokenProvider;
import lombok.RequiredArgsConstructor;
import com.italycalibur.ciallo.domain.modules.security.service.OnlineUserService;
import com.italycalibur.ciallo.domain.utils.AnonTagUtils;
import com.italycalibur.ciallo.domain.enums.RequestMethodEnum;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import java.util.*;

/**
 * @author Zheng Jie
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SpringSecurityConfig {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final ApplicationContext applicationContext;
    private final SecurityProperties properties;
    private final OnlineUserService onlineUserService;

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // 去除 ROLE_ 前缀
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 密码加密方式
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // 获取匿名标记
        Map<String, Set<String>> anonymousUrls = AnonTagUtils.getAnonymousUrl(applicationContext);
        return httpSecurity
                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                .addFilter(corsFilter)
                // 授权异常
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationErrorHandler)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                // 防止iframe 造成跨域
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // 不创建会话
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 路径权限配置
                .authorizeHttpRequests(auth -> auth
                        // 静态资源等等
                        .requestMatchers(
                                HttpMethod.GET,
                                "/*.html",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js",
                                "/webSocket/**"
                        ).permitAll()
                        // swagger 文档
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/*/api-docs").permitAll()
                        // 文件
                        .requestMatchers("/avatar/**").permitAll()
                        .requestMatchers("/file/**").permitAll()
                        // 阿里巴巴 druid
                        .requestMatchers("/druid/**").permitAll()
                        // 放行OPTIONS请求
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 自定义匿名访问所有url放行：允许匿名和带Token访问，细腻化到每个 Request 类型
                        // GET
                        .requestMatchers(HttpMethod.GET, anonymousUrls.get(RequestMethodEnum.GET.getType()).toArray(new String[0])).permitAll()
                        // POST
                        .requestMatchers(HttpMethod.POST, anonymousUrls.get(RequestMethodEnum.POST.getType()).toArray(new String[0])).permitAll()
                        // PUT
                        .requestMatchers(HttpMethod.PUT, anonymousUrls.get(RequestMethodEnum.PUT.getType()).toArray(new String[0])).permitAll()
                        // PATCH
                        .requestMatchers(HttpMethod.PATCH, anonymousUrls.get(RequestMethodEnum.PATCH.getType()).toArray(new String[0])).permitAll()
                        // DELETE
                        .requestMatchers(HttpMethod.DELETE, anonymousUrls.get(RequestMethodEnum.DELETE.getType()).toArray(new String[0])).permitAll()
                        // 所有类型的接口都放行
                        .requestMatchers(anonymousUrls.get(RequestMethodEnum.ALL.getType()).toArray(new String[0])).permitAll()
                        // 所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 添加 JWT 配置
                .addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    protected TokenFilter tokenFilter() {
        return new TokenFilter(tokenProvider, properties, onlineUserService);
    }
}
