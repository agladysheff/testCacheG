package com.agladyshev.cache;



import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

class CacheSubDisk<K extends Serializable, V extends Serializable> implements CacheSub<K, V> {
    private final Serialization<K, V> str = new Serialization<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final String directory;
    private int count = 0;

    CacheSubDisk(String directory) {
        this.directory = directory;
    }

    @Override
    public V put(K key, V val) {
        File dir = dirHashKey(key);
        if (!dir.exists()) dir.mkdirs();
        count++;
        str.serialize(key, val, newFileHashVal(key, val));
        return val;
    }

    @Override
    public V replace (K key, V val) {
        lock.writeLock().lock();
        try {
            str.serialize(key, val, getFileFromDir(key));
        } finally {
            lock.writeLock().unlock();
        }
        return val;
    }

    @Override
    public boolean containsKey(Object key) {
       boolean result;
        lock.readLock().lock();
        try {
            result= dirHashKey(key).exists() && (getFileFromDir(key) != null);
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    @Override
    public V get(Object key) {
        V result = null;
        lock.readLock().lock();
        try {
            final File toUse;
            if (dirHashKey(key).exists() && (toUse = getFileFromDir(key)) != null) {
                result= str.unserialize(toUse).getValue();
            }
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    @Override
    public V remove(Object key) {
        File toUse;
        if (dirHashKey(key).exists() && (toUse = getFileFromDir(key)) != null) {
            toUse.delete();
            count--;
        }
        return get(key);
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
                  return   count;
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

    private File newFileHashVal(K key, V val) {
        return new File(directory + key.hashCode() + "/" + val.hashCode() + randomString() + ".txt");
    }
    public List<Map.Entry<K, V>> getCLastList(int num) {
        throw new UnsupportedOperationException();
    }

    private File getFileFromDir(Object key) {
        File[] files=dirHashKey(key).listFiles();
        if (files==null)return null;
        return Stream.of(files)
                .filter(x -> (str.unserialize(x).getKey()).equals(key))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean containsValue(Object value) {
        Boolean result = false;
       File [] folders=new File(directory).listFiles();
       if (folders!=null)
        result = Arrays.stream(folders)
                .map(x -> Arrays.asList(Objects.requireNonNull(x.listFiles())))
                .flatMap(Collection::stream)
                .anyMatch(z -> str.unserialize(z).getValue().equals(value));
        return result;
    }



}
