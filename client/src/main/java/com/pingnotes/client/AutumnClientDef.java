package com.pingnotes.client;

/**
 * Created by shaobo on 11/28/16.
 */
public class AutumnClientDef {
    private Class interfaceClz;
    private String registryAddress;
    private String remoteService;

    private Selector selector;
    private int workerThread;

    private String remoteIp;
    private int port;

    public Class getInterfaceClz() {
        return interfaceClz;
    }

    public void setInterfaceClz(Class interfaceClz) {
        this.interfaceClz = interfaceClz;
        this.remoteService = interfaceClz.getName();
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRemoteService() {
        return remoteService;
    }

    public void setRemoteService(String remoteService) {
        this.remoteService = remoteService;
    }

    public int getWorkerThread() {
        return workerThread;
    }

    public void setWorkerThread(int workerThread) {
        this.workerThread = workerThread;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public enum Selector {
        RoundRobin, Priority
    }

    @Override
    public AutumnClientDef clone() {
        AutumnClientDef def = new AutumnClientDef();
        def.setRegistryAddress(registryAddress);
        def.setRemoteService(remoteService);
        def.setSelector(selector);
        def.setWorkerThread(workerThread);
        def.setRemoteIp(remoteIp);
        def.setPort(port);
        return def;
    }

    public String clientId(){
        return remoteIp + ":" + port;
    }
}
