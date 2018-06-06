package com.agladyshev.cache;



import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

class CacheDisk<K extends Serializable, V extends Serializable> implements Cache<K, V> {
    private final Serialization<K, V> str = new Serialization<>();
   private  final ReentrantReadWriteLock lockDisk = new ReentrantReadWriteLock();
    private String directory;
    private int count = 0;

    public CacheDisk(String directory) {
        this.directory = directory;
    }

    @Override
    public V put(K key, V val) {
        final File dir = dirHashKey(key);
        File toUse;
        if (!dir.exists()) {
            dir.mkdirs();
            toUse = fileHashKey(key, val);
            count++;
        } else {
            toUse = lookDir(dir, key);
            if (toUse == null) {
                toUse = fileHashKey(key, val);
                count++;
            }
        }
        str.serialize(key, val, toUse);
        return val;
    }

    @Override
    public boolean containsKey(Object key) {
        File dir = dirHashKey(key);
        if (!dir.exists()) {
            return false;
        }
        File toUse = lookDir(dir, key);
     return !(toUse==null);
    }

    @Override
    public V get(Object key) {
        File dir = dirHashKey(key);
        V result;
        lockDisk.readLock().lock();
        try {
            if (!dir.exists()) {
                return null;
            }
            File toUse = lookDir(dir, key);
            if (toUse == null) {
                return null;
            }
            result = str.unserialize(toUse).getValue();
        } finally {
lockDisk.readLock().unlock();
        }
        return result;
    }

    @Override
    public V remove(Object key) {
        V value;
        File dir = dirHashKey(key);
        if (dir.exists()) {
            File toUse = lookDir(dir, key);
            if (toUse != null) {
                toUse.delete();
                count--;
            }
        }
        value = get(key);
        return value;
    }

    @Override
    public void clear() {
        try {
            FileUtils.deleteDirectory(new File(directory));
        } catch (IOException e) {
            e.printStackTrace();
        }
        count = 0;
    }

    @Override
    public int size() {
        return
                count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    private String randomString() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private File dirHashKey(Object key) {
        return new File(directory + key.hashCode() + "/");
    }

    private File fileHashKey(K key, V val) {
        return new File(directory + key.hashCode() + "/" + val.hashCode() + randomString() + ".txt");
    }
    public List<Map.Entry<K, V>> getCLastList(int num) {
        throw new UnsupportedOperationException();
    }

    private File lookDir(File dir, Object key) {
        return Stream.of(dir.listFiles())
                .filter(x -> (str.unserialize(x).getKey()).equals(key))
                .findFirst()
                .orElse(null);
    }
    public ReentrantReadWriteLock getLockDisk() {
        return lockDisk;
    }


}
