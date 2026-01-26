package com.italycalibur.ciallo.domain.config.webConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 将Pageable转换展示在swagger中
 */
@Configuration
public class OpenApiDataConfig {

    @Bean
    public OpenApiCustomizer pageableOpenApiCustomizer() {
        return openApi -> {
            // 添加 Pageable 的替代模式定义
            openApi.getComponents().addSchemas("Page",
                    new io.swagger.v3.oas.models.media.Schema<>()
                            .type("object")
                            .addProperty("page",
                                    new io.swagger.v3.oas.models.media.IntegerSchema()
                                            ._default(0)
                                            .description("页码 (0..N)"))
                            .addProperty("size",
                                    new io.swagger.v3.oas.models.media.IntegerSchema()
                                            ._default(20)
                                            .description("每页显示的数目"))
                            .addProperty("sort",
                                    new io.swagger.v3.oas.models.media.ArraySchema()
                                            .items(new io.swagger.v3.oas.models.media.StringSchema())
                                            .description("以下列格式排序标准：property[,asc | desc]。 默认排序顺序为升序。 支持多种排序条件：如：id,asc")));
        };
    }

    /**
     * 定义 Pageable 对应的模型
     */
    @Schema(description = "分页参数")
    public static class Page {
        @Schema(description = "页码 (0..N)", example = "0", minimum = "0")
        private Integer page;

        @Schema(description = "每页显示的数目", example = "20", minimum = "1")
        private Integer size;

        @Schema(description = "以下列格式排序标准：property[,asc | desc]。 默认排序顺序为升序。 支持多种排序条件：如：id,asc")
        private List<String> sort;
    }
}
