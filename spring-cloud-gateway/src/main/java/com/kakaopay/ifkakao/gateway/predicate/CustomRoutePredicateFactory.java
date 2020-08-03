package com.kakaopay.ifkakao.gateway.predicate;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Predicate;

/**
 * @author hubert.squid
 * @since 2020.08.03
 */
@Component
public class CustomRoutePredicateFactory extends AbstractRoutePredicateFactory<CustomRoutePredicateFactory.Config> {

    public static final String KEY = CustomRoutePredicateFactory.class.getSimpleName();

    public CustomRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return exchange -> true; //route를 찾는 데 사용되는 조건, 이게 true면 route가 결정되는 것임.
    }

    @Override
    public String name() {
        return KEY;
    }

    public static class Config {

    }
}
