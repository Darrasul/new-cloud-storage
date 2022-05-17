package handlers;

import com.buzas.cloud.application.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private Path serverDirectory = Path.of("cloud-server/cloudFiles");

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
        if (message instanceof RefreshMessage refreshMessage){
            ctx.write(new ListMessage(serverDirectory));
        }
        if (message instanceof DeleteMessage deleteMessage){
            Files.delete(serverDirectory.resolve(deleteMessage.getName()));
            ctx.write(new ListMessage(serverDirectory));
        }
        if (message instanceof DownloadMessage downloadMessage){
            Path downloadedFilePath = serverDirectory.resolve(downloadMessage.getName());
            if (!Files.isDirectory(downloadedFilePath)){
                if (Files.exists(downloadedFilePath)) {
                    ctx.write(new DeliverMessage(downloadedFilePath));
                } else if (!Files.exists(downloadedFilePath)){
                    ctx.write(new DownloadErrorMessage("No such file in a server"));
                }
            } else {
                ctx.write(new DownloadErrorMessage("Its a directory, you can't copy it"));
            }
        }
        if (message instanceof FileInfoMessage fileInfoMessage){
            Path requestedFile = Path.of(fileInfoMessage.getName());
            ctx.write(new InfoDeliverMessage(serverDirectory.resolve(requestedFile)));
        }
        if (message instanceof DirectoryRequestMessage requestMessage){
            Path targetPath = serverDirectory.resolve(requestMessage.getName());
            if (requestMessage.getName().equals("..")){
                if (serverDirectory.equals(Path.of("cloud-server/cloudFiles"))){
                    ctx.write(new DirectoryAnswerMessage(true, serverDirectory, true));
                }
                serverDirectory = targetPath;
                ctx.write(new DirectoryAnswerMessage(true, serverDirectory, false));
            } else if (Files.isDirectory(targetPath)){
                serverDirectory = targetPath;
                ctx.write(new DirectoryAnswerMessage(true, serverDirectory, false));
            } else {
                ctx.write(new DirectoryAnswerMessage(false, serverDirectory, false));
            }
        }
        if (message instanceof RenameRequestMessage requestMessage){
            Path oldPath = serverDirectory.resolve(requestMessage.getOldName());
            Files.move(oldPath, oldPath.resolveSibling(requestMessage.getNewName()));
            if (Files.exists(serverDirectory.resolve(requestMessage.getNewName()))){
                ctx.write(new RenameAnswerMessage(true));
            } else {
                ctx.write(new RenameAnswerMessage(false));
            }
        }
        ctx.flush();
    }
}
