package com.agladyshev.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.AbstractMap;


 class Serialization<K extends Serializable, V extends Serializable> {

      void serialize(K key, V value, File file) {
         try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
              ObjectOutputStream oos = new ObjectOutputStream(os)) {
             oos.writeObject(key);
             oos.writeObject(value);
             oos.flush();
         } catch (IOException e) {
             throw new RuntimeException();
         }
     }

      AbstractMap.SimpleEntry<K, V> unserialize(File file) {
         AbstractMap.SimpleEntry<K, V> result;
         try (InputStream is = new BufferedInputStream(new FileInputStream(file));
              ObjectInputStream ois = new ObjectInputStream(is)) {
             @SuppressWarnings("unchecked")
             K key = (K) ois.readObject();
             @SuppressWarnings("unchecked")
             V value = (V) ois.readObject();
             result = new AbstractMap.SimpleEntry<>(key, value);
         } catch (ClassNotFoundException | IOException e) {
             throw new RuntimeException();
         }
         return result;
     }


}


