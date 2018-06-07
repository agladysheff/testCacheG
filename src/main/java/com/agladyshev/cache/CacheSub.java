package com.agladyshev.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CacheSub<K, V> extends Map<K, V> {

    default void putAll(Map m) {
        throw new UnsupportedOperationException();
    }

    default boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    default Set keySet() {
        throw new UnsupportedOperationException();
    }

    default Collection values() {
        throw new UnsupportedOperationException();
    }

    default Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    List<Entry<K, V>> getCLastList(int num);

    V putSame(K key, V val);


}