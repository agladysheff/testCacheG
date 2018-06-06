package com.agladyshev.cache;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class CacheImpl<K extends Serializable, V extends Serializable> implements Cache<K, V> {
    private final Cache<K, V> cacheMemory;
    private  final CacheDisk<K, V> cacheDisk ;
    private final int sizeCacheMemory;
    private final int sizeCacheDisk;
    private final ReentrantReadWriteLock  lock = new ReentrantReadWriteLock();

    private StrategyType strategy;
    private String directory;


    public CacheImpl(String directory,StrategyType strategy, int sizeCacheMemory, int sizeCacheDisk) {
       this.directory=directory;
        this.sizeCacheMemory = sizeCacheMemory;
        this.sizeCacheDisk = sizeCacheDisk;
        this.strategy = strategy;
        this.cacheMemory=new CacheMemory<>();
        this.cacheDisk=new CacheDisk<>(directory);
    }

    @Override
    public V put(K key, V val) {
        if (containsKey(key)) {
            System.out.println("key " + key + " is already contains");
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
               cacheDisk.getLockDisk().readLock().lock();
                try {
                     over().forEach(x->cacheDisk.put(x.getKey(), x.getValue()));
                     cacheMemory.put(key, val);
                } finally {
                    lock.writeLock().unlock();
                    cacheDisk.getLockDisk().readLock().unlock();
                }
            }
        }
        return val;
    }

    @Override
    public V get(Object key) {
        V result;
        lock.readLock().lock();
        try {
            result = cacheMemory.get(key);
            if (result == null) {
                result = cacheDisk.get(key);
                if (strategy == StrategyType.G) {
                    if (result != null) {
                        lock.writeLock().lock();
                        try {
                            List<Entry<K, V>> as = cacheMemory.getCLastList(1);
                            K k = as.get(0).getKey();
                            V v = as.get(0).getValue();
                            cacheDisk.remove(key);
                            cacheMemory.remove(k);
                            cacheMemory.put((K) key, result);
                            cacheDisk.put(k, v);
                        } finally {
                            lock.writeLock().unlock();
                        }
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    @Override
    public V remove(Object key) {
        V value;
        lock.writeLock().lock();
        value = cacheMemory.remove(key);
        if (value == null) value = cacheDisk.remove(key);
        lock.writeLock().unlock();
        return value;
    }

    @Override
    public void clear() {
        cacheDisk.clear();
        cacheMemory.clear();
    }

    @Override
    public int size() {
        return cacheMemory.size() + cacheDisk.size();
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

    @Override
    public List<Map.Entry<K, V>> getCLastList(int num) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        if (cacheMemory.containsKey(key)) return true;
        if (cacheDisk.containsKey(key)) return true;
        return false;
    }

    Cache<K, V> getCacheMemory() {
        return cacheMemory;
    }

    Cache<K, V> getCacheDisk() {
        return cacheDisk;
    }

    @Override
    public boolean isEmpty() {
        return cacheDisk.isEmpty() && cacheMemory.isEmpty();
    }
}
