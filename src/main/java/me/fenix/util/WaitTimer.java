package me.fenix.util;

public class WaitTimer {
    private long time;

    public WaitTimer() {
        time = (System.nanoTime() / 1000000L);
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (getTime() >= time) {
            if (reset) {
                reset();
            }
            return true;
        }
        return false;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L - time;
    }

    public void reset() {
        time = (System.nanoTime() / 1000000L);
    }
}
