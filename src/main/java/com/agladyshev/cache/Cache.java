package com.agladyshev.cache;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;



public class Cache<K extends Serializable, V extends Serializable>  {
    private final CacheSub<K, V> cacheMemory;
    private  final CacheSubDisk<K, V> cacheDisk ;
    private final int sizeCacheMemory;
    private final int sizeCacheDisk;
    private final ReentrantReadWriteLock  lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock  lock1 = new ReentrantReadWriteLock();
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


    public  V put(K key, V val) {
        if (cacheMemory.containsKey(key)) {
            cacheMemory.putSame(key, val);
            return val;
        }
        if (cacheDisk.containsKey(key)) {
            cacheDisk.putSame(key, val);
            return val;
        }
        if (cacheMemory.size() < sizeCacheMemory) {
           lock.writeLock().lock();
            try {
                cacheMemory.put(key, val);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            if (cacheDisk.size() >= sizeCacheDisk) {
                System.out.println("key " + key + " overflow");
            } else {
                lock.writeLock().lock();
                                         try {
                     over().forEach(x->cacheDisk.put(x.getKey(), x.getValue()));
                     cacheMemory.put(key, val);
                } finally {

                    lock.writeLock().unlock();
                }
            }
        }
        return val;
    }

    public V get(Object key) {
        lock.readLock().lock();
        try {
            V result = cacheMemory.get(key);
           if (result == null) {
               result = cacheDisk.get(key);
               if (strategy == StrategyType.G) {
                   if (result != null) {
                       lock1.writeLock().lock();
                       try {
                           List<Map.Entry<K, V>> as = cacheMemory.getCLastList(1);
                           K k = as.get(0).getKey();
                           V v = as.get(0).getValue();
                           cacheDisk.remove(key);
                           cacheMemory.remove(k);
                           cacheMemory.put((K) key, result);
                           cacheDisk.put(k, v);
                       } finally {
                           lock1.writeLock().unlock();
                       }
                   }
               }
           }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }


    public V remove(Object key) {
       lock.writeLock().lock();
        try {
            V value = cacheMemory.remove(key);
            if (value == null) value = cacheDisk.remove(key);
            return value;
        } finally {
            lock.writeLock().unlock();
        }
    }


    public void clear() {
        cacheDisk.clear();
        cacheMemory.clear();
    }


    public int size() {
        lock.readLock().lock();
        try {
            return cacheMemory.size() + cacheDisk.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Map.Entry<K, V>> over() {
        int diffDisk = sizeCacheDisk - cacheDisk.size();
        int shareMemory = sizeCacheMemory / 5;
        if (diffDisk >= shareMemory) {
            return cacheMemory.getCLastList(shareMemory);
        }
        {
            return cacheMemory.getCLastList(diffDisk);
        }
    }


    public boolean containsKey(Object key) {
        if (cacheMemory.containsKey(key)) return true;
        if (cacheDisk.containsKey(key)) return true;
        return false;
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
