package com.alix;

import com.pingnotes.server.RemoteService;


/**
 * Created by shaobo
 */

@RemoteService
public class HelloService implements HelloWorldService {

    @Override
    public HelloWorld.HelloWorldResponse say(HelloWorld.HelloWorldRequest helloWorldRequest) {
        return HelloWorld.HelloWorldResponse.newBuilder()
                .setResponseId("respId" + helloWorldRequest.getRequestId()).build();
    }
}
