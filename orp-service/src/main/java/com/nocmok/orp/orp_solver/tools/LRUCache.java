package com.nocmok.orp.orp_solver.tools;

import java.util.LinkedHashMap;

public class LRUCache<K, V> {

    private LinkedHashMap<K, V> cache;

    private int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<K, V>(capacity);
    }

    public void add(K key, V value) {
        if (!cache.containsKey(key)) {
            cache.put(key, value);
        } else {
            cache.remove(key);
            cache.put(key, value);
        }
        if (cache.size() > capacity) {
            cache.remove(cache.keySet().iterator().next());
        }
    }

    public boolean has(K key) {
        return cache.containsKey(key);
    }

    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            cache.remove(key);
            cache.put(key, value);
        }
        return value;
    }
}
