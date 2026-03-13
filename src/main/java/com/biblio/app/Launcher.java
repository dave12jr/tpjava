package com.biblio.app;

/**
 * Classe de lancement de secours pour éviter l'erreur "JavaFX runtime components are missing".
 * Cette classe ne doit PAS étendre javafx.application.Application.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
