package com.agladyshev.cache;

import java.io.*;
import java.util.AbstractMap;

public class Serialization<K extends Serializable, V extends Serializable> {

    public void serializ(K key, V value, File file) {


        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
             ObjectOutputStream oos = new ObjectOutputStream(os);) {
            oos.writeObject(key);
            oos.writeObject(value);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException("IO exception during serializ", e);
        }
    }


    public AbstractMap.SimpleEntry<K, V> unserializ(File file) {
        AbstractMap.SimpleEntry<K, V> result;

        try (InputStream is = new BufferedInputStream(new FileInputStream(file));
             ObjectInputStream ois = new ObjectInputStream(is);) {
            @SuppressWarnings("unchecked")
            K key = (K) ois.readObject();
            @SuppressWarnings("unchecked")
            V value = (V) ois.readObject();
            result = new AbstractMap.SimpleEntry<>(key, value);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("IO exception during unserializ", e);
        }

        return result;
    }


}

