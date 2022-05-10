package com.buzas.cloud.application.controllers;

import com.buzas.cloud.application.ClientApp;
import com.buzas.cloud.application.dialogs.Dialogs;
import com.buzas.cloud.application.model.TableItem;
import com.buzas.cloud.application.model.*;
import com.buzas.cloud.application.network.ClientNetwork;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable, Closeable {

    private Path serverDirectory = Path.of("../cloud-server/cloudFiles");
    private Path clientDirectory;
    private ClientApp application;
    public ListView<String> leftNameplate;
    public ListView<String> rightNameplate;
    private ClientNetwork clientNetwork;

//    public ListView serverView;
    public TableView<TableItem> userTable;
    public TableView<TableItem> serverTable;
    public TableColumn<TableItem, String> userFileNames;
    public TableColumn<TableItem, String> userFileSizes;
    public TableColumn<TableItem, String> serverFileNames;
    public TableColumn<TableItem, String> serverFileSizes;
    private TableView.TableViewSelectionModel<TableItem> userSelectionModel;
    private TableView.TableViewSelectionModel<TableItem> serverSelectionModel;

    Label userLabel = new Label();
    ContextMenu userMenu = new ContextMenu();
    MenuItem userDeleteFile = new MenuItem("Delete file");
    MenuItem userOpenFolder = new MenuItem("Open folder");
    MenuItem userGetInfo = new MenuItem("Info");
    MenuItem userRefresh = new MenuItem("Refresh list");

    Label serverLabel = new Label();
    ContextMenu serverMenu = new ContextMenu();
    MenuItem serverDeleteFile = new MenuItem("Delete file");
    MenuItem serverOpenFolder = new MenuItem("Open folder");
    MenuItem serverGetInfo = new MenuItem("Info");
    MenuItem serverRefresh = new MenuItem("Refresh list");

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
                    List<String> itemsNames = listMessage.getFileNames();
                    List<String> itemsSizes = listMessage.getFileSizes();
                    List<TableItem> items = new ArrayList<>(0);
                    for (int i = 0; i < itemsNames.size(); i++) {
                        for (int j = 0; j < itemsSizes.size(); j++) {
                            if (i == j){
                                items.add(new TableItem(itemsNames.get(i), itemsSizes.get(j)));
                            }
                        }
                    }
                    serverTable.getItems().clear();
                    serverTable.getItems().add(new TableItem("..", ""));
                    serverTable.getItems().addAll(items);
                    rightNameplate.getItems().clear();
                    rightNameplate.getItems().add(serverDirectory.toString());
//                    serverView.getItems().clear();
//                    serverView.getItems().add("..");
//                    serverView.getItems().addAll(listMessage.getFiles());
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
                        serverTable.getItems().clear();
//                        serverView.getItems().clear();
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

    private List<TableItem> receiveUserFiles() throws IOException {
        List<String> names = Files.list(clientDirectory)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
        List<TableItem> files = new ArrayList<>(0);
        for (String name : names) {
            String size = String.valueOf(Files.size(clientDirectory.resolve(name)));
            files.add(new TableItem(name, size));
        }
        return files;
    }

    private void readUserFiles() throws IOException {
        userTable.getItems().clear();
        userTable.getItems().add(new TableItem("..", ""));
        userTable.getItems().addAll(receiveUserFiles());
//        userView.getItems().clear();
//        userView.getItems().add("..");
//        userView.getItems().addAll(receiveUserFilesNames());
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
            clientDirectory = Path.of("cloud-application","userFiles");
            clientNetwork = new ClientNetwork();
            signUpNameplates();

            userFileNames.setCellValueFactory(new PropertyValueFactory<>("name"));
            userFileSizes.setCellValueFactory(new PropertyValueFactory<>("size"));
            serverFileNames.setCellValueFactory(new PropertyValueFactory<>("name"));
            serverFileSizes.setCellValueFactory(new PropertyValueFactory<>("size"));

            userSelectionModel = userTable.getSelectionModel();
            serverSelectionModel = serverTable.getSelectionModel();
            userSelectionModel.setSelectionMode(SelectionMode.SINGLE);
            serverSelectionModel.setSelectionMode(SelectionMode.SINGLE);

            serverTable.getItems().clear();
            serverTable.getItems().add(new TableItem("..", ""));
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

    private void deleteUserFiles(String userFile) throws IOException {
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

    private void userInfoShow(String fileToRead) throws IOException {
        Path filePath = clientDirectory.resolve(fileToRead);
        String stringPath = filePath.toString();
        String fileSize = String.valueOf(Files.size(filePath));
        String fileLastUpdateFull = String.valueOf(Files.getLastModifiedTime(filePath));
        String fileLastUpdateDate = fileLastUpdateFull.substring(0, 10);
        String fileLastUpdateHour = fileLastUpdateFull.substring(12, 19);
        infoDialogInit(fileToRead, stringPath, fileSize, fileLastUpdateDate, fileLastUpdateHour, false);
    }

    private void openUserFolder(String target) throws IOException {
        String dots = "..";
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

    private void openServerFolder(String target) throws IOException {
        if (target.equals("..")){
            clientNetwork.write(new DirectoryRequestMessage("../cloud-server/cloudFiles"));
        }
        Path targetPath = serverDirectory.resolve(target);
        clientNetwork.write(new DirectoryRequestMessage(targetPath));
    }

    private void deleteServerFile(String serverFile) throws IOException {
        Path serverPath = Path.of(serverFile);
        clientNetwork.write(new DeleteMessage(serverPath));
    }

    private void serverInfoShow(String fileToRead) throws IOException {
        Path filePath = Path.of(fileToRead);
        clientNetwork.write(new FileInfoMessage(filePath));
    }

    public void fromUser(ActionEvent actionEvent) throws Exception {
        String fileName = userSelectionModel.getSelectedItem().getName();
        Path filePath = clientDirectory.resolve(fileName);
        if (!Files.isDirectory(filePath)){
            clientNetwork.write(new FileMessage(filePath));
        } else {
            Dialogs.ErrorDialog.CANT_COPY_DIRECTORY.show();
        }
    }

    public void fromServer(ActionEvent actionEvent) throws Exception {
        String serverFile = serverSelectionModel.getSelectedItem().getName();
        Path serverFilePath = Path.of(serverFile);
        clientNetwork.download(new DownloadMessage(serverDirectory.resolve(serverFilePath)));
    }

    public void pressExitButton(ActionEvent actionEvent) throws IOException {
        clientNetwork.closeNetwork();
        application.INSTANCE.getPrimaryStage().close();
    }

    public void pressDeleteUserFile(ActionEvent actionEvent) throws IOException {
        String userFile = userSelectionModel.getSelectedItem().getName();
        deleteUserFiles(userFile);
    }

    public void pressDeleteServerFile(ActionEvent actionEvent) throws IOException {
        String serverFile = serverSelectionModel.getSelectedItem().getName();
        deleteServerFile(serverFile);
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
        String fileToRead = userSelectionModel.getSelectedItem().getName();
        userInfoShow(fileToRead);
    }

    public void pressedServerFileInfo(ActionEvent actionEvent) throws IOException {
        String fileToRead = serverSelectionModel.getSelectedItem().getName();
        serverInfoShow(fileToRead);
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
        String target = userSelectionModel.getSelectedItem().getName();
        openUserFolder(target);
    }

    public void pressServerFolder(ActionEvent actionEvent) throws IOException {
        String target = serverSelectionModel.getSelectedItem().getName();
        openServerFolder(target);
    }

    public void contextMenuOverUser(ContextMenuEvent contextMenuEvent) {
        String selectedItem = userSelectionModel.getSelectedItem().getName();
        userGetInfo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    userInfoShow(selectedItem);
                } catch (IOException e) {
                    System.err.println("Cant show item info");
                    e.printStackTrace();
                }
            }
        });
        userDeleteFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    deleteUserFiles(selectedItem);
                } catch (IOException e) {
                    System.err.println("Cant delete item");
                    e.printStackTrace();
                }
            }
        });
        userOpenFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    openUserFolder(selectedItem);
                } catch (IOException e) {
                    System.err.println("Cant open user folder");
                    e.printStackTrace();
                }
            }
        });

        userRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    readUserFiles();
                } catch (IOException e) {
                    System.err.println("Cant refresh user files");
                    e.printStackTrace();
                }
            }
        });

        userLabel.setContextMenu(userMenu);
        userMenu.getItems().addAll(userGetInfo, userOpenFolder, userDeleteFile, userRefresh);
        userTable.setContextMenu(userMenu);
    }

    public void contextMenuOverServer(ContextMenuEvent contextMenuEvent) {
        String selectedItem = serverSelectionModel.getSelectedItem().getName();
        serverGetInfo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    serverInfoShow(selectedItem);
                } catch (IOException e) {
                    System.err.println("Cant show info for server item");
                    e.printStackTrace();
                }
            }
        });
        serverOpenFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    openServerFolder(selectedItem);
                } catch (IOException e) {
                    System.err.println("Cant open server folder");
                    e.printStackTrace();
                }
            }
        });
        serverDeleteFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    deleteServerFile(selectedItem);
                } catch (IOException e) {
                    System.err.println("Cant delete server file");
                    e.printStackTrace();
                }
            }
        });
        serverRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    clientNetwork.write(new RefreshMessage());
                } catch (IOException e) {
                    System.err.println("Cant refresh server list");
                    e.printStackTrace();
                }
            }
        });
        serverLabel.setContextMenu(serverMenu);
        serverMenu.getItems().addAll(serverGetInfo, serverOpenFolder, serverDeleteFile, serverRefresh);
        serverTable.setContextMenu(serverMenu);
    }
}
