package com.biblio.controller;

import com.biblio.model.Categorie;
import com.biblio.model.Livre;
import com.biblio.model.Profil;
import com.biblio.security.Session;
import com.biblio.service.BookService;
import com.biblio.service.CategoryService;
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

import java.util.List;

public class BooksController {
    @FXML
    private TableView<Livre> table;
    @FXML
    private TableColumn<Livre, String> colIsbn;
    @FXML
    private TableColumn<Livre, String> colTitre;
    @FXML
    private TableColumn<Livre, String> colAuteur;
    @FXML
    private TableColumn<Livre, String> colCategorie;
    @FXML
    private TableColumn<Livre, Integer> colAnnee;
    @FXML
    private TableColumn<Livre, Integer> colExemplaires;
    @FXML
    private TextField isbnField;
    @FXML
    private TextField titreField;
    @FXML
    private TextField auteurField;
    @FXML
    private ComboBox<Categorie> categorieCombo;
    @FXML
    private Spinner<Integer> anneeSpinner;
    @FXML
    private Spinner<Integer> exemplairesSpinner;
    @FXML
    private TextField searchField;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button newBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button resetBtn;

    private final BookService bookService = new BookService();
    private final CategoryService categoryService = new CategoryService();
    private final ObservableList<Livre> data = FXCollections.observableArrayList();
    @FXML
    private CheckBox availableOnlyCheck;

    @FXML
    public void initialize() {
        categoryService.ensureDefaults();
        categorieCombo.setItems(FXCollections.observableArrayList(categoryService.findAll()));
        anneeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, 2100, 2024));
        exemplairesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        colIsbn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("isbn"));
        colTitre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("auteur"));
        colAnnee.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("anneePublication"));
        colExemplaires.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombreExemplaires"));
        colCategorie.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getCategorie() != null ? cd.getValue().getCategorie().getLibelle() : ""));
        loadData("");
        boolean admin = Session.getCurrentUser() != null && Session.getCurrentUser().getProfil() == Profil.ADMIN;
        if (deleteBtn != null) {
            deleteBtn.setVisible(admin);
            deleteBtn.setManaged(admin);
        }
        if (!admin) {
            if (newBtn != null) { newBtn.setDisable(true); newBtn.setVisible(false); newBtn.setManaged(false); }
            if (saveBtn != null) { saveBtn.setDisable(true); saveBtn.setVisible(false); saveBtn.setManaged(false); }
            if (resetBtn != null) { resetBtn.setDisable(true); resetBtn.setVisible(false); resetBtn.setManaged(false); }
            if (isbnField != null) isbnField.setDisable(true);
            if (titreField != null) titreField.setDisable(true);
            if (auteurField != null) auteurField.setDisable(true);
            if (categorieCombo != null) categorieCombo.setDisable(true);
            if (anneeSpinner != null) anneeSpinner.setDisable(true);
            if (exemplairesSpinner != null) exemplairesSpinner.setDisable(true);
        }
    }

    private void loadData(String q) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "select l from Livre l where (lower(l.titre) like :q or lower(l.auteur) like :q or l.isbn like :q)";
            if (availableOnlyCheck != null && availableOnlyCheck.isSelected()) {
                jpql += " and l.disponible=true";
            }
            jpql += " order by l.titre";
            TypedQuery<Livre> tq = em.createQuery(jpql, Livre.class);
            tq.setParameter("q", "%" + q.toLowerCase() + "%");
            List<Livre> list = tq.getResultList();
            data.setAll(list);
            table.setItems(data);
        } finally {
            em.close();
        }
    }

    @FXML
    public void onSearch() {
        loadData(searchField.getText().trim());
    }

    @FXML
    public void onSave() {
        boolean admin = Session.getCurrentUser() != null && Session.getCurrentUser().getProfil() == Profil.ADMIN;
        if (!admin) {
            new Alert(Alert.AlertType.WARNING, "Modification réservée à l’administrateur").showAndWait();
            return;
        }
        if (!validateForm()) {
            new Alert(Alert.AlertType.WARNING, "Tous les champs sont obligatoires").showAndWait();
            return;
        }
        Livre l = table.getSelectionModel().getSelectedItem();
        if (l == null) l = new Livre();
        l.setIsbn(isbnField.getText());
        l.setTitre(titreField.getText());
        l.setAuteur(auteurField.getText());
        l.setCategorie(categorieCombo.getValue());
        l.setAnneePublication(anneeSpinner.getValue());
        l.setNombreExemplaires(exemplairesSpinner.getValue());
        bookService.save(l);
        loadData(searchField.getText().trim());
    }

    @FXML
    public void onNew() {
        table.getSelectionModel().clearSelection();
        isbnField.clear();
        titreField.clear();
        auteurField.clear();
        if (!categorieCombo.getItems().isEmpty()) categorieCombo.getSelectionModel().select(0);
        anneeSpinner.getValueFactory().setValue(2024);
        exemplairesSpinner.getValueFactory().setValue(1);
    }

    @FXML
    public void onSelect() {
        Livre l = table.getSelectionModel().getSelectedItem();
        if (l == null) return;
        isbnField.setText(l.getIsbn());
        titreField.setText(l.getTitre());
        auteurField.setText(l.getAuteur());
        categorieCombo.setValue(l.getCategorie());
        anneeSpinner.getValueFactory().setValue(l.getAnneePublication() == null ? 2024 : l.getAnneePublication());
        exemplairesSpinner.getValueFactory().setValue(l.getNombreExemplaires() == null ? 1 : l.getNombreExemplaires());
    }

    @FXML
    public void onDelete() {
        Livre l = table.getSelectionModel().getSelectedItem();
        if (l == null) return;
        boolean admin = Session.getCurrentUser() != null && Session.getCurrentUser().getProfil() == Profil.ADMIN;
        if (!admin) {
            new Alert(Alert.AlertType.WARNING, "Suppression réservée à l’administrateur").showAndWait();
            return;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Livre ref = em.find(Livre.class, l.getId());
            if (ref != null) em.remove(ref);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        onNew();
        loadData(searchField.getText().trim());
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

    private boolean validateForm() {
        boolean ok = true;
        if (isbnField.getText() == null || isbnField.getText().isBlank()) ok = false;
        if (titreField.getText() == null || titreField.getText().isBlank()) ok = false;
        if (auteurField.getText() == null || auteurField.getText().isBlank()) ok = false;
        if (categorieCombo.getValue() == null) ok = false;
        if (anneeSpinner.getValue() == null) ok = false;
        if (exemplairesSpinner.getValue() == null || exemplairesSpinner.getValue() < 1) ok = false;
        return ok;
    }
}
