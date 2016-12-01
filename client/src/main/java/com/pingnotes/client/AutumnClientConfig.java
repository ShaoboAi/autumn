package com.pingnotes.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by shaobo;
 */
@Configuration
public class AutumnClientConfig {
    @Bean
    public AutumnClientDef clientDef(){
        AutumnClientDef def = new AutumnClientDef();
        def.setRegistryAddress("192.168.2.121:3721");
        def.setWorkerThread(4);
        def.setSelector(AutumnClientDef.Selector.RoundRobin);
        return def;
    }

    @Bean
    public Registry registry(){
        Registry r = new Registry();
        r.setConnectorString("192.168.2.121:3721");
        return r;
    }
}
