package com.biblio.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class SettingsController {
    @FXML
    private BorderPane root;

    @FXML
    public void onBack() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
        Stage stage = (Stage) this.root.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
