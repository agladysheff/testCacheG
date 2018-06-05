package com.agladyshev.cache;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.stream.IntStream;


public class CacheTest {

    private CacheImpl<String,String> cache;
    private final String KEY1="key1";
    private final String KEY2="key2";
    private final String VALUE1="value1";
    private final String VALUE2="value2";

    @Before
    public  void init(){
        cache=new CacheImpl<>(StrategyType.A,10,10);

    }
    @After
    public  void clearCache(){cache.clear();}

    @Test
    public void addGetRemoveSizeTest(){
        cache.put(KEY1,VALUE1);
        Assert.assertEquals(VALUE1, cache.get(KEY1));
        Assert.assertEquals(1,cache.size());
        cache.put(KEY2,VALUE2);
        Assert.assertEquals(VALUE2, cache.get(KEY2));
        Assert.assertEquals(2,cache.size());
        cache.remove(KEY1);
        Assert.assertNull(cache.get(KEY1));
        Assert.assertEquals(1,cache.size());
        cache.remove(KEY2);
        Assert.assertNull(cache.get(KEY2));
        Assert.assertEquals(0,cache.size());
    }

    @Test
    public void clearCTest(){
        cache.put(KEY1,VALUE1);
        cache.put(KEY2,VALUE2);
        cache.clear();
        Assert.assertNull(cache.get(KEY1));
        Assert.assertNull(cache.get(KEY2));
        Assert.assertEquals(0,cache.size());
    }

    @Test
    public void CTest() {
        IntStream.range(0, 20).forEach(x -> cache.put("k" + x, "v" + x)
        );
        IntStream.range(0, 10).forEach(x -> Assert.assertTrue(cache.getCacheDisk().containsKey("k" + x)));
        IntStream.range(11, 20).forEach(x -> Assert.assertTrue(cache.getCacheMemory().containsKey("k" + x)));
    }






}


