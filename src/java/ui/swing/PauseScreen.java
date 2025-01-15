package ui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import technicalservices.Timer; 

public class PauseScreen extends JFrame {
    private JPanel overlayPanel;
    private JButton resumeButton;
    private JButton restartButton;
    private JButton quitButton;
    public int objectsInCurrentHall;

    private Timer gameTimer; // Reference to the game timer

    public PauseScreen(Timer timer) {
        this.gameTimer = timer;

        setTitle("Pause Screen");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 150)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        overlayPanel.setLayout(new GridBagLayout());

        resumeButton = new JButton("Resume");
        restartButton = new JButton("Restart");
        quitButton = new JButton("Quit");
        objectsInCurrentHall = 5; // Example

        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Resume Game");
                gameTimer.resume(); // Resume the timer
                dispose(); // Close the pause screen
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Restart Game");
                gameTimer.reset(objectsInCurrentHall * 5); // Calculate time
                dispose(); // Close the pause screen
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Quit Game");
                System.exit(0); // Exit the game
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(resumeButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);

        overlayPanel.add(buttonPanel);
        add(overlayPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Example of usage with a Timer instance
        Timer timer = new Timer(25); // Initialize timer with 25 seconds
        timer.start();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PauseScreen pauseScreen = new PauseScreen(timer);
                pauseScreen.setLocationRelativeTo(null);
                pauseScreen.setVisible(true);

                // Simulate pausing the timer when pause screen is shown
                timer.pause();
            }
        });
    }
}