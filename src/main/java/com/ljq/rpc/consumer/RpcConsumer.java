package com.ljq.rpc.consumer;

import com.ljq.rpc.api.IRpcHelloService;
import com.ljq.rpc.api.IRpcService;
import com.ljq.rpc.consumer.proxy.RpcProxy;

public class RpcConsumer {
    public static void main(String[] args) {
        IRpcHelloService service= RpcProxy.creat(IRpcHelloService.class);
        System.out.println(service.hello("LJQ"));
    }
}
