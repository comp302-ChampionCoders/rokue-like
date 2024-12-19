import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//gerekli eklentiler

//play mode'da pause butonu tıklayınca buraya bağlantı
//pause screen açıldığında timer durdurma
//restrat denince timer sıfırlama
//resume diyince timer devam ettirme



public class PauseScreen extends JFrame {
    private JPanel overlayPanel;
    private JButton resumeButton;
    private JButton restartButton;
    private JButton quitButton;


    public PauseScreen() {
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


        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Resume Game");
                dispose();
                //******************** timer devam ettirme
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Restart Game");
                dispose();
                // ******************** timer sıfırlama
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Quit Game");
                System.exit(0);
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PauseScreen pauseScreen = new PauseScreen();
                pauseScreen.setLocationRelativeTo(null);
                pauseScreen.setVisible(true);
            }
        });
    }
}