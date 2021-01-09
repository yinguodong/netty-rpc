package com.ygd.rpcclient.proxy;

import com.ygd.rpc.common.request.RpcRequest;
import com.ygd.rpc.common.response.RpcResponse;
import com.ygd.rpcclient.client.RpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Component
public class RpcProxy implements InvocationHandler {

    @Autowired
    private RpcClient rpcClient;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args){
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameters(args);
        rpcRequest.setParameterTypes(method.getParameterTypes());

        RpcResponse rpcResponse = rpcClient.send(rpcRequest);
        return rpcResponse.getResult();
    }
}
