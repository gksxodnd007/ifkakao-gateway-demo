package com.kakaopay.ifkakao.zuul.config;

import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hubert.squid
 * @since 2020.08.02
 */
@Configuration
@EnableZuulServer
public class ZuulConfig {

    @Bean
    public ProxyRequestHelper proxyRequestHelper(ZuulProperties zuulProperties) {
        return new ProxyRequestHelper(zuulProperties);
    }
}
