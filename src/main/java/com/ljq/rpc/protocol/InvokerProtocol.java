package com.ljq.rpc.protocol;

import java.io.Serializable;

public class InvokerProtocol implements Serializable {
        private String className; //服务名
        private String methodName;//方法名
        private Class<?>[] params;// 方法形参列表

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParams() {
        return params;
    }

    public void setParams(Class<?>[] params) {
        this.params = params;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    private Object [] values ;// 实参列表
}
