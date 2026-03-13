package com.biblio.controller;

import com.biblio.model.Utilisateur;
import com.biblio.service.UserService;
import com.biblio.security.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label statusLabel;

    private final UserService userService = new UserService();

    @FXML
    public void onLogin(ActionEvent event) throws Exception {
        Utilisateur u = userService.authenticate(loginField.getText(), passwordField.getText());
        if (u != null) {
            Session.setCurrentUser(u);
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } else {
            statusLabel.setText("Identifiants invalides");
        }
    }

    @FXML
    public void openSignup() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Créer un compte utilisateur");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
