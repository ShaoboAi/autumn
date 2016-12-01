package com.pingnotes.discovery;

import com.pingnotes.misc.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by shaobo on 11/14/16.
 */
public class ServiceSearcher {
    private ServiceDiscovery<RemoteServiceInstance> discovery;
    private List<Closeable> closeableList = new ArrayList<>();
    private volatile boolean started = false;
    private ServiceCache<RemoteServiceInstance> serviceCache;

    public ServiceSearcher(CuratorFramework curatorFramework, String remoteServiceName, ServiceCacheListener listener) {
        this.discovery = ServiceDiscoveryBuilder.builder(RemoteServiceInstance.class)
                .client(curatorFramework)
                .serializer(new JsonInstanceSerializer<>(RemoteServiceInstance.class))
                .basePath(Constants.ZkBasePath)
                .build();
        this.serviceCache = this.discovery.serviceCacheBuilder()
                .name(remoteServiceName)
                .build();
        this.serviceCache.addListener(listener);
    }

    public ServiceSearcher(String registryAddress, String remoteServiceName, ServiceCacheListener listener) {
        CuratorFramework curator = CuratorFrameworkFactory.newClient(registryAddress, new ExponentialBackoffRetry(1000, 3));
        curator.start();
        this.discovery = ServiceDiscoveryBuilder.builder(RemoteServiceInstance.class)
                .client(curator)
                .serializer(new JsonInstanceSerializer<>(RemoteServiceInstance.class))
                .basePath(Constants.ZkBasePath)
                .build();
        this.serviceCache = this.discovery.serviceCacheBuilder()
                .name(remoteServiceName)
                .build();
        this.serviceCache.addListener(listener);
    }

    public void start() throws Exception {
        if (!started) {
            this.discovery.start();
            this.serviceCache.start();
            started = true;
        }
    }

    public List<ServiceInstance<RemoteServiceInstance>> getInstances(){
        return this.serviceCache.getInstances();
    }

    public Collection<String> listServices() throws Exception {
        return this.discovery.queryForNames();
    }

    public synchronized void close() {
        closeableList.forEach(CloseableUtils::closeQuietly);
    }
}
