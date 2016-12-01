package com.pingnotes;

import com.google.protobuf.Message;
import com.pingnotes.discovery.RemoteServiceInstance;
import com.pingnotes.discovery.ServiceRegistry;
import com.pingnotes.server.*;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.UriSpec;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by shaobo.
 */

public class AutumnApplication {
    private ApplicationContext context;
    private ServiceRegistry serviceRegistry;
    private boolean started = false;
    private Server server;
    private ServiceDefinitionContainer container = ServiceDefinitionContainer.getInstance();

    private AutumnApplication(String registry, String basePackage) {
        this.context = new AnnotationConfigApplicationContext(basePackage);
        this.serviceRegistry = new ServiceRegistry(registry);
        this.server = new AutumnServerBootstrap();
    }

    private AutumnApplication(String registry) {
        this.context = new AnnotationConfigApplicationContext(AutumnApplication.class);
        this.serviceRegistry = new ServiceRegistry(registry);
        this.server = new AutumnServerBootstrap();
    }

    private AutumnApplication(AutumnServerDef def) {
        this.context = new AnnotationConfigApplicationContext(def.getServicePackage());
        this.serviceRegistry = new ServiceRegistry(def.getRegistryAddress());
        this.server = new AutumnServerBootstrap(def);
    }

    public ApplicationContext getContext() {
        return context;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public boolean isStarted() {
        return started;
    }

    public Server getServer() {
        return server;
    }

    public ServiceDefinitionContainer getContainer() {
        return container;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public final static class Builder {
        private String[] scanPackage;
        private String registryAddress;
        private int serverPort = 10660;
        private int threadNum = 100;
        private int idleTime = 600;

        public int getThreadNum() {
            return threadNum;
        }

        public Builder setThreadNum(int threadNum) {
            this.threadNum = threadNum;
            return this;
        }

        public int getIdleTime() {
            return idleTime;
        }

        public Builder setIdleTime(int idleTime) {
            this.idleTime = idleTime;
            return this;
        }

        public int getServerPort() {
            return serverPort;
        }

        public Builder setServerPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public String getRegistryAddress() {
            return registryAddress;
        }

        public Builder setRegistryAddress(String registryAddress) {
            this.registryAddress = registryAddress;
            return this;
        }

        public String[] getScanPackage() {
            return scanPackage;
        }

        public Builder setScanPackage(String... scanPackage) {
            this.scanPackage = scanPackage;
            return this;
        }

        public AutumnApplication build() {
            AutumnServerDef def = new AutumnServerDef(registryAddress);
            def.setIdleTime(idleTime);
            def.setThreadNum(threadNum);
            def.setServerPort(serverPort);
            def.setServicePackage(scanPackage);
            return new AutumnApplication(def);
        }
    }

    public void start() {
        if (started) {
            return;
        }
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    close();

                }
            });
            serviceRegistry.start();
            server.start();
            registerServices();
            started = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void close() {
        server.close();
        serviceRegistry.close();
    }

    private void register(List<CallMethod> methods) {
        UriSpec uriSpec = new UriSpec("{scheme}://{address}:{port}");
        methods.forEach(m -> {
            ServiceInstance<RemoteServiceInstance> rsi = null;
            try {
                RemoteServiceInstance payload = new RemoteServiceInstance();
                payload.setMethod(m.getMethodName());
                payload.setRequestType(m.getParamTypeName());
                payload.setReturnType(m.getReturnType());
                payload.setName(m.getServiceName());
                rsi = ServiceInstance.<RemoteServiceInstance>builder()
                        .uriSpec(uriSpec)
                        .address(server.ip())
                        .port(server.port())
                        .name(m.getServiceName())
                        .payload(payload)
                        .serviceType(ServiceType.DYNAMIC)
                        .build();
                serviceRegistry.registerService(rsi);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void registerServices() {
        Map<String, Object> serviceBeans = context.getBeansWithAnnotation(RemoteService.class);
        for (Map.Entry<String, Object> entry : serviceBeans.entrySet()) {
            Class[] interfaces = entry.getValue().getClass().getInterfaces();
            for (Class clz : interfaces) {
                Arrays.asList(clz.getMethods()).forEach(m -> {
                    CallMethod callMethod = new CallMethod();
                    callMethod.setServiceName(clz.getCanonicalName());
                    callMethod.setClz(entry.getValue().getClass());
                    callMethod.setInstance(entry.getValue());
                    callMethod.setMethodName(m.getName());
                    callMethod.setMethod(m);

                    if (m.getParameterCount() != 1) {
                        //todo exception
                    }

                    try {
                        callMethod.setParameter((Message) m.getParameterTypes()[0].getMethod("getDefaultInstance").invoke(null));
                    } catch (Exception e) {
                        //todo never arrive here
                    }
                    callMethod.setParamTypeName(m.getParameterTypes()[0].getName());
                    container.put(callMethod.methodSignature(), callMethod);
                });
            }
        }
        register(container.allMethods());
    }
}
