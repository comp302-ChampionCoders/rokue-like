package ui.swing;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import controller.HallController;
import controller.TimerController;
import ui.utils.CursorUtils;
import ui.utils.SoundPlayerUtil;
import ui.utils.TaskBarIconUtil;
import controller.ScreenTransition;

public class GameWinScreen extends JFrame {
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private JPanel mainPanel;
    private Font customFont;
    private final ScreenTransition returnToMainMenu;
    private HallController hallController;

    public GameWinScreen(ScreenTransition returnToMainMenu, HallController hallController) {
        this.returnToMainMenu = returnToMainMenu;
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

        loadCustomFont();
        createMainPanel();
        addButton();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Congratulations - You Win!");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setCursor(CursorUtils.createCustomCursor("src/resources/images/pointer_scifi_a.png"));
    }

    private void configureForMacOS() {
        TaskBarIconUtil.setMacTaskbarIcon();
    }

    private void configureForWindows() {
        TaskBarIconUtil.setWindowsTaskbarIcon(this);
    }

    private void configureForOther() {
    }

    private void loadCustomFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/fonts/FantasyRPGtext.ttf")).deriveFont(36f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 36);
        }
    }

    private void createMainPanel() {
        mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 128, 0)); // Victory-themed green background
                g.fillRect(0, 0, getWidth(), getHeight());

                // Add congratulations text
                g.setFont(customFont);
                g.setColor(Color.WHITE);
                String title = "HERO ESCAPED";
                String subtitle = "YOU WON!";
                FontMetrics fm = g.getFontMetrics();

                int titleWidth = fm.stringWidth(title);
                int subtitleWidth = fm.stringWidth(subtitle);

                g.drawString(title, (getWidth() - titleWidth) / 2, 200);
                g.drawString(subtitle, (getWidth() - subtitleWidth) / 2, 300);
            }
        };
        mainPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        add(mainPanel);
    }

    private void addButton() {
        JButton mainMenuButton = createStyledButton("MAIN MENU");
        mainMenuButton.setBounds((SCREEN_WIDTH - 250) / 2, 400, 250, 60);
        mainMenuButton.addActionListener(e -> {
            SoundPlayerUtil.playClickSound();
            TimerController.getInstance().reset();
            hallController.resetHalls();
            returnToMainMenu.execute();
            dispose();
        });
        mainPanel.add(mainMenuButton);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 60));
        button.setMaximumSize(new Dimension(250, 60));

        button.setFont(customFont.deriveFont(24f));
        button.setBackground(new Color(0, 100, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(34, 139, 34));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 100, 0));
            }
        });

        return button;
    }
}
