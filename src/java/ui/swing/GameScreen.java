package ui.swing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import domain.gameobjects.Hero;
import domain.behaviors.Direction;
import domain.monsters.ArcherMonster;
import domain.monsters.FighterMonster;
import domain.monsters.Monster;

public class GameScreen extends JFrame {
    private final int GRID_ROWS = 9;
    private final int GRID_COLUMNS = 11;
    private final int CELL_SIZE = 50; // Grid cell size
    private Hero hero; // Hero nesnesi
    private List<Monster> monsters; // Monster listesi
    private Random random;
    private Timer monsterTimer; // Monster hareketleri için timer

    public GameScreen() {
        setTitle("Game Screen");
        setSize(GRID_COLUMNS * CELL_SIZE, GRID_ROWS * CELL_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        hero = new Hero(0, 0); // Hero başlangıç konumu
        monsters = new ArrayList<>();
        random = new Random();
        spawnMonsters();
        monsterTimer = new Timer(500, e -> moveMonsters()); // Timer her 500ms monster hareketi için
        monsterTimer.start();
        add(new GamePanel());
    }

    private void spawnMonsters() {
        // 1 adet ArcherMonster ve 1 adet FighterMonster ekle
        monsters.add(new ArcherMonster(random.nextInt(GRID_COLUMNS), random.nextInt(GRID_ROWS)));
        monsters.add(new FighterMonster(random.nextInt(GRID_COLUMNS), random.nextInt(GRID_ROWS)));
    }

    private void moveMonsters() {
        Direction[] directions = Direction.values(); // Get all possible directions
        for (Monster monster : monsters) {
            Direction randomDirection = directions[random.nextInt(directions.length)];
            int newX = monster.getX() + randomDirection.getDx();
            int newY = monster.getY() + randomDirection.getDy();
            
            // Check boundaries and update position
            if (newX >= 0 && newX < GRID_COLUMNS && newY >= 0 && newY < GRID_ROWS) {
                monster.move(randomDirection);
            }
        }
        repaint();
    }

    private class GamePanel extends JPanel implements KeyListener {
        private BufferedImage heroImage;
        private BufferedImage archerImage;
        private BufferedImage fighterImage;
        private boolean isKeyPressed = false; // Tek tuş kontrolü için

        public GamePanel() {
            setFocusable(true);
            addKeyListener(this);
            loadImages();
        }

        private void loadImages() {
            try {
                heroImage = ImageIO.read(new File("src/resources/images/player.png"));
                archerImage = ImageIO.read(new File("src/resources/images/archer.png"));
                fighterImage = ImageIO.read(new File("src/resources/images/fighter.png"));
            } catch (IOException | NullPointerException e) {
                System.err.println("Failed to load images.");
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGrid(g);
            drawHero(g);
            drawMonsters(g);
        }

        private void drawGrid(Graphics g) {
            g.setColor(Color.GRAY);
            for (int i = 0; i <= GRID_ROWS; i++) {
                g.drawLine(0, i * CELL_SIZE, GRID_COLUMNS * CELL_SIZE, i * CELL_SIZE); // Horizontal lines
            }
            for (int j = 0; j <= GRID_COLUMNS; j++) {
                g.drawLine(j * CELL_SIZE, 0, j * CELL_SIZE, GRID_ROWS * CELL_SIZE); // Vertical lines
            }
        }

        private void drawHero(Graphics g) {
            if (heroImage != null) {
                g.drawImage(heroImage, hero.getX() * CELL_SIZE, hero.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
            } else {
                g.setColor(Color.RED);
                g.fillRect(hero.getX() * CELL_SIZE, hero.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        private void drawMonsters(Graphics g) {
            for (Monster monster : monsters) {
                BufferedImage monsterImage = null;
                if (monster instanceof ArcherMonster) {
                    monsterImage = archerImage;
                } else if (monster instanceof FighterMonster) {
                    monsterImage = fighterImage;
                }
                if (monsterImage != null) {
                    g.drawImage(monsterImage, monster.getX() * CELL_SIZE, monster.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
                } else {
                    g.setColor(Color.BLUE);
                    g.fillRect(monster.getX() * CELL_SIZE, monster.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (isKeyPressed) return;
            isKeyPressed = true;
            
            Direction direction = null;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    direction = Direction.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    direction = Direction.DOWN;
                    break;
                case KeyEvent.VK_LEFT:
                    direction = Direction.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    direction = Direction.RIGHT;
                    break;
            }
            
            if (direction != null) {
                int newX = hero.getX() + direction.getDx();
                int newY = hero.getY() + direction.getDy();
                
                // Check boundaries
                if (newX >= 0 && newX < GRID_COLUMNS && newY >= 0 && newY < GRID_ROWS) {
                    hero.move(direction);
                }
            }
            
            System.out.println("Hero Position: X=" + hero.getX() + ", Y=" + hero.getY());
            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            isKeyPressed = false; // Tuş bırakıldığında izin ver
        }

        @Override
        public void keyTyped(KeyEvent e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameScreen screen = new GameScreen();
            screen.setVisible(true);
        });
    }
}