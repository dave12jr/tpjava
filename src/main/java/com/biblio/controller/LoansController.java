package com.biblio.controller;

import com.biblio.model.Adherent;
import com.biblio.model.Emprunt;
import com.biblio.model.Livre;
import com.biblio.model.Utilisateur;
import com.biblio.security.Session;
import com.biblio.service.LoanService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class LoansController {
    @FXML
    private TableView<Emprunt> table;
    @FXML
    private TableColumn<Emprunt, String> colLivre;
    @FXML
    private TableColumn<Emprunt, String> colAdherent;
    @FXML
    private TableColumn<Emprunt, String> colDateEmp;
    @FXML
    private TableColumn<Emprunt, String> colDatePrev;
    @FXML
    private TableColumn<Emprunt, String> colDateRet;
    @FXML
    private TableColumn<Emprunt, String> colPenalite;
    @FXML
    private ComboBox<Livre> livreCombo;
    @FXML
    private ComboBox<Adherent> adherentCombo;
    @FXML
    private DatePicker datePrevuePicker;

    private final LoanService service = new LoanService();
    private final ObservableList<Emprunt> data = FXCollections.observableArrayList();
    private static final BigDecimal DAILY_RATE = new BigDecimal("100");
    @FXML
    private CheckBox openOnlyCheck;
    @FXML
    private CheckBox overdueOnlyCheck;

    @FXML
    public void initialize() {
        loadCombos();
        colLivre.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getLivre() != null ? cd.getValue().getLivre().getTitre() : ""));
        colAdherent.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getAdherent() != null ? cd.getValue().getAdherent().getMatricule() : ""));
        colDateEmp.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getDateEmprunt() != null ? cd.getValue().getDateEmprunt().toString() : ""));
        colDatePrev.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getDateRetourPrevue() != null ? cd.getValue().getDateRetourPrevue().toString() : ""));
        colDateRet.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getDateRetourEffective() != null ? cd.getValue().getDateRetourEffective().toString() : ""));
        colPenalite.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getPenalite() != null ? cd.getValue().getPenalite().toPlainString() : "0"));
        loadData();
    }

    private void loadCombos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Livre> livres = em.createQuery("select l from Livre l order by l.titre", Livre.class).getResultList();
            List<Adherent> adherents = em.createQuery("select a from Adherent a where a.actif=true order by a.matricule", Adherent.class).getResultList();
            livreCombo.setItems(FXCollections.observableArrayList(livres));
            adherentCombo.setItems(FXCollections.observableArrayList(adherents));
        } finally {
            em.close();
        }
    }

    private void loadData() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "select e from Emprunt e where 1=1";
            if (openOnlyCheck != null && openOnlyCheck.isSelected()) {
                jpql += " and e.dateRetourEffective is null";
            }
            if (overdueOnlyCheck != null && overdueOnlyCheck.isSelected()) {
                jpql += " and e.dateRetourEffective is null and e.dateRetourPrevue < :today";
            }
            jpql += " order by e.dateEmprunt desc";
            TypedQuery<Emprunt> q = em.createQuery(jpql, Emprunt.class);
            if (overdueOnlyCheck != null && overdueOnlyCheck.isSelected()) {
                q.setParameter("today", LocalDate.now());
            }
            data.setAll(q.getResultList());
            table.setItems(data);
        } finally {
            em.close();
        }
    }

    @FXML
    public void onRegisterLoan() {
        Livre l = livreCombo.getValue();
        Adherent a = adherentCombo.getValue();
        Utilisateur u = Session.getCurrentUser();
        if (l == null || a == null || datePrevuePicker.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Livre, adhérent et date de retour prévue sont obligatoires").showAndWait();
            return;
        }
        Emprunt created = service.registerLoan(l, a, u, datePrevuePicker.getValue());
        if (created == null) {
            new Alert(Alert.AlertType.WARNING, "Livre indisponible pour emprunt").showAndWait();
            return;
        }
        loadData();
    }

    private boolean isAvailable(Livre l) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long ouverts = em.createQuery("select count(e) from Emprunt e where e.livre=:l and e.dateRetourEffective is null", Long.class)
                    .setParameter("l", l).getSingleResult();
            int ex = l.getNombreExemplaires() == null ? 1 : l.getNombreExemplaires();
            return ouverts < ex;
        } finally {
            em.close();
        }
    }

    @FXML
    public void onRegisterReturn() {
        Emprunt e = table.getSelectionModel().getSelectedItem();
        if (e == null || e.getDateRetourEffective() != null) return;
        service.registerReturn(e);
        loadData();
    }

    @FXML
    public void onReset() {
        livreCombo.getSelectionModel().clearSelection();
        adherentCombo.getSelectionModel().clearSelection();
        datePrevuePicker.setValue(null);
    }

    @FXML
    public void onBack() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
        Stage stage = (Stage) table.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void onFilter() {
        loadData();
    }
}
