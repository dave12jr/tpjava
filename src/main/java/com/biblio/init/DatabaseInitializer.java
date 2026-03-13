package com.biblio.init;

import com.biblio.service.UserService;

public final class DatabaseInitializer {
    private DatabaseInitializer() {}

    public static void ensureDefaultAdmin() {
        new UserService().ensureAdminExists();
    }
}
