import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import serialization.SerializationPipeLine;

@Slf4j
public class ServerApp {
    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup light = new NioEventLoopGroup(1);
        EventLoopGroup hard = new NioEventLoopGroup();
        try {
            bootstrap.group(light, hard)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SerializationPipeLine());

            ChannelFuture channelFuture = bootstrap.bind(8189).sync();
            log.info("Server online");
            channelFuture.channel()
                    .closeFuture()
                    .sync();
        } catch (Exception e) {
            log.error("troubles with bootstrap", e);
            e.printStackTrace();
        } finally {
            light.shutdownGracefully();
            hard.shutdownGracefully();
        }
    }
}
