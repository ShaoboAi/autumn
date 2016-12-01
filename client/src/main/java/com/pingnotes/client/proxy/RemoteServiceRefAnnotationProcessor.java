package com.pingnotes.client.proxy;

import com.pingnotes.annotation.RemoteServiceRef;
import com.pingnotes.client.AutumnClientDef;
import com.pingnotes.client.Registry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;

/**
 * Created by shaobo;
 */
@Component
public class RemoteServiceRefAnnotationProcessor implements BeanPostProcessor {
    @Autowired
    private Registry registry;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            RemoteServiceRef rsrAnnotation = field.getAnnotation(RemoteServiceRef.class);
            if (rsrAnnotation == null) {
                return;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                throw new IllegalStateException("@RemoteServiceRef annotation is not support on static fields");
            }
            ReflectionUtils.makeAccessible(field);
            AutumnClientDef def = new AutumnClientDef();
            def.setInterfaceClz(field.getType());
            def.setWorkerThread(rsrAnnotation.workerThreads());
            def.setRegistryAddress(registry.getConnectorString());
            RemoteServiceProxy proxy = new RemoteServiceProxy(def);
            proxy.init();
            field.set(bean, proxy.buildProxy());

        });
        return bean;
    }
}
