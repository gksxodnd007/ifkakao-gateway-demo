package com.kakaopay.ifkakao.gateway.locator;

import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import reactor.core.publisher.Flux;

/**
 * @author hubert.squid
 * @since 2020.08.03
 */
public class CustomRouteLocator implements Ordered, RouteLocator,
    ApplicationListener<RefreshRoutesEvent>, ApplicationEventPublisherAware {

    private final RouteLocator delegate;

    public CustomRouteLocator(RouteLocator routeLocator) {
        this.delegate = routeLocator;
    }

    @Override
    public Flux<Route> getRoutes() {
        return this.delegate.getRoutes();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        //TODO set publisher
    }

    @Override
    public void onApplicationEvent(RefreshRoutesEvent event) {
        //TODO refresh
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
