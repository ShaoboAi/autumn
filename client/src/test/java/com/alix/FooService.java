package com.alix;

import com.pingnotes.annotation.RemoteServiceRef;
import org.springframework.stereotype.Service;

/**
 * Created by shaobo on 11/30/16.
 */

@Service
public class FooService {

    @RemoteServiceRef
    HelloWorldService helloWorldService;

    public void foo() {
        HelloWorld.HelloWorldRequest req = HelloWorld.HelloWorldRequest.newBuilder()
                .setMethod("method")
                .setRequestId("1234")
                .build();

        System.out.println(helloWorldService.say(req).getResponseId());
    }

}
