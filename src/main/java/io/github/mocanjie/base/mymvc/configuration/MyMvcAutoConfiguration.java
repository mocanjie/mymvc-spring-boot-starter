package io.github.mocanjie.base.mymvc.configuration;

import io.github.mocanjie.base.mymvc.service.IBaseService;
import io.github.mocanjie.base.mymvc.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MyMvcAutoConfiguration {


    @Bean
    @Primary
    public IBaseService getBaseService(){
        return new BaseServiceImpl();
    }

}
