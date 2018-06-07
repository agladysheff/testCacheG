package com.agladyshev.cache;


import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;


class CacheSubMemory<K, V> implements CacheSub<K, V> {
    private final Map<K, V> storage = new LinkedHashMap<>();
    private final ReentrantReadWriteLock lockMemory = new ReentrantReadWriteLock();

    @Override
    public V put(K key, V val) {
        return storage.put(key, val);
    }

    @Override
    public V putSame(K key, V val) {
        return storage.put(key,val);
           }

    @Override
    public V get(Object key) {
        return storage.get(key);
    }

    @Override
    public V remove(Object key) {
        return storage.remove(key);
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public boolean containsKey(Object key) {
      return storage.containsKey(key);
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }

    public List<Map.Entry<K, V>> getCLastList(int num) {
        final List<Map.Entry<K, V>> list = new ArrayList<>();
           Iterator<Map.Entry<K, V>> iterator = storage.entrySet().iterator();
        int n = 0;
        while (iterator.hasNext() && n <num) {
            n++;
            Map.Entry<K, V> x = iterator.next();
            list.add(x);
            iterator.remove();
        }
           return list;
    }


}
