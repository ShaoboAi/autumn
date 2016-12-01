package com.pingnotes.client;

/**
 * Created by shaobo on 11/25/16.
 */
public class ClientSelector<T> implements ISelector<T>{
    static int pos = 0;

    @Override
    public T select(T[] instances) {
        if (instances == null || instances.length == 0){
            return null;
        }

        pos = pos % instances.length;
        return instances[pos++];
    }
}
