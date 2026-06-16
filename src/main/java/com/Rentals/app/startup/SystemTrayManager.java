package com.Rentals.app.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

@Component
public class SystemTrayManager {

    private final ApplicationContext applicationContext;

    public SystemTrayManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void addToSystemTray() {

        System.setProperty("java.awt.headless", "false");

        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported.");
            return;
        }

        EventQueue.invokeLater(() -> {
            try {
                SystemTray tray = SystemTray.getSystemTray();

                URL iconUrl = getClass().getResource("/app.ico");
                if (iconUrl == null) {
                    System.out.println("ERROR: app.ico not found.");
                    return;
                }

                Image image = Toolkit.getDefaultToolkit().createImage(iconUrl);

              // ── Hidden frame — shows in main taskbar ────
                Frame frame = new Frame("Rentals App");
                frame.setIconImage(image);
                frame.setResizable(false);
                frame.setUndecorated(true);  // ← removes title bar and default window controls
                frame.setSize(0, 0);
                frame.setState(Frame.ICONIFIED); // ← set minimized BEFORE setVisible
                frame.setVisible(true);

                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        shutdown(tray, frame);
                    }
                });
                // ────────────────────────────────────────────
                            
                            
                // ── System tray icon ───────────────────────

              

                TrayIcon trayIcon = new TrayIcon(image, "Rentals App");
                trayIcon.setImageAutoSize(true);
                trayIcon.addActionListener(e -> openBrowser());
                tray.add(trayIcon);
                // ────────────────────────────────────────────

                trayIcon.displayMessage(
                    "Rentals App",
                    "Running at localhost:8080",
                    TrayIcon.MessageType.INFO
                );

                System.out.println("Taskbar and tray icon loaded.");

            } catch (Exception e) {
                System.out.println("Tray error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void openBrowser() {
        try {
            new ProcessBuilder("cmd", "/c", "start", "",
                "http://localhost:8080").start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shutdown(SystemTray tray, Frame frame) {
        try {
            TrayIcon[] icons = tray.getTrayIcons();
            for (TrayIcon icon : icons) {
                tray.remove(icon);
            }
            frame.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpringApplication.exit(applicationContext, () -> 0);
        System.exit(0);
    }
}