package com.pingnotes.client;

import com.google.common.util.concurrent.SettableFuture;
import com.pingnotes.proto.InternalPb;

import java.util.concurrent.TimeUnit;

/**
 * Created by shaobo on 11/8/16.
 */
public class ResponseListener implements Listener {
    SettableFuture<InternalPb.InternalResponse> future = SettableFuture.create();

    @Override
    public void onReceive(InternalPb.InternalResponse response) {
        future.set(response);
    }

    public InternalPb.InternalResponse get(long timeout, TimeUnit timeUnit) throws AtmClientException {
        try {
            return future.get(timeout, timeUnit);
        } catch (Exception e) {
            throw new AtmClientException(e);
        }
    }

    public InternalPb.InternalResponse get() throws AtmClientException {
        try {
            return future.get();
        } catch (Exception e) {
            throw new AtmClientException(e);
        }
    }
}
