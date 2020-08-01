package com.kakaopay.ifkakao.zuul2.filter;

import com.kakaopay.ifkakao.zuul2.constant.FilterOrder;
import com.kakaopay.ifkakao.zuul2.constant.HttpMethod;
import com.kakaopay.ifkakao.zuul2.domain.RouteInfo;
import com.kakaopay.ifkakao.zuul2.repository.RouteRepository;
import com.kakaopay.ifkakao.zuul2.repository.RouteRepositoryImpl;
import com.netflix.zuul.context.SessionContext;
import com.netflix.zuul.filters.http.HttpInboundFilter;
import com.netflix.zuul.message.http.HttpRequestMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import rx.Observable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author hubert.squid
 * @since 2020.07.30
 */
@Component
public class RouteDecisionFilter extends HttpInboundFilter {

    private final RouteRepository repository = new RouteRepositoryImpl();

    @Override
    public int filterOrder() {
        return FilterOrder.ROUTE_DECISION.getOrder();
    }

    @Override
    public Observable<HttpRequestMessage> applyAsync(HttpRequestMessage input) {
        System.out.println(">>> execute RouteDecisionFilter");
        SessionContext context = input.getContext();
        String requestPath = input.getPath();
        HttpMethod method = HttpMethod.valueOf(input.getMethod().toUpperCase());
        String service = requestPath.split("/")[1];
        String strippedPrefixPath = "/"
            + Arrays.stream(StringUtils.tokenizeToStringArray(requestPath, "/"))
            .skip(1).collect(Collectors.joining("/"));

        RouteInfo routeInfo = repository.getRoute(service, method, strippedPrefixPath).orElseThrow(RuntimeException::new);
        try {
            context.setEndpoint(ProxyFilter.class.getCanonicalName());
            context.setRouteHost(new URL("http://" + routeInfo.getHost()));
            context.setRouteVIP(routeInfo.getPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return Observable.just(input);
    }

    @Override
    public boolean shouldFilter(HttpRequestMessage msg) {
        return true;
    }
}
