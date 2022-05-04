package handlers;

import com.buzas.cloud.application.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private final Path serverDirectory = Path.of("cloud-server/cloudFiles");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ListMessage message = new ListMessage(serverDirectory);
        ctx.writeAndFlush(message);
    }

    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
        log.info("received : {} message", message.getMessageType().getName());
        if (message instanceof FileMessage fileMessage){
            Files.write(serverDirectory.resolve(fileMessage.getName()), fileMessage.getBytes());
            ctx.write(new ListMessage(serverDirectory));
        }
        if (message instanceof DeleteMessage deleteMessage){
            Files.delete(serverDirectory.resolve(deleteMessage.getName()));
            ctx.write(new ListMessage(serverDirectory));
        }
        if (message instanceof DownloadMessage downloadMessage){
            Path downloadedFilePath = Path.of(serverDirectory.resolve(downloadMessage.getName()).toString());
            if (Files.exists(downloadedFilePath)) {
                ctx.write(new DeliverMessage(downloadedFilePath));
            } else if (!Files.exists(downloadedFilePath)){
                ctx.write(new DownloadErrorMessage());
            }
        }
        ctx.flush();
    }
}
