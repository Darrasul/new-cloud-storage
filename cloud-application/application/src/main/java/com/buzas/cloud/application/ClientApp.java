package com.buzas.cloud.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {

    public static ClientApp INSTANCE;
    private FXMLLoader primaryStageLoader;
    private Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        initializePrimaryStage();
        getPrimaryStage().show();
    }

    @Override
    public void init() {
        INSTANCE = this;
    }

    private void initializePrimaryStage() throws IOException {
        primaryStageLoader = new FXMLLoader();
        primaryStageLoader.setLocation(getClass().getResource("com.buzas.cloud.application/main.fxml"));

        Parent primaryWindow = primaryStageLoader.load();
        this.primaryStage.setScene(new Scene(primaryWindow));
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }
}
