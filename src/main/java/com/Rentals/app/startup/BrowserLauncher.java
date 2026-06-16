package com.Rentals.app.startup;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("run")
public class BrowserLauncher {

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {

        try {

            new ProcessBuilder(
                "cmd", "/c", "start", "", "http://localhost:8080"
            ).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}