package src.main.java.technicalservices;

public class Timer {
    private int remainingTime; // Time left in seconds
    private boolean isRunning;

    public Timer(int startTime) {
        this.remainingTime = startTime;
        this.isRunning = false;
    }

    public void start() {
        isRunning = true;
        System.out.println("Timer started.");
    }

    public void pause() {
        isRunning = false;
        System.out.println("Timer paused.");
    }

    public void resume() {
        isRunning = true;
        System.out.println("Timer resumed.");
    }

    public void tick() {
        if (isRunning && remainingTime > 0) {
            remainingTime--;
            System.out.println("Time remaining: " + remainingTime + " seconds");
        }
    }

    public boolean isTimeUp() {
        return remainingTime <= 0;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void addTime(int seconds) {
        remainingTime += seconds;
        System.out.println(seconds + " seconds added. Total time: " + remainingTime + " seconds.");
    }

    public void reset(int newTime) {
        remainingTime = newTime;
        isRunning = false;
        System.out.println("Timer reset to " + newTime + " seconds.");
    }
}
