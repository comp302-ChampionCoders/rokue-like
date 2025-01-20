package ui.swing;

import controller.HallController;
import controller.ScreenTransition;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class LoadGameScreen extends JFrame {
    private final ScreenTransition onMainMenu;
    private final ScreenTransition onGameLoad;
    private final HallController hallController;

    public LoadGameScreen(ScreenTransition onMainMenu, ScreenTransition onGameLoad, HallController hallController) {
        this.onMainMenu = onMainMenu;
        this.onGameLoad = onGameLoad;
        this.hallController = hallController;


        // JFrame özellikleri
        setTitle("Load Game");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Save dosyalarını listeleme
        DefaultListModel<String> listModel = new DefaultListModel<>();
        File saveDir = new File("saves");
        if (saveDir.exists()) {
            for (File file : saveDir.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".sav")) {
                    listModel.addElement(file.getName());
                }
            }
        }

        JList<String> saveList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(saveList);
        add(scrollPane, BorderLayout.CENTER);

        // Load butonu
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> {
            String selectedFile = saveList.getSelectedValue();
            if (selectedFile != null) {
                //loadGame("saves/" + selectedFile);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a save file to load.", "No File Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        add(loadButton, BorderLayout.SOUTH);
    }

  /*  private void loadGame(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Game loadedGame = (Game) ois.readObject();
            onGameLoad.execute(); // Yükleme sonrası oyun ekranına geçiş
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load game. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }*/
}

