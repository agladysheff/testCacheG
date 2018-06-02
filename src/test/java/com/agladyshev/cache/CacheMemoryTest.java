package com.agladyshev.cache;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CacheMemoryTest {

    private CacheMemory<String,String> cache;
    private final String KEY1="key1";
    private final String KEY2="key2";
    private final String VALUE1="value1";
    private final String VALUE2="value2";

    @Before
    public  void init(){
        cache=new CacheMemory<>();

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



}
