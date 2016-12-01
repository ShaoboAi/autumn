package com.pingnotes.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shaobo.
 */
public class RequestContext {
    private Map<String, Listener> responseMap = new ConcurrentHashMap<>();

    public void put(String requestId, Listener listener) {
        responseMap.put(requestId, listener);
    }

    public Listener get(String requestId) {
        return responseMap.get(requestId);
    }

    public Listener remove(String requestId) {
        return responseMap.remove(requestId);
    }
}
