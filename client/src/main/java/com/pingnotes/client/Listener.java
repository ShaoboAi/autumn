package com.pingnotes.client;

import com.pingnotes.proto.InternalPb;

import java.util.concurrent.TimeUnit;

/**
 * Created by shaobo on 11/8/16.
 */
public interface Listener {
    void onReceive(InternalPb.InternalResponse response);
    InternalPb.InternalResponse get(long timeout, TimeUnit timeUnit) throws AtmClientException;
    InternalPb.InternalResponse get() throws AtmClientException;
}