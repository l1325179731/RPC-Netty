package com.ljq.rpc.provider;

import com.ljq.rpc.api.IRpcHelloService;

public class RpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String name) {
        return "Hello "+name+" !";
    }
}
