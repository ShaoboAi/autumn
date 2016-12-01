package com.pingnotes.discovery;

import com.pingnotes.misc.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

/**
 * Created by shaobo.
 */
public class ServiceRegistry {
    private ServiceDiscovery<RemoteServiceInstance> discovery;
    private CuratorFramework client;

    public ServiceRegistry(String registryAdress) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAdress, new ExponentialBackoffRetry(1000, 3));
        this.client = client;
        this.discovery = ServiceDiscoveryBuilder.builder(RemoteServiceInstance.class)
                .serializer(new JsonInstanceSerializer<>(RemoteServiceInstance.class))
                .basePath(Constants.ZkBasePath)
                .client(client)
                .build();
    }

    public ServiceRegistry(CuratorFramework client) {
        this.client = client;
        this.discovery = ServiceDiscoveryBuilder.builder(RemoteServiceInstance.class)
                .serializer(new JsonInstanceSerializer<>(RemoteServiceInstance.class))
                .basePath(Constants.ZkBasePath)
                .client(client)
                .build();
    }

    public void start() throws Exception {
        this.client.start();
        this.discovery.start();
    }

    public void registerService(String serviceName) throws Exception {
        UriSpec uriSpec = new UriSpec("{scheme}://{ip}:{port}");
        RemoteServiceInstance rsi = new RemoteServiceInstance();
        rsi.setName("ai qin");
        ServiceInstance instance = ServiceInstance.<RemoteServiceInstance>builder()
                .name(serviceName)
                .payload(rsi)
                .port(Constants.serverPort)
                .uriSpec(uriSpec)
                .build();

        this.registerService(instance);
    }


    public void registerService(ServiceInstance<RemoteServiceInstance> serviceInstance) throws Exception {
        this.discovery.registerService(serviceInstance);
    }

    public void unregisterService(ServiceInstance<RemoteServiceInstance> serviceInstance) throws Exception {
        this.discovery.unregisterService(serviceInstance);
    }

    public void updateService(ServiceInstance<RemoteServiceInstance> serviceInstance) throws Exception {
        this.discovery.updateService(serviceInstance);
    }

    public void close() {
        CloseableUtils.closeQuietly(discovery);
        CloseableUtils.closeQuietly(client);
    }
}
