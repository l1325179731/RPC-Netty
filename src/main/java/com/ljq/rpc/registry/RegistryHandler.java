package com.ljq.rpc.registry;

import com.ljq.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryHandler extends ChannelInboundHandlerAdapter {

    private List<String> classNames=new ArrayList<>();
    private Map<String,Object> registryMap=new ConcurrentHashMap<>();
    public RegistryHandler() {
        //1、根据一个包名将所有符合条件的class会部扫描出来，放到一个容器中
        scannerClass("com.ljq.rpc.provider");
        //2、给每一个对应的Class起一个唯一名字，作为服务名称，保存到一个容器中
        doRegistry();
        
    }

    private void doRegistry() {
        if(classNames.isEmpty())
            return;
        else {
            for (String className : classNames) {
                try {
                    Class<?> clazz= Class.forName(className);
                    Class<?> i=clazz.getInterfaces()[0];
                     registryMap.put(i.getName(),clazz.newInstance());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                scannerClass(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replaceAll(".class", ""));
            }
        }
    }

    //有客户端连上的时候回调
    //3、当有客户端连接过来之后，就会获取协议内容 InvokerProtocol的对象
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InvokerProtocol request= (InvokerProtocol) msg;
        Object result=new Object();
        //4、要去注册好的容器中找到符合条件服务
        if(registryMap.containsKey(request.getClassName())){
            Object service=registryMap.get(request.getClassName());
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParams());
             result = method.invoke(service, request.getValues());
        }
        //5、通过远程调用Provider得到返回结果，并回复给客户端
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }


    //连接发生异常的时候回调
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
