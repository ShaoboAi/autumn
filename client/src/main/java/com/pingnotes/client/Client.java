package com.pingnotes.client;


import com.pingnotes.proto.InternalPb;

/**
 * mail: pd-shaobo@qq.com
 */
public interface Client {
    String getId();
    void start() throws Exception;
    void stop();
    boolean started();

    byte[] call(int uin, String method, byte[] req, long timeout) throws AtmClientException;
    InternalPb.InternalResponse call(int uin, String method, InternalPb.InternalRequest req, long timeout) throws AtmClientException;
}
