package ui.utils;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundPlayerUtil {
    private static final String CLICK_SOUND_PATH = "/sounds/click.wav";
    private static final String OBJECT_PLACED_SOUND_PATH = "/sounds/objectPlaced.wav";
    private static final String MISPLACED_SOUND_PATH = "/sounds/misplaced.wav";
    private static final String ADD_SOUND_PATH = "/sounds/addSound.wav";
    private static final String GAME_OVER_PATH = "/sounds/gameOverSax.wav";
    private static final String HERO_HURT_PATH = "/sounds/heroHit.wav";
    private static final String HERO_MOVE_PATH = "/sounds/footstep.wav";
    private static final String CLOTH_SOUND_PATH = "/sounds/cloth2.wav";
    private static final String ERROR_SOUND_PATH = "/sounds/error-message182475.wav";
    private static final String OPEN_DOOR_SOUND_PATH = "/sounds/doorOpen_2.wav";
    private static final String WIN_SOUND_PATH = "/sounds/winSound.wav";
    private static final String THROW_SOUND_PATH = "/sounds/throw.wav";

    public static void playClickSound() {
        playSound(CLICK_SOUND_PATH);
    }

    public static void playObjectPlacedSound() {
        playSound(OBJECT_PLACED_SOUND_PATH);
    }

    public static void playMisplacedSound() {
        playSound(MISPLACED_SOUND_PATH);
    }

    public static void playAddSound() {
        playSound(ADD_SOUND_PATH);
    }

    public static void playGameOverJingle() {
        playSound(GAME_OVER_PATH);
    }

    public static void playHurtSound() {
        playSound(HERO_HURT_PATH);
    }

    public static void playMoveSound() {
        playSound(HERO_MOVE_PATH);
    }

    public static void playClothSound() {
        playSound(CLOTH_SOUND_PATH);
    }

    public static void playErrorSound() {
        playSound(ERROR_SOUND_PATH);
    }

    public static void playOpenDoorSound() {
        playSound(OPEN_DOOR_SOUND_PATH);
    }

    public static void playWinSound() {
        playSound(WIN_SOUND_PATH);
    }

    public static void playThrowSound() {
        playSound(THROW_SOUND_PATH);
    }

    private static void playSound(String soundFilePath) {
        try (InputStream soundStream = SoundPlayerUtil.class.getResourceAsStream(soundFilePath)) {
            if (soundStream == null) {
                throw new IOException("Sound file not found: " + soundFilePath);
            }

            BufferedInputStream bufferedStream = new BufferedInputStream(soundStream);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Failed to play sound: " + soundFilePath + " - " + e.getMessage());
        }
    }
}
