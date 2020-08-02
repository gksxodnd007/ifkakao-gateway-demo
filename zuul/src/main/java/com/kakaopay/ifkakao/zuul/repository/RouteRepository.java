package com.kakaopay.ifkakao.zuul.repository;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hubert.squid
 * @since 2020.08.02
 */
@Repository
public class RouteRepository implements RouteLocator {

    private final Map<String, Route> concurrentHashMap;
    private final ZuulProperties zuulProperties;

    public RouteRepository(ZuulProperties zuulProperties) {
        this.zuulProperties = zuulProperties;
        this.concurrentHashMap = new ConcurrentHashMap<>();
        this.concurrentHashMap.put("ifkakao", new Route("ifkakao", "/api/ping", "http://localhost:8081", "/ifkakao", false, null));
    }

    @Override
    public Collection<String> getIgnoredPaths() {
        return zuulProperties.getIgnoredPatterns();
    }

    @Override
    public List<Route> getRoutes() {
        List<Route> routes = new ArrayList<>();
        for (Map.Entry<String, Route> entry : concurrentHashMap.entrySet()) {
            routes.add(entry.getValue());
        }
        return routes;
    }

    /**
     * 사용하면 안되는 함수. 하단 함수 참고
     *
     * 참고 링크: https://github.com/spring-cloud/spring-cloud-netflix/pull/2579
     */
    @Override
    public Route getMatchingRoute(String path) {
        throw new UnsupportedOperationException("This method should not be invoked");
    }

    public Optional<Route> getRoute(String path) {
        for (Map.Entry<String, Route> entry : this.concurrentHashMap.entrySet()) {
            Route route = entry.getValue();
            if (route.getFullPath().equals(path)) {
                return Optional.of(route);
            }
        }

        return Optional.empty();
    }
}
