package com.ygd.rpcclient;

import com.ygd.rpc.common.service.HelloService;
import com.ygd.rpcclient.proxy.RpcProxy;
import com.ygd.rpcclient.scanner.RpcScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RpcBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        RpcScanner rpcScanner = new RpcScanner(registry);
        int count = rpcScanner.scan("com.ygd.rpc.common.service");
        System.out.println(count);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
