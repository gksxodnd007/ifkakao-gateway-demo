package com.kakaopay.ifkakao.zuul2.repository;

import com.kakaopay.ifkakao.zuul2.constant.HttpMethod;
import com.kakaopay.ifkakao.zuul2.domain.RouteInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hubert.squid
 * @since 2020.07.30
 */
public class RouteRepositoryImpl implements RouteRepository {

    private final Map<Long, RouteInfo> concurrentHashMap;

    public RouteRepositoryImpl() {
        this.concurrentHashMap = new ConcurrentHashMap<>();
        RouteInfo route1 = RouteInfo.newInstance("payment", "localhost:8081", HttpMethod.GET, "/api/ping");
        RouteInfo route2 = RouteInfo.newInstance("money", "localhost:8082", HttpMethod.GET, "/api/ping");
        RouteInfo route3 = RouteInfo.newInstance("genie", "localhost:8083", HttpMethod.GET, "/api/ping");
        this.concurrentHashMap.put(route1.getId(), route1);
        this.concurrentHashMap.put(route2.getId(), route2);
        this.concurrentHashMap.put(route3.getId(), route3);
    }

    @Override
    public Optional<RouteInfo> getRoute(long id) {
        return Optional.ofNullable(this.concurrentHashMap.get(id));
    }

    @Override
    public Optional<RouteInfo> getRoute(String service, HttpMethod method, String path) {
        for (Map.Entry<Long, RouteInfo> longRouteInfoEntry : this.concurrentHashMap.entrySet()) {
            RouteInfo routeInfo = longRouteInfoEntry.getValue();
            if (routeInfo.isMatch(service, method, path)) {
                return Optional.of(routeInfo);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<RouteInfo> getRoutes(String service) {
        List<RouteInfo> list = new ArrayList<>();
        for (Map.Entry<Long, RouteInfo> longRouteInfoEntry : this.concurrentHashMap.entrySet()) {
            RouteInfo routeInfo = longRouteInfoEntry.getValue();
            if (routeInfo.getService().equals(service)) {
                list.add(routeInfo);
            }
        }

        return list;
    }
}
