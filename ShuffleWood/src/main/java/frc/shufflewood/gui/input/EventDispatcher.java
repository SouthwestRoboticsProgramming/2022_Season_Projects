package frc.shufflewood.gui.input;

import java.util.function.Consumer;

public class EventDispatcher {
    private final Event event;

    public EventDispatcher(Event event) {
        this.event = event;
    }

    public <T extends Event> void dispatch(Class<T> type, Consumer<T> handler) {
        if (type.isInstance(event)) {
            handler.accept(type.cast(event));
        }
    }
}
