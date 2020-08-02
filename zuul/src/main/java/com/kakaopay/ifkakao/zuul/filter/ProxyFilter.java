package com.kakaopay.ifkakao.zuul.filter;

import com.kakaopay.ifkakao.zuul.constant.FilterOrder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.springframework.http.HttpMethod.valueOf;

/**
 * @author hubert.squid
 * @since 2020.08.02
 */
@Component
public class ProxyFilter extends ZuulFilter {

    private final RestTemplate restTemplate;
    private final ProxyRequestHelper proxyRequestHelper;

    @Autowired
    public ProxyFilter(RestTemplate restTemplate, ProxyRequestHelper proxyRequestHelper) {
        this.restTemplate = restTemplate;
        this.proxyRequestHelper = proxyRequestHelper;
    }

    @Override
    public String filterType() {
        return FilterConstants.ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterOrder.PROXY_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().getRouteHost() != null && RequestContext.getCurrentContext().sendZuulResponse();
    }

    @Override
    public Object run() throws ZuulException {
        System.out.println(">>> execute proxy filter");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpMethod method = valueOf(request.getMethod().toUpperCase());

        switch (method) {
            case GET:
                try {
                    ResponseEntity<String> response = restTemplate.execute(buildURL(ctx), method, null, restTemplate.responseEntityExtractor(String.class));
                    ctx.set("zuulResponse", response);
                    this.proxyRequestHelper.setResponse(response.getStatusCodeValue(), new ByteArrayInputStream(response.getBody().getBytes()), response.getHeaders());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            case POST:
            case PUT:
            case PATCH:
            case DELETE:
            default:
                break;
        }

        return null;
    }

    private String buildURL(RequestContext ctx) {
        URL routeHost = ctx.getRouteHost();
        return routeHost.getProtocol() + "://" + routeHost.getHost() + ":" + routeHost.getPort() + ctx.get(FilterConstants.REQUEST_URI_KEY);
    }
}
