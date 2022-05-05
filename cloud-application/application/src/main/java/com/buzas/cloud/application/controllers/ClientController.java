package com.buzas.cloud.application.controllers;

import com.buzas.cloud.application.ClientApp;
import com.buzas.cloud.application.dialogs.Dialogs;
import com.buzas.cloud.application.model.*;
import com.buzas.cloud.application.network.ClientNetwork;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    private final Path serverDirectory = Path.of("../cloud-server/cloudFiles");
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
                    Dialogs.ErrorDialog.DOWNLOADING_FILES_ERROR.show();
                }
                if (message instanceof ListMessage listMessage){
                    System.out.println("read messages");
                    serverView.getItems().clear();
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
        clientNetwork.write(new FileMessage(clientDirectory.resolve(fileName)));
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
            System.out.println("File at path: " + userPath + " deleted");
            Files.delete(userPath);
            readUserFiles();
        }
    }

    public void pressDeleteServerFile(ActionEvent actionEvent) throws IOException {
        String serverFile = serverView.getSelectionModel().getSelectedItem();
        Path serverPath = serverDirectory.resolve(serverFile);
        clientNetwork.write(new DeleteMessage(serverPath));
    }

    public void pressAbout(ActionEvent actionEvent) {
        Dialogs.AboutDialog.UPDATE.show();
    }

    public void pressRefreshButton(ActionEvent actionEvent) throws IOException {
        readUserFiles();
        clientNetwork.write(new RefreshMessage());
    }
}
