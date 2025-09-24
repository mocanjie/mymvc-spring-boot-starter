package io.github.mocanjie.base.mymvc.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson配置 - 确保忽略未知属性
 * @author mo
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonConfig {

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
                .featuresToDisable(
                    // 忽略未知属性，避免前端传递多余字段时报错
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    // 忽略空对象
                    DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES
                )
                .build();
    }
}