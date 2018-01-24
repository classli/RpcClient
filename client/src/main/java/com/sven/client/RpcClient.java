package com.sven.client;

import android.util.Log;

import com.sven.common.bean.RpcRequest;
import com.sven.common.bean.RpcResponse;
import com.sven.common.code.RpcDecoder;
import com.sven.common.code.RpcEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by sven on 17/2/13.
 */

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
    private static final String TAG = "RpcClient";
    private final String host;
    private final int port;

    private RpcResponse response;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.e(TAG,"api caught exception "+cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;
    }

    public RpcResponse send(RpcRequest request) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 客户端 Bootstrap 对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcRequest.class));
                    pipeline.addLast(new RpcDecoder(RpcResponse.class));
                    pipeline.addLast(RpcClient.this);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel =  future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            Log.d(TAG,"client bind closeFuture");
            return response;
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return null;
    }
}
