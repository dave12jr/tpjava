package com.biblio.controller;

import com.biblio.model.Adherent;
import com.biblio.model.Emprunt;
import com.biblio.service.LoanService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class AdherentHistoryController {
    @FXML
    private TableView<Emprunt> table;
    @FXML
    private TableColumn<Emprunt, String> colLivre;
    @FXML
    private TableColumn<Emprunt, String> colEmp;
    @FXML
    private TableColumn<Emprunt, String> colPrev;
    @FXML
    private TableColumn<Emprunt, String> colRet;

    private final LoanService service = new LoanService();
    private Adherent adherent;

    public void setAdherent(Adherent a) {
        this.adherent = a;
        load();
    }

    @FXML
    public void initialize() {
        colLivre.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getLivre() != null ? cd.getValue().getLivre().getTitre() : ""));
        colEmp.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getDateEmprunt() != null ? cd.getValue().getDateEmprunt().toString() : ""));
        colPrev.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getDateRetourPrevue() != null ? cd.getValue().getDateRetourPrevue().toString() : ""));
        colRet.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getDateRetourEffective() != null ? cd.getValue().getDateRetourEffective().toString() : ""));
    }

    private void load() {
        if (adherent == null) return;
        table.setItems(FXCollections.observableArrayList(service.findByAdherent(adherent.getId())));
    }
}
