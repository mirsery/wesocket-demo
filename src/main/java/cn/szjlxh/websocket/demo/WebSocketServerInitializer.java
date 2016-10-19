package cn.szjlxh.websocket.demo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.WriteTimeoutHandler;

public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline piple = ch.pipeline();
		
		piple.addLast("decoder",new HttpRequestDecoder());
		piple.addLast("objectAggergator",new HttpObjectAggregator(65536));
		piple.addLast("encoder",new HttpResponseEncoder());
		piple.addLast("writeTimeout",new WriteTimeoutHandler(10));
	    piple.addLast("handler", new PulishServerChannelHandler());
	    
	}
	  

}
