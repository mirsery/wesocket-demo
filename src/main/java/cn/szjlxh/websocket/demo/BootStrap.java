package cn.szjlxh.websocket.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class BootStrap {

	public void run() {

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap().group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class).childHandler(new WebSocketServerInitializer())
				.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
		
		try {
			ChannelFuture future = b.bind("127.0.0.1", 8080).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			System.out.println("WebsocketChatServer 关闭了");
		}
	}

	public static void main(String[] args) {
		new BootStrap().run();
	}
}
