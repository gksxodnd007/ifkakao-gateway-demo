package com.kakaopay.ifkakao.zuul.constant;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @author hubert.squid
 * @since 2020.08.02
 */
public enum FilterOrder {

    ROUTE_DECISION_FILTER(FilterConstants.PRE_DECORATION_FILTER_ORDER - 1),
    PROXY_FILTER(FilterConstants.RIBBON_ROUTING_FILTER_ORDER - 1);

    private final int order;

    FilterOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
