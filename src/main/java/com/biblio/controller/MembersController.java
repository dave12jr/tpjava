package com.biblio.controller;

import com.biblio.model.Adherent;
import com.biblio.service.MemberService;
import com.biblio.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.util.List;

public class MembersController {
    @FXML
    private TableView<Adherent> table;
    @FXML
    private TableColumn<Adherent, String> colMatricule;
    @FXML
    private TableColumn<Adherent, String> colNom;
    @FXML
    private TableColumn<Adherent, String> colPrenom;
    @FXML
    private TableColumn<Adherent, String> colEmail;
    @FXML
    private TableColumn<Adherent, Boolean> colActif;
    @FXML
    private TextField matriculeField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField adresseField;
    @FXML
    private CheckBox actifCheck;

    private final MemberService service = new MemberService();
    private final ObservableList<Adherent> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMatricule.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("matricule"));
        colNom.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        colActif.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("actif"));
        loadData();
    }

    private void loadData() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Adherent> q = em.createQuery("select a from Adherent a order by a.matricule", Adherent.class);
            List<Adherent> list = q.getResultList();
            data.setAll(list);
            table.setItems(data);
        } finally {
            em.close();
        }
    }

    @FXML
    public void onSave() {
        if (!validateForm()) {
            new Alert(Alert.AlertType.WARNING, "Tous les champs sont obligatoires").showAndWait();
            return;
        }
        Adherent a = table.getSelectionModel().getSelectedItem();
        if (a == null) a = new Adherent();
        a.setMatricule(matriculeField.getText());
        a.setNom(nomField.getText());
        a.setPrenom(prenomField.getText());
        a.setEmail(emailField.getText());
        a.setTelephone(telephoneField.getText());
        a.setAdresse(adresseField.getText());
        a.setActif(actifCheck.isSelected());
        service.save(a);
        loadData();
    }

    @FXML
    public void onNew() {
        table.getSelectionModel().clearSelection();
        matriculeField.clear();
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        adresseField.clear();
        actifCheck.setSelected(true);
    }

    @FXML
    public void onSelect() {
        Adherent a = table.getSelectionModel().getSelectedItem();
        if (a == null) return;
        matriculeField.setText(a.getMatricule());
        nomField.setText(a.getNom());
        prenomField.setText(a.getPrenom());
        emailField.setText(a.getEmail());
        telephoneField.setText(a.getTelephone());
        adresseField.setText(a.getAdresse());
        actifCheck.setSelected(a.isActif());
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

    @FXML
    public void onHistory() throws Exception {
        Adherent a = table.getSelectionModel().getSelectedItem();
        if (a == null) return;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adherent_history.fxml"));
        Parent root = loader.load();
        AdherentHistoryController ctrl = loader.getController();
        ctrl.setAdherent(a);
        Stage stage = new Stage();
        stage.setTitle("Historique de " + a.getMatricule());
        stage.setScene(new Scene(root));
        stage.show();
    }

    private boolean validateForm() {
        boolean ok = true;
        if (matriculeField.getText() == null || matriculeField.getText().isBlank()) ok = false;
        if (nomField.getText() == null || nomField.getText().isBlank()) ok = false;
        if (prenomField.getText() == null || prenomField.getText().isBlank()) ok = false;
        if (emailField.getText() == null || emailField.getText().isBlank()) ok = false;
        if (telephoneField.getText() == null || telephoneField.getText().isBlank()) ok = false;
        if (adresseField.getText() == null || adresseField.getText().isBlank()) ok = false;
        return ok;
    }
}
