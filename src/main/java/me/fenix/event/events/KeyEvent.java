package me.fenix.event.events;

import com.darkmagician6.eventapi.events.Event;

public class KeyEvent implements Event {
    private final int keyCode;

    public KeyEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
