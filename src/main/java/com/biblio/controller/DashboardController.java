package com.biblio.controller;

import com.biblio.model.Profil;
import com.biblio.security.Session;
import com.biblio.service.BookService;
import com.biblio.service.LoanService;
import com.biblio.service.MemberService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashboardController {
    @FXML
    private Label totalLivresLabel;
    @FXML
    private Label empruntsMoisLabel;
    @FXML
    private Label adherentsActifsLabel;
    @FXML
    private MenuItem menuUsersItem;
    @FXML
    private MenuItem menuSettingsItem;
    @FXML
    private Button btnUsers;
    @FXML
    private Button btnBooks;
    @FXML
    private Button btnMembers;
    @FXML
    private Button btnLoans;
    @FXML
    private Button btnSettings;
    @FXML
    private Button btnCategories;
    @FXML
    private MenuItem menuCategoriesItem;
    @FXML
    private TableView<com.biblio.model.Emprunt> overduesTable;
    @FXML
    private TableColumn<com.biblio.model.Emprunt, String> colOvLivre;
    @FXML
    private TableColumn<com.biblio.model.Emprunt, String> colOvAdherent;
    @FXML
    private TableColumn<com.biblio.model.Emprunt, String> colOvPrev;
    @FXML
    private TableColumn<com.biblio.model.Emprunt, String> colOvJours;

    private final BookService bookService = new BookService();
    private final LoanService loanService = new LoanService();
    private final MemberService memberService = new MemberService();
    private Timeline autoRefresh;

    @FXML
    public void initialize() {
        refreshStats();
        applyRoleVisibility();
        setupOverduesTable();
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(10), e -> refreshStats()));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    @FXML
    public void openUsers(ActionEvent e) throws Exception {
        if (!isAdmin()) {
            showForbidden();
            return;
        }
        navigate("/fxml/users.fxml");
    }

    @FXML
    public void openBooks(ActionEvent e) throws Exception {
        navigate("/fxml/books.fxml");
    }

    @FXML
    public void openMembers(ActionEvent e) throws Exception {
        navigate("/fxml/members.fxml");
    }

    @FXML
    public void openLoans(ActionEvent e) throws Exception {
        navigate("/fxml/loans.fxml");
    }

    @FXML
    public void openCategories(ActionEvent e) throws Exception {
        if (!isAdmin()) {
            showForbidden();
            return;
        }
        navigate("/fxml/categories.fxml");
    }

    @FXML
    public void logout() throws Exception {
        if (autoRefresh != null) {
            autoRefresh.stop();
        }
        com.biblio.security.Session.clear();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Stage stage = (Stage) totalLivresLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void refreshStats() {
        totalLivresLabel.setText(String.valueOf(bookService.countAll()));
        empruntsMoisLabel.setText(String.valueOf(loanService.countForCurrentMonth()));
        adherentsActifsLabel.setText(String.valueOf(memberService.countActive()));
        if (overduesTable != null) {
            java.util.List<com.biblio.model.Emprunt> list = loanService.findOverdues();
            overduesTable.getItems().setAll(list);
        }
    }

    @FXML
    public void openSettings(ActionEvent e) throws Exception {
        if (!isAdmin()) {
            showForbidden();
            return;
        }
        navigate("/fxml/settings.fxml");
    }

    private void navigate(String fxml) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) totalLivresLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private boolean isAdmin() {
        return Session.getCurrentUser() != null && Session.getCurrentUser().getProfil() == Profil.ADMIN;
    }

    private void applyRoleVisibility() {
        boolean admin = isAdmin();
        if (menuUsersItem != null) menuUsersItem.setVisible(admin);
        if (menuSettingsItem != null) menuSettingsItem.setVisible(admin);
        if (menuCategoriesItem != null) menuCategoriesItem.setVisible(admin);
        if (btnUsers != null) btnUsers.setVisible(admin);
        if (btnSettings != null) btnSettings.setVisible(admin);
        if (btnCategories != null) btnCategories.setVisible(admin);
    }

    private void showForbidden() {
        Alert a = new Alert(Alert.AlertType.WARNING, "Accès réservé à l’administrateur");
        a.showAndWait();
    }

    private void setupOverduesTable() {
        if (overduesTable == null) return;
        colOvLivre.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getLivre() != null ? cd.getValue().getLivre().getTitre() : ""));
        colOvAdherent.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getAdherent() != null ? cd.getValue().getAdherent().getMatricule() : ""));
        colOvPrev.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getDateRetourPrevue() != null ? cd.getValue().getDateRetourPrevue().toString() : ""));
        colOvJours.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getDateRetourPrevue() != null ?
                        String.valueOf(java.time.temporal.ChronoUnit.DAYS.between(cd.getValue().getDateRetourPrevue(), java.time.LocalDate.now())) : "0"));
    }
}
