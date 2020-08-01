package com.kakaopay.ifkakao.zuul2.config;

import com.kakaopay.ifkakao.zuul2.filter.ProxyFilter;
import com.kakaopay.ifkakao.zuul2.filter.RouteDecisionFilter;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.config.DynamicIntProperty;
import com.netflix.netty.common.accesslog.AccessLogPublisher;
import com.netflix.netty.common.channel.config.ChannelConfig;
import com.netflix.netty.common.channel.config.CommonChannelConfigKeys;
import com.netflix.netty.common.metrics.EventLoopGroupMetrics;
import com.netflix.netty.common.proxyprotocol.StripUntrustedProxyHeadersHandler;
import com.netflix.netty.common.status.ServerStatusManager;
import com.netflix.spectator.api.Registry;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.FilterUsageNotifier;
import com.netflix.zuul.RequestCompleteHandler;
import com.netflix.zuul.context.SessionContextDecorator;
import com.netflix.zuul.netty.server.BaseServerStartup;
import com.netflix.zuul.netty.server.DirectMemoryMonitor;
import com.netflix.zuul.netty.server.ZuulServerChannelInitializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hubert.squid
 * @since 2020.07.28
 */
@Component
public class Zuul2ServerStartUp extends BaseServerStartup {

    @Autowired
    public Zuul2ServerStartUp(ServerStatusManager serverStatusManager, FilterLoader filterLoader,
                              SessionContextDecorator sessionCtxDecorator, FilterUsageNotifier usageNotifier,
                              RequestCompleteHandler reqCompleteHandler, Registry registry,
                              DirectMemoryMonitor directMemoryMonitor, EventLoopGroupMetrics eventLoopGroupMetrics,
                              ApplicationInfoManager applicationInfoManager, AccessLogPublisher accessLogPublisher) throws Exception {
        super(serverStatusManager, filterLoader, sessionCtxDecorator, usageNotifier, reqCompleteHandler,
            registry, directMemoryMonitor, eventLoopGroupMetrics, null, applicationInfoManager, accessLogPublisher);
        String[] filterClassNames = { RouteDecisionFilter.class.getCanonicalName(), ProxyFilter.class.getCanonicalName() };
        filterLoader.putFiltersForClasses(filterClassNames);
    }

    @Override
    protected Map<Integer, ChannelInitializer> choosePortsAndChannels(ChannelGroup clientChannels) {
        Map<Integer, ChannelInitializer> portsToChannels = new HashMap<>();

        int port = new DynamicIntProperty("zuul.server.port.main", 8080).get();
        String portName = "ifkakao";

        ChannelConfig channelConfig = defaultChannelConfig(portName);
        channelConfig.set(CommonChannelConfigKeys.allowProxyHeadersWhen, StripUntrustedProxyHeadersHandler.AllowWhen.ALWAYS);
        channelConfig.set(CommonChannelConfigKeys.preferProxyProtocolForClientIp, false);
        channelConfig.set(CommonChannelConfigKeys.isSSlFromIntermediary, false);
        channelConfig.set(CommonChannelConfigKeys.withProxyProtocol, false);

        portsToChannels.put(port, new ZuulServerChannelInitializer(port, channelConfig, defaultChannelDependencies(portName), clientChannels));
        logPortConfigured(port, null);

        return portsToChannels;
    }


}
