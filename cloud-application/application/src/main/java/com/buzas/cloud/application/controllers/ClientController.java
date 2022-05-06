package com.buzas.cloud.application.controllers;

import com.buzas.cloud.application.ClientApp;
import com.buzas.cloud.application.dialogs.Dialogs;
import com.buzas.cloud.application.model.*;
import com.buzas.cloud.application.network.ClientNetwork;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable, Closeable {

    private Path serverDirectory = Path.of("../cloud-server/cloudFiles");
    private Path clientDirectory;
    private ClientApp application;
    public ListView<String> leftNameplate;
    public ListView<String> rightNameplate;
    private ClientNetwork clientNetwork;
    public ListView<String> userView;
    public ListView<String> serverView;

    private void readCommands() {
        try {
            while (true){
                AbstractMessage message = clientNetwork.read();
                if (message instanceof DownloadErrorMessage){
//                    Случай прерывания чтения
                    System.out.println("ERROR WITH DOWNLOAD!");
                    String error = ((DownloadErrorMessage) message).getMessage();
                    if (error.equals("Its a directory, you can't copy it")){
                        System.out.println(error);
                    }
                    if (error.equals("No such file in a server")){
                        System.out.println(error);
                        Dialogs.ErrorDialog.DOWNLOADING_FILES_ERROR.show();
                    }
                }
                if (message instanceof ListMessage listMessage){
                    System.out.println("read messages");
                    serverView.getItems().clear();
                    serverView.getItems().add("..");
                    serverView.getItems().addAll(listMessage.getFiles());
                }
                if (message instanceof DeliverMessage deliverMessage){
                    System.out.println("deliver file");
                    Path deliveredFile = Path.of(deliverMessage.getName());
                    byte[] deliveredBytes = deliverMessage.getBytes();
                    Path requiredPath = clientDirectory.resolve(deliveredFile);

                    Files.write(requiredPath, deliveredBytes);
                    try {
                        readUserFiles();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if (message instanceof DirectoryAnswerMessage answerMessage){
                    if (answerMessage.isDirectory()){
                        serverDirectory = serverDirectory.resolve(answerMessage.getName());
                        serverView.getItems().clear();
                        clientNetwork.write(new RefreshMessage());
                    } else {
                        System.out.println("Not a directory. Да, оно и здесь не дает использовать окно ошибки. Опять не видит сцену");
//                        Dialogs.ErrorDialog.NOT_A_DIRECTORY.show();
                    }
                }
                if (message instanceof InfoDeliverMessage infoDeliverMessage){
                    String fileToRead = infoDeliverMessage.getName();
                    String filePath = infoDeliverMessage.getFilePath();
                    String fileSize = infoDeliverMessage.getFileSize();
                    String fileLastUpdateFull = infoDeliverMessage.getFileLastUpdateFull();
                    String fileLastUpdateDate = fileLastUpdateFull.substring(0, 10);
                    String fileLastUpdateHour = fileLastUpdateFull.substring(12, 19);
                    infoDialogInit(fileToRead, filePath, fileSize, fileLastUpdateDate, fileLastUpdateHour, true);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to read command");
            e.printStackTrace();
        }
    }

    private List<String> receiveUserFilesNames() throws IOException {
        return Files.list(clientDirectory)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
    }

    private void readUserFiles() throws IOException {
        userView.getItems().clear();
        userView.getItems().add("..");
        userView.getItems().addAll(receiveUserFilesNames());
    }

    private void signUpNameplates() {
        leftNameplate.getItems().clear();
        rightNameplate.getItems().clear();
        leftNameplate.getItems().add("User");
        rightNameplate.getItems().add("Server");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            signUpNameplates();
            clientDirectory = Path.of("cloud-application","userFiles");
            clientNetwork = new ClientNetwork();
            readUserFiles();
            Thread.sleep(300);
            Thread commandReadThread = new Thread(this::readCommands);
            commandReadThread.setDaemon(true);
            commandReadThread.start();
        } catch (IOException e) {
            System.err.println("Failed to launch Client Network");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Client was interrupted");
            e.printStackTrace();
        }
    }

    public void fromUser(ActionEvent actionEvent) throws Exception {
        String fileName = userView.getSelectionModel().getSelectedItem();
        Path filePath = clientDirectory.resolve(fileName);
        if (!Files.isDirectory(filePath)){
            clientNetwork.write(new FileMessage(filePath));
        } else {
            Dialogs.ErrorDialog.CANT_COPY_DIRECTORY.show();
        }
    }

    public void fromServer(ActionEvent actionEvent) throws Exception {
        String serverFile = serverView.getSelectionModel().getSelectedItem();
        Path serverFilePath = Path.of(serverFile);
        clientNetwork.download(new DownloadMessage(serverDirectory.resolve(serverFilePath)));
    }

    public void pressExitButton(ActionEvent actionEvent) throws IOException {
        clientNetwork.closeNetwork();
        application.INSTANCE.getPrimaryStage().close();
    }

    public void pressDeleteUserFile(ActionEvent actionEvent) throws IOException {
        String userFile = userView.getSelectionModel().getSelectedItem();
        Path userPath = clientDirectory.resolve(userFile);
        if (Files.exists(userPath)){
            if (Files.isDirectory(userPath)){
                File dir = new File(String.valueOf(userPath));
                File[] filesInDir = dir.listFiles();
                if (filesInDir.length <= 0){
                    Files.delete(userPath);
                } else {
                    for (File file : filesInDir) {
                        file.delete();
                    }
                    Files.delete(userPath);
                }
            } else {
                System.out.println("File at path: " + userPath + " deleted");
                Files.delete(userPath);
            }
            readUserFiles();
        }
    }

    public void pressDeleteServerFile(ActionEvent actionEvent) throws IOException {
        String serverFile = serverView.getSelectionModel().getSelectedItem();
        Path serverPath = Path.of(serverFile);
        clientNetwork.write(new DeleteMessage(serverPath));
    }

    public void pressAbout(ActionEvent actionEvent) {
        Dialogs.AboutDialog.UPDATE.show();
    }

    public void pressRefreshButton(ActionEvent actionEvent) throws IOException {
        readUserFiles();
        clientNetwork.write(new RefreshMessage());
    }

    @Override
    public void close() throws IOException {
        clientNetwork.closeNetwork();
        application.INSTANCE.getPrimaryStage().close();
    }

    public void pressedUserFileInfo(ActionEvent actionEvent) throws IOException {
        String fileToRead = userView.getSelectionModel().getSelectedItem();
        Path filePath = clientDirectory.resolve(fileToRead);
        String stringPath = filePath.toString();
        String fileSize = String.valueOf(Files.size(filePath));
        String fileLastUpdateFull = String.valueOf(Files.getLastModifiedTime(filePath));
        String fileLastUpdateDate = fileLastUpdateFull.substring(0, 10);
        String fileLastUpdateHour = fileLastUpdateFull.substring(12, 19);
        infoDialogInit(fileToRead, stringPath, fileSize, fileLastUpdateDate, fileLastUpdateHour, false);
    }

    public void pressedServerFileInfo(ActionEvent actionEvent) throws IOException {
        String fileToRead = serverView.getSelectionModel().getSelectedItem();
        Path filePath = Path.of(fileToRead);
        clientNetwork.write(new FileInfoMessage(filePath));
    }

    private void infoDialogInit(String fileToRead, String filePath, String fileSize, String fileLastUpdateDate,
                                String fileLastUpdateHour, boolean tagFromServer) {
        String message = "Name is " + fileToRead + "\n" +
                "Path is " + filePath + "\n" +
                "Size is " + fileSize + " bytes\n" +
                "Last update " + fileLastUpdateHour + " of " + fileLastUpdateDate;
        String TITLE = "Info";
        if (!tagFromServer){
            if (!Files.isDirectory(Path.of(filePath))){
                String TYPE = "This is file";
                showDialog(Alert.AlertType.INFORMATION, TITLE, TYPE, message);
            } else {
                String TYPE = "This is directory";
                showDialog(Alert.AlertType.INFORMATION, TITLE, TYPE, message);
            }
        }
        if (tagFromServer) {
            System.out.println("Тут должна быть ошибка. То есть она есть");
//            String TYPE = "This is from server";
//            showDialog(Alert.AlertType.INFORMATION, TITLE, TYPE, message);
        }

    }

    private void showDialog(Alert.AlertType alertType, String title, String type, String message) {
        Alert alert = new Alert(alertType);
        alert.initOwner(application.INSTANCE.getPrimaryStage());
        alert.setTitle(title);
        alert.setHeaderText(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void pressUserFolder(ActionEvent actionEvent) throws IOException {
        String dots = "..";
        String target = userView.getSelectionModel().getSelectedItem();
        Path targetPath = clientDirectory.resolve(target);
        if (target.equals(dots)){
            clientDirectory = Path.of("cloud-application","userFiles");
        }
        if (Files.isDirectory(targetPath)){
            clientDirectory = targetPath;
        } else {
            Dialogs.ErrorDialog.NOT_A_DIRECTORY.show();
        }
        readUserFiles();
    }

    public void pressServerFolder(ActionEvent actionEvent) throws IOException {
        String target = serverView.getSelectionModel().getSelectedItem();
        if (target.equals("..")){
            clientNetwork.write(new DirectoryRequestMessage(".."));
        }
        Path targetPath = serverDirectory.resolve(target);
        clientNetwork.write(new DirectoryRequestMessage(targetPath));
    }
}
