package com.pingnotes.client;

/**
 * Created by shaobo on 11/29/16.
 */
public class AtmClientException extends Exception {
    public AtmClientException() {
        super();
    }

    public AtmClientException(String message) {
        super(message);
    }

    public AtmClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public AtmClientException(Throwable cause) {
        super(cause);
    }
}
