package frc.shufflewood.gui.input;

public class KeyTypedEvent implements Event {
    private final char c;

    public KeyTypedEvent(char c) {
        this.c = c;
    }

    public char getChar() {
        return c;
    }
}
