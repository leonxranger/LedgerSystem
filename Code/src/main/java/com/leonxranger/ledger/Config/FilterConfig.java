package com.leonxranger.ledger.Config;

import com.leonxranger.ledger.Filter.Idempotencyfilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<Idempotencyfilter> FilterRegistration (Idempotencyfilter idempotencyfilter){
        FilterRegistrationBean<Idempotencyfilter> filterRegistrationBean = new FilterRegistrationBean<>(idempotencyfilter);

        filterRegistrationBean.addUrlPatterns("/transactions/*");
        filterRegistrationBean.setOrder(1);

        return  filterRegistrationBean;
    }
}
