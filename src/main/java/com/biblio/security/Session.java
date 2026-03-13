package com.biblio.security;

import com.biblio.model.Utilisateur;

public final class Session {
    private static Utilisateur currentUser;

    private Session() {}

    public static void setCurrentUser(Utilisateur u) {
        currentUser = u;
    }

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public static boolean isAuthenticated() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
    }
}
