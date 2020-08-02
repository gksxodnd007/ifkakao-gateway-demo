package com.kakaopay.ifkakao.zuul.filter;

import com.kakaopay.ifkakao.zuul.constant.FilterOrder;
import com.kakaopay.ifkakao.zuul.repository.RouteRepository;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * @author hubert.squid
 * @since 2020.08.02
 */
@Component
public class RouteDecisionFilter extends ZuulFilter {

    private final UrlPathHelper urlPathHelper;
    private final RouteRepository routeRepository;

    @Autowired
    public RouteDecisionFilter(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
        this.urlPathHelper = new UrlPathHelper();
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.ROUTE_DECISION_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        System.out.println(">>> execute route decision filter");
        RequestContext ctx = RequestContext.getCurrentContext();
        String path = this.urlPathHelper.getPathWithinApplication(ctx.getRequest());
        Optional<Route> route = routeRepository.getRoute(path);

        route.ifPresent(e -> {
            ctx.set(FilterConstants.REQUEST_URI_KEY, e.getPath());
            ctx.set(FilterConstants.PROXY_KEY, e.getPrefix());
            ctx.set(FilterConstants.RETRYABLE_KEY, e.getRetryable());
            ctx.set(FilterConstants.SERVICE_ID_KEY, e.getLocation());
            try {
                ctx.setRouteHost(new URL(e.getLocation()));
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
            }
        });

        return null;
    }
}
