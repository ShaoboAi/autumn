package com.pingnotes.client;

import com.pingnotes.proto.InternalPb;

import java.util.concurrent.TimeUnit;

/**
 * Created by shaobo;
 */
public class AutumnClient implements Client {
    private AutumnClientBootstrap bootstrap;
    private boolean started = false;
    private AutumnClientDef clientDef;

    public AutumnClient(AutumnClientDef clientDef) {
        this.clientDef = clientDef;
        this.bootstrap = new AutumnClientBootstrap(clientDef);
    }

    @Override
    public String getId() {
        return clientDef.clientId();
    }

    @Override
    public void start() throws Exception {
        if (started) {
            return;
        }
        bootstrap.start();
        started = true;
    }

    @Override
    public void stop() {
        bootstrap.close();
    }

    @Override
    public boolean started() {
        return false;
    }

    @Override
    public byte[] call(int uin, String method, byte[] req, long timeout) throws AtmClientException {
        return new byte[0];
    }

    @Override
    public InternalPb.InternalResponse call(int uin, String method, InternalPb.InternalRequest req, long timeout) throws AtmClientException {
        ClientHandler handler = bootstrap.getClientHandler();
        Listener listener = new ResponseListener();
        handler.sendMessageAsync(req, listener);
        InternalPb.InternalResponse response = listener.get(timeout, TimeUnit.SECONDS);
        return response;
    }
}