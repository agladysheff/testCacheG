package com.agladyshev.cache;


import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheMemory<K, V> implements Cache<K, V> {
    private final Map<K, V> storage = new LinkedHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    @Override
    public V put(K key, V val) {
        V result;
       // lock.writeLock().lock();
        try {
            result = storage.put(key, val);
        } finally {
        //    lock.writeLock().unlock();
        }
        return result;
    }



    @Override
    public V get (Object key) {
        V value;
        lock.readLock().lock();
        try {
             value = storage.get(key);
        }
        finally {
            lock.readLock().unlock();
        }


        return value;
    }

    @Override
    public V remove(Object key) {
        V value;
        lock.writeLock().lock();
        try {
            value=storage.remove(key);
        } finally {
            lock.writeLock().unlock();

        }
        return value;
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
        return storage.size()==0?true:false;
    }

    public List<Map.Entry<K, V>> getCLastList(int num) {
        final List<Map.Entry<K, V>> list = new ArrayList<>();
        lock.writeLock().lock();
        Iterator<Map.Entry<K, V>> iterator = storage.entrySet().iterator();
        int n = 0;
        while (iterator.hasNext() && n <num) {
            n++;
            Map.Entry<K, V> x = iterator.next();
            list.add(x);
            iterator.remove();
        }
        lock.writeLock().unlock();
        return list;
    }


}
