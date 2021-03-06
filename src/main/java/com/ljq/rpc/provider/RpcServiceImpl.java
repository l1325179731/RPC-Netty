package com.ljq.rpc.provider;

import com.ljq.rpc.api.IRpcService;

public class RpcServiceImpl implements IRpcService {

    @Override
    public int add(int a, int b) {
        return a+b;
    }

    @Override
    public int sub(int a, int b) {
        return a-b;
    }

    @Override
    public int multi(int a, int b) {
        return a* b;
    }

    @Override
    public int div(int a, int b) {
        return a/b;
    }
}
