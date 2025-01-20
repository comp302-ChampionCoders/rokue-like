package utils;

import controller.GameState;

import java.io.*;
import java.util.List;

public class SaveLoadUtil {

    private static final String SAVE_DIRECTORY = "saves/";

    public static void saveGame(GameState gameState, String saveName) {
        try {
            new File(SAVE_DIRECTORY).mkdirs();
            FileOutputStream fileOut = new FileOutputStream(SAVE_DIRECTORY + saveName + ".dat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gameState);
            out.close();
            fileOut.close();
            System.out.println("Game saved to " + SAVE_DIRECTORY + saveName + ".dat");
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    public static GameState loadGame(String saveName) {
        try {
            FileInputStream fileIn = new FileInputStream(SAVE_DIRECTORY + saveName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            GameState gameState = (GameState) in.readObject();
            in.close();
            fileIn.close();
            return gameState;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return null;
        }
    }

    public static String[] listSaveFiles() {
        File dir = new File(SAVE_DIRECTORY);
        if (!dir.exists() || !dir.isDirectory()) {
            return new String[0];
        }
        return dir.list((d, name) -> name.endsWith(".dat"));
    }
}

