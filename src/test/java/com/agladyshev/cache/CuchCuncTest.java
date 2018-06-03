package com.agladyshev.cache;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class CuchCuncTest {


    private CacheImpl<Integer,Integer> cache;
Integer i=0;

    @Before
    public  void init(){
        cache=new CacheImpl<>(StrategyType.A,1500,1000);

    }
    @After
    public  void clearCache(){cache.clear();}



    @Test
    public void mult() throws InterruptedException {
        Thread thread=new Thread(()->IntStream.range(1,1000).forEach(x->cache.put(x,x)));
        Thread thread1=new Thread(()->IntStream.range(1000,2000).forEach(x->cache.put(x,x)));
        Thread thread2=new Thread(()->IntStream.range(2000,3000).forEach(x->cache.put(x,x)));
        thread.start();
        thread1.start();
        thread2.start();
        thread.join();
        thread1.join();
        thread2.join();
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
    @Test
    public  void ddd() {


        try {
            for (i = 0; i <1000 ; i++) {

                Executor executor=Executors.newFixedThreadPool(10);

            Integer future1 = CompletableFuture.runAsync(() -> cache.put(i, i))
                    .thenApplyAsync(x -> cache.get(2)).get();

            Assert.assertEquals((Integer)i,future1);}
        } catch (InterruptedException  |ExecutionException  e) {
            e.printStackTrace();
        }

    }


    @Test
    public void mult1() throws InterruptedException {

        ExecutorService executorService= Executors.newFixedThreadPool(100);
Future future   =     executorService.submit(()->IntStream.range(1,30001).forEach(x->cache.put(x,x)));

while (!future.isDone())
{}
        System.out.println(cache.size());
        }

    }


