package com.ljq.rpc.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class RpcRegistry {
    private int port;

    public RpcRegistry(int port) {
        this.port = port;
    }
    private void start(){
        //Netty基于NIO来实现
        //Selector 主线程，Work线程

        //初始化主线程池，Selector
        try {


            EventLoopGroup bossGroup = new NioEventLoopGroup();
            //初始化子线程池，具体对应客户端的处理逻辑
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //在Netty中，把所有的业务逻辑处理全部归总到了一个队列中
                            //这个队列中包含了各种各样的处理逻辑，对这些处理逻辑在Netty中有一个封装
                            //封装成了一个对象，无锁化身行任务队列
                            //这个对象就是Pipline
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //就是对我们处理逻辑的封装
                            //对于自定义协议的内容要进行编、解码

                            //解码
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                    0, 4, 0, 4));
                            //编码
                            pipeline.addLast(new LengthFieldPrepender(4));
                            //实参处理
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            //编解码完成了对数据的解析
                            //最后一步，执行自己的逻辑
                            //1、注册，给每一个对象起一个名字，对外提供服务的名字
                            //2、服务位置要做一个登记
                            pipeline.addLast(new RegistryHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //正式启动服务，相当于用一个死循环开始轮询
            ChannelFuture future = server.bind(this.port).sync();
            System.out.println("GP RPC Registry start listen at "+ this.port);
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RpcRegistry(8080).start();
    }
}
