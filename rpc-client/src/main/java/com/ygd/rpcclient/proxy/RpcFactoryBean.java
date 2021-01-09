package com.ygd.rpcclient.proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

public class RpcFactoryBean<T> implements FactoryBean<T> {
    private Class<T> interfaceClass;

    @Autowired
    private RpcProxy rpcProxy;

    public RpcFactoryBean(Class<T> interfaceClass){
        this.interfaceClass = interfaceClass;
    }

    @Override
    public T getObject(){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, rpcProxy);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }
}
