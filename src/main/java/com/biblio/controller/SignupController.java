package com.biblio.controller;

import com.biblio.model.Profil;
import com.biblio.model.Utilisateur;
import com.biblio.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SignupController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private Label statusLabel;

    private final UserService service = new UserService();

    @FXML
    public void onCreate() {
        String login = loginField.getText();
        String pwd = passwordField.getText();
        if (login == null || login.isBlank() || pwd == null || pwd.isBlank()) {
            statusLabel.setText("Login et mot de passe requis");
            return;
        }
        if (!pwd.equals(confirmField.getText())) {
            statusLabel.setText("Les mots de passe ne correspondent pas");
            return;
        }
        if (service.findByLogin(login) != null) {
            statusLabel.setText("Ce login existe déjà");
            return;
        }
        Utilisateur u = new Utilisateur();
        u.setLogin(login);
        u.setNom(nomField.getText());
        u.setPrenom(prenomField.getText());
        u.setEmail(emailField.getText());
        u.setProfil(Profil.BIBLIOTHECAIRE);
        service.register(u, pwd);
        statusLabel.setText("Compte créé. Vous pouvez vous connecter.");
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
}
