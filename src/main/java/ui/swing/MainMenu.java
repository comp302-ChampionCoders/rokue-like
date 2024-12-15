package ui.swing;
import javax.swing.*;

public class MainMenu {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Rokue-Like");
        JButton newGame = new JButton("Start New Game");
        JButton help = new JButton("Help");
        JButton exit = new JButton("Exit");

        newGame.addActionListener(e -> System.out.println("Start game"));
        help.addActionListener(e -> System.out.println("Show help"));
        exit.addActionListener(e -> System.exit(0));

        JPanel panel = new JPanel();
        panel.add(newGame);
        panel.add(help);
        panel.add(exit);

        frame.add(panel);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
