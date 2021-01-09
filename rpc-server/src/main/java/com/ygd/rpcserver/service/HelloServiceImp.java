package com.ygd.rpcserver.service;

import com.ygd.rpc.common.service.HelloService;
import com.ygd.rpcserver.annotation.RpcService;

@RpcService
public class HelloServiceImp implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
