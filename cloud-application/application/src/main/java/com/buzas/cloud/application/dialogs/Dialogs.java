package com.buzas.cloud.application.dialogs;

import com.buzas.cloud.application.ClientApp;
import javafx.scene.control.Alert;

public class Dialogs {

    public enum AboutDialog {
        UPDATE("Last update: 27.04.22");

        private final String message;
        private static final String TITLE = "Program info";
        private static final String TYPE = "Cloud storage program";

        AboutDialog(String message){
            this.message = message;
        }

        public void show() {
            showDialog(Alert.AlertType.INFORMATION, TITLE, TYPE, message);
        }
    }

    public enum ErrorDialog {
        DOWNLOADING_FILES_ERROR("Error with downloading files from server");

        private static final String TITLE = "Error!";
        private static final String TYPE = TITLE;
        private final String message;

        ErrorDialog (String message){
            this.message = message;
        }

        public void show() {showDialog(Alert.AlertType.ERROR,TITLE, TYPE, message);}

    }

    private static void showDialog(Alert.AlertType alertType,String title, String type, String message) {
        Alert alert = new Alert(alertType);
        alert.initOwner(ClientApp.INSTANCE.getPrimaryStage());
        alert.setTitle(title);
        alert.setHeaderText(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
