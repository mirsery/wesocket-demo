package cn.szjlxh.websocket.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class PulishServerChannelHandler extends SimpleChannelInboundHandler<Object> {
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("some one connect");
	}

	@SuppressWarnings("static-access")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("msg:" + msg.getClass());
		if (msg instanceof HttpRequest) {
			HttpHeaders headers = ((HttpRequest) msg).headers();
			HttpRequest request = (HttpRequest) msg;

			// List<Map.Entry<String, String>> ls = headers.entries();
			// for (Map.Entry<String, String> i : ls) {
			// System.out.println(i.getKey() + ":" + i.getValue());
			// }

			if (!request.getMethod().equals(HttpMethod.GET) || !"websocket".equalsIgnoreCase(headers.get("Upgrade"))) {
				DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
						HttpResponseStatus.BAD_REQUEST);
				ctx.writeAndFlush(response);
				ctx.close();
			}

			// System.out.println("protocol : " + headers.get(HttpHeaders.Names.SEC_WEBSOCKET_VERSION));

			WebSocketServerHandshakerFactory wsShakerFactory = new WebSocketServerHandshakerFactory(
					"ws://" + headers.get(HttpHeaders.Names.HOST), headers.get(HttpHeaders.Names.WEBSOCKET_PROTOCOL),
					false);
			WebSocketServerHandshaker wsShakerHandler = wsShakerFactory.newHandshaker(request);
			if (wsShakerHandler == null) {
				// 无法处理的websocket版本
				wsShakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			} else {
				// 向客户端发送websocket握手,完成握手
				// 客户端收到的状态是101 sitching protocol
				// System.out.println("webscoket version : " + wsShakerHandler.version().toString());
				wsShakerHandler.handshake(ctx.channel(), (FullHttpRequest) request);
			}
		} else if (msg instanceof WebSocketFrame) {
			WebSocketFrame request = (WebSocketFrame) msg;
			
			if (request instanceof CloseWebSocketFrame) {
				System.out.println("关闭套接字");
				ctx.close();
			} else if (request instanceof PingWebSocketFrame) {
				ctx.writeAndFlush(new PongWebSocketFrame(request.content()));
			} else if (request instanceof TextWebSocketFrame) {
				TextWebSocketFrame txtReq = (TextWebSocketFrame) request;
				System.out.println("txtReq:" + txtReq.text());
				// 向websocket客户端发送多个响应
				for (int i = 1; i <= 20; i++) {
					ctx.writeAndFlush(new TextWebSocketFrame("hello word" + i));
					try {
						Thread.sleep(300);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else {
				System.out.println(" - - - - - -");
			}
		}

	}

}
