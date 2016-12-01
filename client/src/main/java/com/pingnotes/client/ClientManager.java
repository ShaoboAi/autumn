package com.pingnotes.client;

import com.pingnotes.discovery.RemoteServiceInstance;
import com.pingnotes.discovery.ServiceSearcher;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shaobo.
 */
public class ClientManager {
    private final static Logger log = LoggerFactory.getLogger(ClientManager.class);
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Map<String, Client> clientMap = new HashMap<>();
    private ServiceSearcher serviceSearcher;
    private ISelector<Client> selector;
    private AutumnClientDef clientDef;

    public ClientManager(AutumnClientDef clientDef) {
        this.clientDef = clientDef;
        this.selector = new ClientSelector<>();
        this.serviceSearcher = new ServiceSearcher(clientDef.getRegistryAddress(), clientDef.getRemoteService(), new SCListener());
    }

    private Client buildClient(ServiceInstance<RemoteServiceInstance> instance) {
        AutumnClientDef def = clientDef.clone();
        def.setPort(instance.getPort());
        def.setRemoteIp(instance.getAddress());
        return new AutumnClient(def);
    }

    public void init() {
        try {
            serviceSearcher.start();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("service discovery err", e);
        }
        List<ServiceInstance<RemoteServiceInstance>> instances = serviceSearcher.getInstances();
        List<Client> clients = new ArrayList<>();
        for (ServiceInstance<RemoteServiceInstance> ins : instances) {
            clients.add(buildClient(ins));
        }
        reload(clients);
    }

    public synchronized Client selectClient() {
        Collection values = clientMap.values();
        Client[] clients = new Client[values.size()];
        Iterator<Client> iter = values.iterator();
        for (int i = 0; i < values.size(); i++) {
            clients[i] = iter.next();
        }
        return selector.select(clients);
    }

    public synchronized Set<String> getClientIds() {
        return clientMap.keySet();
    }

    public synchronized List<Client> getClients() {
        return (List<Client>) clientMap.values();
    }

    public synchronized void reload(List<Client> clients) {
        if (clientMap.isEmpty()) {
            clients.forEach(client -> clientMap.put(client.getId(), client));
        } else {
            Map<String, Client> newClients = new HashMap<>();
            for (Client c : clients) {
                if (clientMap.containsKey(c.getId())) {
                    newClients.put(c.getId(), clientMap.get(c.getId()));
                } else {
                    newClients.put(c.getId(), c);
                }
            }
            this.clientMap = newClients;
        }

        this.clientMap.values().forEach(client -> {
            try {
                client.start();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("client start err. ", e);
            }
        });
    }

    public synchronized boolean exists(String id) {
        return clientMap.containsKey(id);
    }

    public synchronized Client remove(String id) {
        return clientMap.remove(id);
    }

    public synchronized Client get(String id) {
        return this.clientMap.get(id);
    }

    private static String clientId(ServiceInstance<RemoteServiceInstance> instance) {
        return instance.getAddress() + ":" + instance.getPort();
    }

    private class SCListener implements ServiceCacheListener {
        @Override
        public void cacheChanged() {
            List<ServiceInstance<RemoteServiceInstance>> instances = serviceSearcher.getInstances();
            List<Client> clients = new ArrayList<>();
            List<String> clientIds = new ArrayList<>();
            for (ServiceInstance<RemoteServiceInstance> ins : instances) {
                String cid = clientId(ins);
                clientIds.add(cid);
                if (!exists(cid)) {
                    clients.add(buildClient(ins));
                }
            }
            reload(clients);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    Set<String> oldClients = new HashSet<>(getClientIds());
                    oldClients.removeAll(clientIds);
                    Client cli;
                    for (String id : oldClients) {
                        if ((cli = remove(id)) != null) {
                            cli.stop();
                        }
                    }
                }
            });
        }

        @Override
        public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        }
    }
}
