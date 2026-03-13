package com.biblio.controller;

import com.biblio.model.Profil;
import com.biblio.model.Utilisateur;
import com.biblio.security.Session;
import com.biblio.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import com.biblio.util.JPAUtil;
import java.util.List;

public class UsersController {
    @FXML
    private TableView<Utilisateur> table;
    @FXML
    private TableColumn<Utilisateur, String> colLogin;
    @FXML
    private TableColumn<Utilisateur, String> colNom;
    @FXML
    private TableColumn<Utilisateur, String> colPrenom;
    @FXML
    private TableColumn<Utilisateur, String> colEmail;
    @FXML
    private TableColumn<Utilisateur, String> colProfil;
    @FXML
    private TableColumn<Utilisateur, Boolean> colActif;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<Profil> profilCombo;
    @FXML
    private CheckBox actifCheck;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button resetPwdBtn;

    private final UserService service = new UserService();
    private final ObservableList<Utilisateur> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        profilCombo.setItems(FXCollections.observableArrayList(Profil.values()));
        colLogin.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("login"));
        colNom.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        colProfil.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("profil"));
        colActif.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("actif"));
        loadData();
        if (Session.getCurrentUser() != null && Session.getCurrentUser().getProfil() != Profil.ADMIN) {
            deleteBtn.setDisable(true);
            if (resetPwdBtn != null) resetPwdBtn.setDisable(true);
        }
    }

    private void loadData() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Utilisateur> q = em.createQuery("select u from Utilisateur u order by u.login", Utilisateur.class);
            List<Utilisateur> list = q.getResultList();
            data.setAll(list);
            table.setItems(data);
        } finally {
            em.close();
        }
    }

    @FXML
    public void onSave() {
        Utilisateur u = table.getSelectionModel().getSelectedItem();
        if (u == null) u = new Utilisateur();
        u.setLogin(loginField.getText());
        if (!passwordField.getText().isEmpty()) {
            u.setMotDePasse(BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt(10)));
        } else if (u.getId() == null) {
            return;
        }
        u.setNom(nomField.getText());
        u.setPrenom(prenomField.getText());
        u.setEmail(emailField.getText());
        u.setProfil(profilCombo.getValue());
        u.setActif(actifCheck.isSelected());
        service.save(u);
        loadData();
    }

    @FXML
    public void onNew() {
        table.getSelectionModel().clearSelection();
        loginField.clear();
        passwordField.clear();
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        actifCheck.setSelected(true);
        profilCombo.setValue(Profil.BIBLIOTHECAIRE);
    }

    @FXML
    public void onDelete() {
        Utilisateur u = table.getSelectionModel().getSelectedItem();
        if (u == null) return;
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Utilisateur ref = em.find(Utilisateur.class, u.getId());
            if (ref != null) em.remove(ref);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        loadData();
    }

    @FXML
    public void onSelect() {
        Utilisateur u = table.getSelectionModel().getSelectedItem();
        if (u == null) return;
        loginField.setText(u.getLogin());
        passwordField.clear();
        nomField.setText(u.getNom());
        prenomField.setText(u.getPrenom());
        emailField.setText(u.getEmail());
        profilCombo.setValue(u.getProfil());
        actifCheck.setSelected(u.isActif());
    }

    @FXML
    public void onResetPassword() {
        Utilisateur u = table.getSelectionModel().getSelectedItem();
        if (u == null) return;
        if (Session.getCurrentUser() == null || Session.getCurrentUser().getProfil() != Profil.ADMIN) return;
        String temp = java.util.UUID.randomUUID().toString().substring(0, 8);
        u.setMotDePasse(BCrypt.hashpw(temp, BCrypt.gensalt(10)));
        service.save(u);
        new Alert(Alert.AlertType.INFORMATION, "Nouveau mot de passe: " + temp).showAndWait();
    }

    @FXML
    public void onReset() {
        onNew();
    }

    @FXML
    public void onBack() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
        Stage stage = (Stage) table.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
