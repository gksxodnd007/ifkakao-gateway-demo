package com.kakaopay.ifkakao.zuul2.repository;

import com.kakaopay.ifkakao.zuul2.constant.HttpMethod;
import com.kakaopay.ifkakao.zuul2.domain.RouteInfo;

import java.util.List;
import java.util.Optional;

/**
 * @author hubert.squid
 * @since 2020.07.30
 */
public interface RouteRepository {

    Optional<RouteInfo> getRoute(long id);
    Optional<RouteInfo> getRoute(String service, HttpMethod method, String path);
    List<RouteInfo> getRoutes(String service);
}
