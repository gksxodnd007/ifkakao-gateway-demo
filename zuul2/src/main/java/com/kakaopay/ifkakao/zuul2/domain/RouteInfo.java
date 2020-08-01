package com.kakaopay.ifkakao.zuul2.domain;

import com.kakaopay.ifkakao.zuul2.constant.HttpMethod;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hubert.squid
 * @since 2020.07.30
 */
public class RouteInfo {

    private static final AtomicLong autoIncrement = new AtomicLong(0);
    private final long id;
    private final String service;
    private final String host;
    private final HttpMethod method;
    private final String path;

    private RouteInfo(long id, String service, String host, HttpMethod method, String path) {
        this.id = id;
        this.service = service;
        this.host = host;
        this.method = method;
        this.path = path;
    }

    public static RouteInfo newInstance(String service, String host, HttpMethod method, String path) {
        long id = autoIncrement.incrementAndGet();
        return new RouteInfo(id, service, host, method, path);
    }

    public long getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getHost() {
        return host;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public boolean isMatch(String service, HttpMethod method, String path) {
        return this.service.equals(service) && this.method == method && this.path.equals(path);
    }
}
