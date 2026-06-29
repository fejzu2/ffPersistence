package pl.fejzu.persistence.core.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class EventBus {

    private final Map<Class<?>, List<Consumer<Object>>> handlers = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
            .add((Consumer<Object>) handler);
    }

    public <T> void publish(T event) {
        List<Consumer<Object>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            eventHandlers.forEach(handler -> handler.accept(event));
        }
    }

    public <T> void unsubscribe(Class<T> eventType) {
        handlers.remove(eventType);
    }

    public void clear() {
        handlers.clear();
    }
}
