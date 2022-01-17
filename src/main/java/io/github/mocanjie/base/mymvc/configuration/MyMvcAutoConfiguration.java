package io.github.mocanjie.base.mymvc.configuration;

import io.github.mocanjie.base.mymvc.aspect.RequestParamValidAspect;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;

@Configuration
public class MyMvcAutoConfiguration implements BeanPostProcessor, Ordered {

    @Bean
    @Primary
    public RequestParamValidAspect getRequestParamValidAspect(){
        return new RequestParamValidAspect();
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
