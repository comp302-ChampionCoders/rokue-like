package ui.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayerUtil {
    private static final String CLICK_SOUND_PATH = "src/resources/sounds/click.wav";
    private static final String OBJECT_PLACED_SOUND_PATH = "src/resources/sounds/objectPlaced.wav";
    private static final String MISPLACED_SOUND_PATH = "src/resources/sounds/misplaced.wav";
    private static final String ADD_SOUND_PATH = "src/resources/sounds/addSound.wav";
    private static final String GAME_OVER_PATH = "src/resources/sounds/gameOver.wav";
    private static final String HERO_HURT_PATH = "src/resources/sounds/hurt.wav";
    private static final String HERO_MOVE_PATH = "src/resources/sounds/footstep.wav";
    

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

