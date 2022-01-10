package frc.shufflewood;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class GuiStorage {
  private final Map<Object, Object> storage;
  private final Set<Object> unusedKeys;

  public GuiStorage() {
    storage = new HashMap<>();
    unusedKeys = new HashSet<>();
  }

  public void set(Object key, Object value) {
    storage.put(key, value);
    unusedKeys.remove(key);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(Object key, T def) {
    unusedKeys.remove(key);
    return (T) storage.getOrDefault(key, def);
  }

  @SuppressWarnings("unchecked")
  public <T> T getOrSet(Object key, T def) {
    unusedKeys.remove(key);
    if (storage.containsKey(key)) {
      return (T) storage.get(key);
    } else {
      storage.put(key, def);
      return def;
    }
  }

  public void update() {
    for (Object key : unusedKeys) {
      storage.remove(key);
    }

    unusedKeys.clear();
    unusedKeys.addAll(storage.keySet());
  }
}