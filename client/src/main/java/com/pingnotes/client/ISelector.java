package com.pingnotes.client;

/**
 * Created by shaobo.
 */
public interface ISelector<T> {
    T select(T[] instances);
}
