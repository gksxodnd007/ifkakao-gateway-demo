package com.kakaopay.ifkakao.gateway.repository;

import com.kakaopay.ifkakao.gateway.predicate.CustomRoutePredicateFactory;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hubert.squid
 * @since 2020.08.03
 */
@Repository
public class CustomRouteDefinitionRepository implements RouteDefinitionRepository {

    private final Map<String, RouteDefinition> concurrentHashMap;


    public CustomRouteDefinitionRepository() {
        this.concurrentHashMap = new ConcurrentHashMap<>();
        this.concurrentHashMap.put("ifkakao", newInstance("ifkakao"));
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routes = new ArrayList<>();
        for (Map.Entry<String, RouteDefinition> entry : concurrentHashMap.entrySet()) {
            routes.add(entry.getValue());
        }

        return Flux.fromIterable(routes);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(e -> {
            concurrentHashMap.put(e.getId(), e);
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            concurrentHashMap.remove(id);
            return Mono.empty();
        });
    }

    private static RouteDefinition newInstance(String service) {
        try {
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setName(CustomRoutePredicateFactory.KEY);
            FilterDefinition filter = new FilterDefinition();
            Map<String, String> filterArgs = new HashMap<>();
            filterArgs.put(StripPrefixGatewayFilterFactory.PARTS_KEY, Integer.toString(1));
            filter.setName("StripPrefix");
            filter.setArgs(filterArgs); // Config property 초기화에 사용되는 값

            RouteDefinition route = new RouteDefinition();
            route.setId(service);
            route.setUri(new URI("http://localhost:8081"));
            route.getPredicates().add(predicate);
            route.getFilters().add(filter);

            return route;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
