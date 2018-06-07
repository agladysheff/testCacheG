package com.agladyshev.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;


public class CachMultTestA {


    private Cache<Integer, Integer> cache;
    private final Integer SIZE_CACHE_MEMORY=1500;
    private final Integer SIZE_CACHE_DISK=1500;

    @Before
    public void init() {
        cache = new Cache<>("C:/994/",StrategyType.A, SIZE_CACHE_MEMORY, SIZE_CACHE_DISK);
    }

    @After
    public void clearCache() {
        cache.clear();
    }

    @Test
    public void multPut()  {
        Integer n = 10;
        Integer diff = 400;
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        IntStream.range(0, n).mapToObj(y -> executorService.submit(() -> IntStream.range(y*diff, y*diff + diff).forEach(x -> cache.put(x, x))))
                .collect(Collectors.toList()).forEach(z -> {
            while (!z.isDone()) {
            }
        });
        IntStream.range(0, n).mapToObj(y -> executorService.submit(() -> IntStream.range(y*diff, y*diff + diff).forEach(x -> cache.put(x, x))))
                .collect(Collectors.toList()).forEach(z -> {
            while (!z.isDone()) {
            }
        });
        assertEquals(Math.min(SIZE_CACHE_DISK+SIZE_CACHE_MEMORY,n*diff), cache.size());
    }

    @Test
    public void multRemove() {
        Integer n =20;
        Integer diff = 100;
        IntStream.range(0, n*diff).forEach(x -> cache.put(x, x));
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        IntStream.range(0, n).mapToObj(y -> executorService.submit(() -> IntStream.range(y*diff, y*diff + diff).forEach(x -> cache.remove(x))))
                .collect(Collectors.toList()).forEach(z -> {
            while (!z.isDone()) {
            }
        });
        assertEquals(0, cache.size());
    }

    @Test
    public void multPutGet() {
        Integer n = 20;
        Integer diff = 150;
        List<List<Integer>> listListResult = IntStream.range(0, n).mapToObj(x -> new ArrayList<Integer>()).collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Integer> listExpect = IntStream.range(0, n * diff).mapToObj(x ->
                x).collect(Collectors.toList());
        IntStream.range(0, n).mapToObj(x -> executorService.submit(() -> asyncPutGet(x, diff, listListResult.get(x)))).collect(Collectors.toList()).forEach(x -> {
            while (!x.isDone()) {
            }
        });
        List<Integer> listResult = listListResult.stream().flatMap(x -> x.stream()).peek(y-> assertEquals(y,listExpect.get(y))

            ).collect(Collectors.toList());
        assertEquals(listExpect,listResult);
        assertEquals(n * diff, cache.size());
    }

    private void asyncPutGet(Integer z, Integer diff, List<Integer> list) {
        Integer nStart = z * diff;
        Integer nStop = nStart + diff;
        IntStream.range(nStart, nStop).forEach(x -> {
            try {
                list.add(CompletableFuture.runAsync(() -> cache.put(x, x))
                        .thenApplyAsync((y) -> cache.get(x)).get());
                         } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}


