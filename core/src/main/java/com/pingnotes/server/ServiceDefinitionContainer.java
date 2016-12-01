package com.pingnotes.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;

/**
 * Created by shaobo.
 */
public class ServiceDefinitionContainer {
    private Set<String> services = new HashSet<>();
    private Map<String, CallMethod> methodRepository = new HashMap<>();
    private static ServiceDefinitionContainer instance = new ServiceDefinitionContainer();

    private ServiceDefinitionContainer(){
    }

    public static ServiceDefinitionContainer getInstance(){
        return instance;
    }

    public Set<String> allServices() {
        return ImmutableSet.copyOf(services);
    }

    public void put(String id, CallMethod method) {
        methodRepository.put(id, method);
        services.add(method.getServiceName());
    }

    public CallMethod get(String methodSignature) {
        return methodRepository.get(methodSignature);
    }

    public List<CallMethod> allMethods() {
        return ImmutableList.copyOf(methodRepository.values());
    }
}
