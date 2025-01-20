package ui.swing;

import controller.HallController;
import controller.ScreenTransition;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import controller.HallController;

import controller.ScreenTransition;
import ui.utils.SoundPlayerUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import utils.SaveLoadUtil;

public class LoadGameScreen extends JFrame {
    private static final double WIDTH_RATIO = 0.4; // Ekran genişliğinin %40'ı
    private static final double HEIGHT_RATIO = 0.5; // Ekran yüksekliğinin %50'si

    public LoadGameScreen(ScreenTransition onExit, ScreenTransition onLoadGame,HallController hallController) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * WIDTH_RATIO);
        int height = (int) (screenSize.height * HEIGHT_RATIO);

        setTitle("Load Game");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Save dosyalarını listeleme paneli
        JPanel saveListPanel = new JPanel();
        saveListPanel.setLayout(new BoxLayout(saveListPanel, BoxLayout.Y_AXIS));
        saveListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] saveFiles = SaveLoadUtil.listSaveFiles();
        if (saveFiles.length == 0) {
            JLabel noSavesLabel = new JLabel("No saved games found.");
            noSavesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            saveListPanel.add(noSavesLabel);
        } else {
            for (String save : saveFiles) {
                JButton loadButton = new JButton(save.replace(".dat", "")); // Dosya adını temizle
                loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                loadButton.addActionListener(e -> {
                    SoundPlayerUtil.playClickSound();
                    var gameState = SaveLoadUtil.loadGame(save);
                    if (gameState != null) {
                        hallController.loadGameState(gameState);
                        GameScreen.isLoaded = true;
                        onLoadGame.execute(); // Oyun yüklenir
                        dispose();
                    }
                });
                saveListPanel.add(loadButton);
                saveListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(saveListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Çıkış butonu
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            SoundPlayerUtil.playClickSound();
            onExit.execute();
            dispose();
        });

        // Panel düzenlemesi
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(exitButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}


