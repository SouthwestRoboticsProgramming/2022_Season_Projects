package frc.shuffleplank.gui;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class GuiStorage {
    private final Map<Object, Object> data;
    private final Set<Object> unusedKeys;

    public GuiStorage() {
        data = new WeakHashMap<>();
        unusedKeys = new HashSet<>();
    }

    public void store(Object key, Object value) {
        unusedKeys.remove(key); // Mark key as used
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object key) {
        unusedKeys.remove(key); // Mark key as used
        return (T) data.get(key);
    }

    public int size() {
        return data.size();
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return data.entrySet();
    }

    // Removes all entries that were not set or accessed during the last frame.
    public void flushUnusedEntries() {
        for (Object key : unusedKeys) {
            data.remove(key);
        }
        unusedKeys.clear();
        unusedKeys.addAll(data.keySet());
    }
}
