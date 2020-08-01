package com.kakaopay.ifkakao.zuul2;

import com.kakaopay.ifkakao.zuul2.config.Zuul2ServerStartUp;
import com.netflix.config.ConfigurationManager;
import com.netflix.zuul.netty.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hubert.squid
 * @since 2020.07.29
 */
@Component
public class Bootstrap {

    private final Zuul2ServerStartUp zuul2ServerStartup;

    @Autowired
    public Bootstrap(Zuul2ServerStartUp zuul2ServerStartUp) {
        this.zuul2ServerStartup = zuul2ServerStartUp;
    }

    public void start() {
        int exitCode = 0;

        try {
            ConfigurationManager.loadCascadedPropertiesFromResources("application");
            this.zuul2ServerStartup.server().start(true);
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("###############");
            System.err.println("Zuul2 Sample: initialization failed. Forcing shutdown now.");
            System.err.println("###############");
            exitCode = 1;
        } finally {
            // server shutdown
            if (this.zuul2ServerStartup.server() != null) {
                this.zuul2ServerStartup.server().stop();
            }

            System.exit(exitCode);
        }
    }

    public void stop() {
        this.zuul2ServerStartup.server().stop();
    }
}
