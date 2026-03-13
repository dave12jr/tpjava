package com.biblio.controller;

import com.biblio.model.Categorie;
import com.biblio.service.CategoryService;
import com.biblio.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class CategoriesController {
    @FXML
    private TableView<Categorie> table;
    @FXML
    private TableColumn<Categorie, String> colLibelle;
    @FXML
    private TableColumn<Categorie, String> colDescription;
    @FXML
    private TextField libelleField;
    @FXML
    private TextField descriptionField;

    private final CategoryService service = new CategoryService();
    private final ObservableList<Categorie> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colLibelle.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("libelle"));
        colDescription.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));
        loadData();
    }

    private void loadData() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Categorie> q = em.createQuery("select c from Categorie c order by c.libelle", Categorie.class);
            List<Categorie> list = q.getResultList();
            data.setAll(list);
            table.setItems(data);
        } finally {
            em.close();
        }
    }

    @FXML
    public void onNew() {
        table.getSelectionModel().clearSelection();
        libelleField.clear();
        descriptionField.clear();
    }

    @FXML
    public void onSave() {
        if (libelleField.getText() == null || libelleField.getText().isBlank()
                || descriptionField.getText() == null || descriptionField.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Libellé et description sont obligatoires").showAndWait();
            return;
        }
        Categorie c = table.getSelectionModel().getSelectedItem();
        if (c == null) c = new Categorie();
        c.setLibelle(libelleField.getText());
        c.setDescription(descriptionField.getText());
        service.save(c);
        loadData();
    }

    @FXML
    public void onDelete() {
        Categorie c = table.getSelectionModel().getSelectedItem();
        if (c == null) return;
        service.delete(c.getId());
        onNew();
        loadData();
    }

    @FXML
    public void onSelect() {
        Categorie c = table.getSelectionModel().getSelectedItem();
        if (c == null) return;
        libelleField.setText(c.getLibelle());
        descriptionField.setText(c.getDescription());
    }

    @FXML
    public void onBack() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
        Stage stage = (Stage) table.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
