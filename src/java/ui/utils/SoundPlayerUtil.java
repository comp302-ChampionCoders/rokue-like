package ui.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayerUtil {
    private static final String CLICK_SOUND_PATH = "src/resources/sounds/click.wav";
    private static final String OBJECT_PLACED_SOUND_PATH = "src/resources/sounds/objectPlaced.wav";
    private static final String MISPLACED_SOUND_PATH = "src/resources/sounds/misplaced.wav";
    private static final String ADD_SOUND_PATH = "src/resources/sounds/addSound.wav";
    private static final String GAME_OVER_PATH = "src/resources/sounds/gameOverSax.wav";
    private static final String HERO_HURT_PATH = "src/resources/sounds/heroHit.wav";
    private static final String HERO_MOVE_PATH = "src/resources/sounds/footstep.wav";
    private static final String CLOTH_SOUND_PATH = "src/resources/sounds/cloth2.wav";
    private static final String  ERROR_SOUND_PATH ="src/resources/sounds/error-message182475.wav";
    private static final String OPEN_DOOR_SOUND_PATH = "src/resources/sounds/doorOpen_2.wav"; // Yeni ses yolu
    private static final String WIN_SOUND_PATH = "src/resources/sounds/winSound.wav";

    public static void playClickSound() {
        playSound(CLICK_SOUND_PATH);
    }

    public static void playObjectPlacedSound() {
        playSound(OBJECT_PLACED_SOUND_PATH);
    }

    public static void playMisplacedSound() {
        playSound(MISPLACED_SOUND_PATH);
    }
    
    public static void playAddSound(){
        playSound(ADD_SOUND_PATH);
    }

    public static void playGameOverJingle(){
        playSound(GAME_OVER_PATH);
    }

    public static void playHurtSound(){
        playSound(HERO_HURT_PATH);
    }

    public static void playMoveSound(){
        playSound(HERO_MOVE_PATH);
    }

    public static void playClothSound(){
        playSound(CLOTH_SOUND_PATH);
    }

    public static void playErrorSound(){playSound(ERROR_SOUND_PATH);}

    public static void playOpenDoorSound() { // Yeni yöntem
        playSound(OPEN_DOOR_SOUND_PATH);
    }

    public static void playWinSound() {playSound(WIN_SOUND_PATH);}

    private static void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Failed to play sound: " + soundFilePath + " - " + e.getMessage());
        }
    }
}

