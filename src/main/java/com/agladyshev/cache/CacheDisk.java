package com.agladyshev.cache;



import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

public class CacheDisk<K extends Serializable, V extends Serializable> implements Cache<K, V> {
    private final Serialization<K, V> str = new Serialization<>();
    private final String DIR = "C:/994/";
    private AtomicInteger elementsAdded = new AtomicInteger(0);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
private int count=0;

    @Override
    public V put(K key, V val) {

        final File dir = dirHashKey(key);
        File toUse;

   //    lock.writeLock().lock();
       try {
            if (!dir.exists()) {
                dir.mkdirs();
                toUse = fileHashKey(key, val);
                elementsAdded.incrementAndGet();
//count++;
            } else {

                toUse = lookDir(dir, key);


                if (toUse == null) {
                    toUse = fileHashKey(key, val);
                   elementsAdded.incrementAndGet();
//count++;
                }

            }

            str.serializ(key, val, toUse);
        }

       finally {
        //   lock.writeLock().unlock();
       }
return val;
    }

    @Override
    public boolean containsKey(Object key) {

        File dir = dirHashKey(key);

        if (!dir.exists()) {
            return false;
        }
        File toUse = lookDir(dir, key);
        if (toUse == null) {
            return false;
        }
        return true;
    }

    @Override
    public V get (Object key) {

        File dir = dirHashKey(key);
V result;
        if (!dir.exists()) {
            return null;
        }

        File toUse = null;

        lock.readLock().lock();
        try {
            toUse = lookDir(dir, key);


        if (toUse == null) {
            return null;
        }



            result = str.unserializ(toUse).getValue();}
         finally {
            lock.readLock().unlock();

        }

        return result;
    }

    @Override
    public V remove(Object key) {
V value;
        File dir = dirHashKey(key);

        lock.writeLock().lock();
        try {
            if (dir.exists()) {

                File toUse = lookDir(dir, key);

                if (toUse != null) {

                    toUse.delete();

                    elementsAdded.decrementAndGet();
//count--;
                }
            }
            value=get(key);
        } finally {
            lock.writeLock().unlock();
        }

    return value;}



    @Override
    public void clear() {


        try {
            FileUtils.deleteDirectory(new File(DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }


       elementsAdded.set(0);
//count=0;
    }



    @Override
    public int size() {
        return elementsAdded.get();
        //count;
    }

    @Override
    public boolean isEmpty() {
        return elementsAdded.get()==0?true:false;
    }

    private String randomString() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private File dirHashKey(Object key) {
        return new File(DIR + key.hashCode() + "/");
    }

    private File fileHashKey(K key, V val) {
        return new File(DIR + key.hashCode() + "/" + val.hashCode() + randomString() + ".txt");
    }

    public List<Map.Entry<K, V>> getCLastList(int num) {
        throw new UnsupportedOperationException();
    }

    ;

    private File lookDir(File dir, Object key) {
        return Stream.of(dir.listFiles())
                .filter(x -> (str.unserializ(x).getKey()).equals(key))
                .findFirst()
                .orElse(null);
    }


}
