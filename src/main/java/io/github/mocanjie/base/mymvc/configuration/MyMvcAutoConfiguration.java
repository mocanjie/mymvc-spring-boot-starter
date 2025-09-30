package io.github.mocanjie.base.mymvc.configuration;

import io.github.mocanjie.base.mymvc.aspect.RequestParamValidAspect;
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


    @Override
    public int getOrder() {
        return 0;
    }
}
