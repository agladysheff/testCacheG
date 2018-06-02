package com.agladyshev.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class CuchCuncTest {


    private CacheImpl<Integer,Integer> cache;


    @Before
    public  void init(){
        cache=new CacheImpl<>(StrategyType.G,10000,10000);

    }
    @After
    public  void clearCache(){cache.clear();}



    @Test
    public void mult() throws InterruptedException {
        Thread thread=new Thread(()->IntStream.range(1,10000).forEach(x->cache.put(x,x)));
        Thread thread1=new Thread(()->IntStream.range(10000,20000).forEach(x->cache.put(x,x)));
        thread.start();
        thread1.start();
        thread.join();
        thread1.join();
        System.out.println(cache.size());


    }
    @Test
    public void multRem() throws InterruptedException {
       IntStream.range(1,20000).forEach(x->cache.put(x,x));
        System.out.println(cache.size());
        Thread thread=new Thread(()->IntStream.range(1,10000).forEach(x->cache.remove(x)));
        Thread thread1=new Thread(()->IntStream.range(1000,20000).forEach(x->cache.remove(x)));
        thread.start();
        thread1.start();
        thread.join();
        thread1.join();
        System.out.println(cache.size());


    }

    @Test
    public void multGet() throws InterruptedException {
        IntStream.range(1,20000).forEach(x->cache.put(x,x));
        System.out.println(cache.size());
        Thread thread=new Thread(()->IntStream.range(1,10000).forEach(x->cache.get(x)));
        Thread thread1=new Thread(()->IntStream.range(1000,20000).forEach(x->cache.get(x)));
        thread.start();
        thread1.start();
        thread.join();
        thread1.join();
        System.out.println(cache.size());


    }



}
