package com.pingnotes.client.proxy;

import com.google.protobuf.Message;
import com.pingnotes.client.AtmClientException;
import com.pingnotes.client.AutumnClientDef;
import com.pingnotes.client.Client;
import com.pingnotes.client.ClientManager;
import com.pingnotes.proto.InternalPb;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by shaobo on 11/8/16.
 */
public class RemoteServiceProxy implements MethodInterceptor {
    private Class interfaceClass;

    private ClientManager client;

    public Class getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public RemoteServiceProxy(AutumnClientDef autumnClientDef) {
        this.interfaceClass = autumnClientDef.getInterfaceClz();
        this.client = new ClientManager(autumnClientDef);
    }

    public void init() {
        client.init();
    }

    public Object buildProxy() {
        Enhancer enhancer = new Enhancer();//该类用于生成代理对象
        enhancer.setInterfaces(new Class[]{this.interfaceClass});
        enhancer.setSuperclass(Object.class);
        enhancer.setCallback(this);//设置回调用对象为本身
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        String requestId = UUID.randomUUID().toString();

        if (params.length > 1){
            //todo this is wrong;
        }
        Message param = (Message)params[0];

        InternalPb.InternalRequest.Builder builder = InternalPb.InternalRequest.newBuilder()
                .setRequestId(requestId)
                .setUin(11)
                .setServiceName(interfaceClass.getCanonicalName())
                .setMethodName(method.getName())
                .setParamType(method.getParameterTypes()[0].getName())
                .setParameter(param.toByteString());

        Client cli;
        if ( (cli=client.selectClient()) == null){
            throw new AtmClientException("No alive server");
        }
        InternalPb.InternalResponse response = cli.call(1, method.getName(), builder.build(), 1000);
        Class returnType = method.getReturnType();
        Object result = returnType.getMethod("parseFrom", byte[].class).invoke(null, response.getResponseContent().toByteArray());
        return result;
    }

}
