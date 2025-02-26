package ui.swing;

import controller.HallController;
import controller.ScreenTransition;
import controller.TimerController;
import domain.core.Game;
import ui.utils.CursorUtils;
import ui.utils.SoundPlayerUtil;
import ui.utils.TaskBarIconUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.*;

public class GameOverScreen extends JFrame {
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private final ScreenTransition restartGame;
    private final ScreenTransition returnToMenu;
    private Font customFont;
    private HallController hallController;

    public GameOverScreen(ScreenTransition restartGame, ScreenTransition returnToMenu, HallController hallController) {
        this.restartGame = restartGame;
        this.returnToMenu = returnToMenu;
        this.hallController = hallController;

        initializeFrame();

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            configureForWindows();
        } else if (osName.contains("mac")) {
            configureForMacOS();
        } else {
            configureForOther();
        }

        createMainPanel();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Game Over");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setCursor(CursorUtils.createCustomCursor(getClass().getResourceAsStream("/images/pointer_scifi_a.png")));
    }

    private void configureForMacOS(){
        TaskBarIconUtil.setMacTaskbarIcon();
    }

    public void configureForWindows(){
        TaskBarIconUtil.setWindowsTaskbarIcon(this);
    }

    public void configureForOther(){
    }

    private void createMainPanel() {
        JPanel mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);  
                g.setColor(new Color(66, 40, 53,255));
                g.fillRect(0, 0, getWidth(), getHeight());

                InputStream fontStream = getClass().getResourceAsStream("/fonts/FantasyRPGtext.ttf");
                try {
                    customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(60f);
                } catch (FontFormatException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    customFont = new Font("Arial", Font.BOLD, 60);
                }
                g.setFont(customFont);
                g.setColor(Color.WHITE);
                String gameOverText = "GAME OVER";
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(gameOverText);
                g.drawString(gameOverText, (getWidth() - textWidth) / 2, 180);

            }
        };
        mainPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        add(mainPanel);
        addButtons(mainPanel);
    }

    private void addButtons(JPanel mainPanel) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(SCREEN_WIDTH / 4, 300, SCREEN_WIDTH / 2, 200);

        JButton restartButton = createStyledButton("RESTART GAME");
        JButton menuButton = createStyledButton("RETURN TO MENU");


        restartButton.addActionListener(e -> {
            hallController.restartGame();
            SoundPlayerUtil.playClickSound();
            TimerController.getInstance().reset();
            GameScreen.isLoaded = false;
            restartGame.execute();});


        menuButton.addActionListener(e -> {
            hallController.resetHalls();
            SoundPlayerUtil.playClickSound();
            TimerController.getInstance().reset();
            returnToMenu.execute();
        });

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(restartButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(menuButton);
        buttonPanel.add(Box.createVerticalGlue());

        mainPanel.add(buttonPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 60));
        button.setMaximumSize(new Dimension(250, 60));

        InputStream fontStream = getClass().getResourceAsStream("/fonts/FantasyRPGtext.ttf");
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream) .deriveFont(24f);
        } catch (FontFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 16);
        }
        button.setFont(customFont);
        button.setBackground(new Color(139, 69, 19));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(160, 82, 45));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(139, 69, 19));
            }
        });

        return button;
    }
}
