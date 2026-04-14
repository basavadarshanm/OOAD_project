package com.onlinebanking.config;

public final class ApplicationContext {
    private static final ApplicationContext INSTANCE = new ApplicationContext();

    private ApplicationContext() {
    }

    public static ApplicationContext getInstance() {
        return INSTANCE;
    }

    public void shutdown() {
        // No resources to clean up with SQLite
    }
}
