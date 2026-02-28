package io.github.mocanjie.base.mymvc.configuration;

import io.github.mocanjie.base.mymvc.aspect.RequestParamValidAspect;
import io.github.mocanjie.base.mymvc.privacy.fastjson.PrivacyFastjsonFilter;
import io.github.mocanjie.base.mymvc.privacy.fastjson2.PrivacyFastjson2Filter;
import io.github.mocanjie.base.mymvc.privacy.jackson.PrivacyJacksonModule;
import jakarta.validation.Validator;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
@ConditionalOnWebApplication
@ConditionalOnClass({RequestParamValidAspect.class})
public class MyMvcAutoConfiguration implements BeanPostProcessor, Ordered {

    @Bean
    @ConditionalOnMissingBean
    public RequestParamValidAspect getRequestParamValidAspect(Validator validator){
        return new RequestParamValidAspect(validator);
    }

    @Bean
    @ConditionalOnMissingBean(PrivacyJacksonModule.class)
    @ConditionalOnClass(name = "com.fasterxml.jackson.databind.ObjectMapper")
    public PrivacyJacksonModule privacyJacksonModule() {
        return new PrivacyJacksonModule();
    }

    @Bean
    @ConditionalOnMissingBean(PrivacyFastjson2Filter.class)
    @ConditionalOnClass(name = "com.alibaba.fastjson2.JSON")
    public PrivacyFastjson2Filter privacyFastjson2Filter() {
        return new PrivacyFastjson2Filter();
    }

    @Bean
    @ConditionalOnMissingBean(PrivacyFastjsonFilter.class)
    @ConditionalOnClass(name = "com.alibaba.fastjson.JSON")
    public PrivacyFastjsonFilter privacyFastjsonFilter() {
        return new PrivacyFastjsonFilter();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
