package com.agladyshev.cache;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;



public class Cache<K extends Serializable, V extends Serializable>  {
    private final CacheSub<K, V> cacheMemory;
    private final CacheSub<K, V> cacheDisk ;
    private final int sizeCacheMemory;
    private final int sizeCacheDisk;
    private final ReentrantReadWriteLock  lock = new ReentrantReadWriteLock();
    private final Lock lockW =lock.writeLock();
    private final Lock lockR =lock.readLock();
    private StrategyType strategy;
    private final String directory;


    public Cache(String directory, StrategyType strategy, int sizeCacheMemory, int sizeCacheDisk) {
       this.directory=directory;
        this.sizeCacheMemory = sizeCacheMemory;
        this.sizeCacheDisk = sizeCacheDisk;
        this.strategy = strategy;
        this.cacheMemory =new CacheSubMemory<>();
        this.cacheDisk=new CacheSubDisk<>(directory);
    }

    public V put(K key, V val) {
        if (cacheMemory.containsKey(key)) {
            cacheMemory.put(key, val);
            return val;
        }
        if (cacheDisk.containsKey(key)) {
            cacheDisk.replace(key, val);
            return val;
        }
        lockW.lock();
        try {
            if (cacheMemory.size() < sizeCacheMemory) {
                cacheMemory.put(key, val);
                       } else {
                if (cacheDisk.size() >= sizeCacheDisk) {
                    System.out.println("key " + key + " overflow");
                } else {
                    over().forEach(x -> cacheDisk.put(x.getKey(), x.getValue()));
                    cacheMemory.put(key, val);
                }
            }
        } finally {
            lockW.unlock();
        }
        return val;
    }

    public V get(Object key) {
        V result;
        V resultD = null;
        lockR.lock();
        try {
            result = cacheMemory.get(key);
            if (result == null) {
                resultD = cacheDisk.get(key);
                result = resultD;
            }
        } finally {
            lockR.unlock();
        }
        if (strategy == StrategyType.G) {
            if (resultD != null) {
                lock.writeLock().lock();
                try {
                    Map.Entry<K,V> as= cacheMemory.entrySet().iterator().next();
                    K k = as.getKey();
                    V v = as.getValue();
                    cacheDisk.remove(key);
                    cacheMemory.remove(k);
                    cacheMemory.put((K)key, result);
                    cacheDisk.put(k, v);
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }
        return result;
    }

    public V remove(Object key) {
      V value;
       lockW.lock();
        try {
             value = cacheMemory.remove(key);
            if (value == null) value = cacheDisk.remove(key);

        } finally {
            lockW.unlock();
        }
        return value;
    }


    public void clear() {
       lockW.lock();
        try {
            cacheDisk.clear();
            cacheMemory.clear();
        } finally {
            lockW.unlock();
        }
    }

    public int size() {
        int result;
        lockR.lock();
        try {
            result= cacheMemory.size() + cacheDisk.size();
        } finally {
            lockR.unlock();
        }
        return result;
    }

    public boolean containsValue(Object value) {
        boolean result;
        lockR.lock();
        try {
            result= cacheMemory.containsValue(value)||cacheDisk.containsValue(value);
        } finally {
            lockR.unlock();
        }
        return result;
    }

    public List<Map.Entry<K, V>> over() {
        List<Map.Entry<K, V>> result;
        int diffDisk = sizeCacheDisk - cacheDisk.size();
        int shareMemory = sizeCacheMemory / 5;
        if (diffDisk >= shareMemory) {
            result = cacheMemory.getCLastList(shareMemory);
        } else {
            result = cacheMemory.getCLastList(diffDisk);
        }
        return result;
    }

    public boolean containsKey(Object key) {
        boolean result;
        lockR.lock();
        try {
            result= cacheMemory.containsKey(key) || cacheDisk.containsKey(key);
        } finally {
            lockR.unlock();
        }
        return result;
    }

     CacheSub<K, V> getCacheDisk() {
        return cacheDisk;
    }

     CacheSub<K, V> getCacheMemory() {
        return cacheMemory;
    }


    public boolean isEmpty() {
        return cacheDisk.isEmpty() && cacheMemory.isEmpty();
    }
}
