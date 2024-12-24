package ui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PauseScreen extends JFrame {
    public PauseScreen(ActionListener resumeAction, ActionListener mainMenuAction, ActionListener exitAction) {
        setTitle("Pause Menu");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new GridLayout(3, 1));
        setLocationRelativeTo(null);

        JButton resumeButton = new JButton("Resume");
        resumeButton.addActionListener(e -> {
            resumeAction.actionPerformed(e); // Execute the resume action
            dispose(); // Close the pause screen
        });

        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.addActionListener(mainMenuAction);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(exitAction);

        add(resumeButton);
        add(mainMenuButton);
        add(exitButton);
    }

    public static void main(String[] args) {
        PauseScreen pauseScreen = new PauseScreen(
            e -> System.out.println("Resume clicked"),
            e -> System.out.println("Main Menu clicked"),
            e -> System.out.println("Exit clicked")
        );
        pauseScreen.setVisible(true);
    }
    
    
}

