package com.pingnotes.server;

/**
 * mail: pd-shaobo@qq.com
 */
public interface Server {
    String ip();
    int port();
    void start() throws Exception;
    void close();
    boolean isStarted();
}
