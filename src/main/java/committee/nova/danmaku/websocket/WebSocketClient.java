package committee.nova.danmaku.websocket;

import committee.nova.danmaku.Danmaku;
import committee.nova.danmaku.site.ISite;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketClient {
    public static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();
    private final ISite site;
    private final URI uri;
    private Channel channel;

    public WebSocketClient(ISite site) {
        this.site = site;
        this.uri = URI.create(site.getUri());
    }

    public void open() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        SslContext sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        EventLoopGroup group = new NioEventLoopGroup();
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13,
                null, false, EmptyHttpHeaders.INSTANCE);
        WebSocketClientHandler handler = new WebSocketClientHandler(handshaker, site);

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipe = ch.pipeline();
                        pipe.addLast(
                                sslCtx.newHandler(ch.alloc(), uri.getHost(), uri.getPort()),
                                new HttpClientCodec(),
                                new HttpObjectAggregator(8192),
                                handler
                        );
                    }
                });

        channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
        handler.handshakeFuture().sync();

        site.initMessage(this);
        Danmaku.HEART_BEAT_TASK = SERVICE.scheduleAtFixedRate(() -> sendMessage(site.getHeartBeat()),
                site.getHeartBeatInterval(), site.getHeartBeatInterval(), TimeUnit.MILLISECONDS);
    }

    public void close() throws InterruptedException {
        channel.writeAndFlush(new CloseWebSocketFrame());
        channel.closeFuture().sync();
        Danmaku.HEART_BEAT_TASK.cancel(true);
    }

    public void sendMessage(final ByteBuf binaryData) {
        channel.writeAndFlush(new BinaryWebSocketFrame(binaryData));
    }
}
