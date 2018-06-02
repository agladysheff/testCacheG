package com.agladyshev.cache;



import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class CacheImpl<K extends Serializable, V extends Serializable> implements Cache<K, V> {
    private final Cache<K, V> casheMemory = new CacheMemory<>();
    private final Cache<K, V> casheDisk = new CacheDisk<>();
    private StrategyType strategy;
    private final int sizeCasheMemory;
    private final int sizeCasheDisk;

    public CacheImpl(StrategyType strategy, int sizeCasheMemory, int sizeCasheDisk) {
        this.sizeCasheMemory = sizeCasheMemory;
        this.sizeCasheDisk = sizeCasheDisk;
        this.strategy=strategy;
    }

    @Override
    public V put(K key, V val) {
             if (containsKey(key)) {
            System.out.println("key " + key + " is already contains");
            return val;
        }

        if (casheMemory.size() < sizeCasheMemory) {
            casheMemory.put(key, val);
        } else {
            if (casheDisk.size() >= sizeCasheDisk) {
                System.out.println("key " + key + " overflow");
            } else {

                for (Map.Entry<K, V> x : over()
                        ) {
                    casheDisk.put(x.getKey(), x.getValue());

                }

                casheMemory.put(key, val);
            }
        }return val;
    }

    @Override
    public V get(Object key) {
        V result = casheMemory.get(key);
        if (result == null) {
            result = casheDisk.get(key);

            if (strategy == StrategyType.G) {
                if (result != null) {
                    List<Map.Entry<K, V>> as = casheMemory.getCLastList(1);
                    K k = as.get(0).getKey();
                    V v = as.get(0).getValue();

                    casheDisk.remove(key);
                    casheMemory.remove(k);
                    casheMemory.put((K)key, result);
                    casheDisk.put(k, v);
                }
            }
        }

        return result;
    }

    @Override
    public V remove(Object key) {
        V value;
        value=casheMemory.remove(key);
        if(value==null)value=casheDisk.remove(key);
        return value;
    }



    @Override
    public void clear() {
        casheDisk.clear();
        casheMemory.clear();

    }

    @Override
    public int size() {
        return casheMemory.size() + casheDisk.size();
    }

    public List<Map.Entry<K, V>> over() {
        int diffDisk = sizeCasheDisk - casheDisk.size();
        int shareMemory = sizeCasheMemory / 5;
        if (diffDisk >= shareMemory) {
            return casheMemory.getCLastList(shareMemory);
        }
        {
            return casheMemory.getCLastList(diffDisk);
        }

    }

    @Override
    public List<Map.Entry<K, V>> getCLastList(int num) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        if (casheMemory.containsKey(key)) return true;
        if (casheDisk.containsKey(key)) return true;
        return false;
    }

     Cache<K, V> getCasheMemory() {
        return casheMemory;
    }

     Cache<K, V> getCasheDisk() {
        return casheDisk;
    }

    @Override
    public boolean isEmpty() {
        return casheDisk.isEmpty()&&casheMemory.isEmpty();
    }
}
