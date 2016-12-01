package com.pingnotes.server;

/**
 * mail: pd-shaobo@qq.com
 * created by shaobo on 10/10/16.
 */
public class AutumnServerDef {
    private String[] servicePackage;
    private String registryAddress;
    private int serverPort = 10660;
    private int threadNum = 100;
    private int idleTime = 600;

    public String[] getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(String[] servicePackage) {
        this.servicePackage = servicePackage;
    }

    public AutumnServerDef(String address) {
        this.registryAddress = address;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
