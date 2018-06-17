package com.agladyshev.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;


class CacheSubMemory<K, V> implements CacheSub<K, V> {
    private final Map<K, V> storage = new LinkedHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public V put(K key, V val) {
 lock.writeLock().lock();
        try {
             storage.put(key, val);
        } finally {
            lock.writeLock().unlock();
        }
        return val;
    }

    @Override
    public V get(Object key) {

        V result;
        lock.readLock().lock();
        try {
            result = storage.get(key);
        } finally {
            lock.readLock().unlock();
        }
        return result;
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
        int result;
        lock.readLock().lock();
        try {
            result = storage.size();
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean result;
        lock.readLock().lock();
        try {
            result = storage.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    @Override
    public boolean containsValue(Object value) {
        return storage.containsValue(value);
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }

    public List<Entry<K, V>> getCLastList(int num) {
         List<Map.Entry<K, V>> list = new ArrayList<>();
         Iterator<Entry<K, V>> iterator = storage.entrySet().iterator();
        int n = 0;
        while (iterator.hasNext() && n < num) {
            n++;
            Map.Entry<K, V> x = iterator.next();
            list.add(x);
            iterator.remove();
        }
           return list;
    }


}
