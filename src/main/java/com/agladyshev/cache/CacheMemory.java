package com.agladyshev.cache;


import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class CacheMemory<K, V> implements Cache<K, V> {
    private final Map<K, V> storage = new LinkedHashMap<>();

    @Override
    public  V put(K key, V val) {
        V result;
            result = storage.put(key, val);
        return result;
    }

    @Override
    public  V get (Object key) {
        V value;
                value = storage.get(key);
              return value;
    }

    @Override
    public V remove(Object key) {
        V value;
            value=storage.remove(key);
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
