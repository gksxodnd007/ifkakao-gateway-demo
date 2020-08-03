package com.kakaopay.ifkakao.gateway.config;

import com.kakaopay.ifkakao.gateway.locator.CustomRouteLocator;
import org.springframework.cloud.gateway.route.CompositeRouteLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author hubert.squid
 * @since 2020.08.03
 */
@Configuration
public class GatewayConfig {

    @Bean
    @Primary
    public RouteLocator cachedCompositeRouteLocator(List<RouteLocator> routeLocators) {
        System.out.println(">>> cachedCompositeRouteLocator init bean");
        return new CustomRouteLocator(new CompositeRouteLocator(Flux.fromIterable(routeLocators)));
    }
}
