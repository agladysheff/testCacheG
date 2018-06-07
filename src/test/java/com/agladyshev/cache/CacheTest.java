package com.agladyshev.cache;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.stream.IntStream;
import static org.junit.Assert.*;


public class CacheTest {

    private Cache<String,String> cache;
    private final String KEY1="key1";
    private final String KEY2="key2";
    private final String VALUE1="value1";
    private final String VALUE2="value2";
    private final Integer SIZE_CACHE_MEMORY=10;
    private final Integer SIZE_CACHE_DISK=10;

    @Before
    public  void init(){
        cache=new Cache<>("C:/994/",StrategyType.G,SIZE_CACHE_MEMORY,SIZE_CACHE_DISK);

    }
    @After
    public  void clearCache(){cache.clear();}

    @Test
    public void addGetRemoveSizeTest(){
        cache.put(KEY1,VALUE1);
        assertEquals(VALUE1, cache.get(KEY1));
        assertEquals(1,cache.size());
        assertTrue(cache.containsKey(KEY1));
        cache.put(KEY2,VALUE2);
        assertEquals(VALUE2, cache.get(KEY2));
        assertEquals(2,cache.size());
        assertTrue(cache.containsKey(KEY2));
        cache.remove(KEY1);
        assertNull(cache.get(KEY1));
        assertEquals(1,cache.size());
        assertFalse(cache.containsKey(KEY1));
        cache.remove(KEY2);
        assertNull(cache.get(KEY2));
        assertEquals(0,cache.size());
        assertFalse(cache.containsKey(KEY2));
        assertTrue(cache.isEmpty());
    }

    @Test
    public void clearCTest(){
        cache.put(KEY1,VALUE1);
        cache.put(KEY2,VALUE2);
        cache.clear();
        assertNull(cache.get(KEY1));
        assertNull(cache.get(KEY2));
        assertEquals(0,cache.size());
    }

    @Test
    public void CTest() {
        IntStream.range(0, SIZE_CACHE_MEMORY + SIZE_CACHE_DISK).forEach(x -> cache.put("k" + x, "v" + x)
        );

      //  IntStream.range(0, SIZE_CACHE_MEMORY).forEach(x -> assertTrue(cache.getCacheDisk().containsKey("k" + x)));
   //     IntStream.range(SIZE_CACHE_MEMORY + 1, SIZE_CACHE_MEMORY + SIZE_CACHE_DISK)
   //             .forEach(x -> assertTrue(cache.getCacheMemory().containsKey("k" + x)));
       IntStream.range(1,20).forEach(x-> System.out.println(cache.getCacheMemory().containsKey("k"+x)+"  "+ x));

        System.out.println("k"+1);
        cache.get("k"+2);
        System.out.println("add");
        IntStream.range(1,20).forEach(x-> System.out.println(cache.getCacheMemory().containsKey("k"+x)+"  "+ x));
    }




}


