package com.kakaopay.ifkakao.zuul2.config;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.CloudInstanceConfig;
import com.netflix.appinfo.PropertiesInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.netty.common.accesslog.AccessLogPublisher;
import com.netflix.netty.common.metrics.EventLoopGroupMetrics;
import com.netflix.netty.common.status.ServerStatusManager;
import com.netflix.spectator.api.DefaultRegistry;
import com.netflix.spectator.api.Registry;
import com.netflix.zuul.*;
import com.netflix.zuul.context.SessionContextDecorator;
import com.netflix.zuul.context.ZuulSessionContextDecorator;
import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.init.ZuulFiltersModule;
import com.netflix.zuul.netty.server.ClientRequestReceiver;
import com.netflix.zuul.netty.server.DirectMemoryMonitor;
import com.netflix.zuul.origins.BasicNettyOrigin;
import com.netflix.zuul.origins.BasicNettyOriginManager;
import com.netflix.zuul.origins.OriginManager;
import com.netflix.zuul.stats.BasicRequestMetricsPublisher;
import com.netflix.zuul.stats.RequestMetricsPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author hubert.squid
 * @since 2020.07.28
 */
@Configuration
public class Zuul2Config extends ZuulFiltersModule {

    private final ZuulFilterFinder zuulFilterFinder = new ZuulFilterFinder();

    @Bean
    public ApplicationInfoManager applicationInfoManager() {
        return new ApplicationInfoManager(new PropertiesInstanceConfig() {}, (ApplicationInfoManager.OptionalArgs) null);
    }

    @Bean
    public Registry registry() {
        return new DefaultRegistry();
    }

    @Bean
    public ServerStatusManager serverStatusManager(ApplicationInfoManager applicationInfoManager) {
        return new ServerStatusManager(applicationInfoManager, null);
    }

    @Bean
    public SessionContextDecorator sessionContextDecorator(OriginManager<BasicNettyOrigin> originManager) {
        return new ZuulSessionContextDecorator(originManager);
    }

    @Bean
    public OriginManager<BasicNettyOrigin> originManager(Registry registry) {
        return new BasicNettyOriginManager(registry);
    }

    @Bean
    public FilterFileManager filterFileManager(FilterLoader filterLoader) {
        return new FilterFileManager(zuulFilterFinder.newFilterFileManagerConfig(), filterLoader);
    }

    @Bean
    public RequestCompleteHandler requestCompleteHandler() {
        return new BasicRequestCompleteHandler();
    }

    @Bean
    public RequestMetricsPublisher requestMetricsPublisher() {
        return new BasicRequestMetricsPublisher();
    }

    @Bean
    public DynamicCodeCompiler dynamicCodeCompiler() {
        return new GroovyCompiler();
    }

    @Bean
    public FilterUsageNotifier filterUsageNotifier() {
        return new BasicFilterUsageNotifier();
    }

    @Bean
    public EventLoopGroupMetrics eventLoopGroupMetrics(Registry registry) {
        return new EventLoopGroupMetrics(registry);
    }

    @Bean
    public DirectMemoryMonitor directMemoryMonitor() {
        return new DirectMemoryMonitor();
    }

    @Bean
    public AccessLogPublisher accessLogPublisher() {
        return new AccessLogPublisher("ifkakao", (channel, httpRequest) -> ClientRequestReceiver.getRequestFromChannel(channel).getContext().getUUID());
    }

    @Bean
    public FilterLoader filterLoader(DynamicCodeCompiler dynamicCodeCompiler) {
        return new FilterLoader(new FilterRegistry(), dynamicCodeCompiler, new DefaultFilterFactory());
    }
}
