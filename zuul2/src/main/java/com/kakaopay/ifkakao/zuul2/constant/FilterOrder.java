package com.kakaopay.ifkakao.zuul2.constant;

/**
 * @author hubert.squid
 * @since 2020.07.30
 */
public enum FilterOrder {

    ROUTE_DECISION(5),
    FORWARD(10),
    PROXY(20);

    private final int order;

    FilterOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }
}
