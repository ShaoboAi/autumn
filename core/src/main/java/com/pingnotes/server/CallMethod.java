package com.pingnotes.server;

import com.google.common.base.Preconditions;
import com.google.protobuf.Message;

import java.lang.reflect.Method;

public class CallMethod {
    String serviceName;
    Class  clz;
    Object instance;
    String methodName;
    Method method;
    String paramTypeName;
    Message parameter;
    String returnType;

    public String methodSignature(){
        Preconditions.checkNotNull(serviceName);
        Preconditions.checkNotNull(methodName);
        Preconditions.checkNotNull(paramTypeName);
        StringBuilder sbd = new StringBuilder(serviceName)
                .append(".")
                .append(methodName)
                .append("(")
                .append(paramTypeName)
                .append(")");
        return sbd.toString();
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public String getParamTypeName() {
        return paramTypeName;
    }

    public void setParamTypeName(String paramTypeName) {
        this.paramTypeName = paramTypeName;
    }

    public Message getParameter() {
        return parameter;
    }

    public void setParameter(Message parameter) {
        this.parameter = parameter;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
