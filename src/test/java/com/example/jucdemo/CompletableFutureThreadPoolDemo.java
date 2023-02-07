package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class CompletableFutureThreadPoolDemo {
    /**
     * 1.不加自定义线程池，thenRun方法
     * 任务1----ForkJoinPool.commonPool-worker-1
     * 任务2----ForkJoinPool.commonPool-worker-1
     * 任务3----ForkJoinPool.commonPool-worker-1
     * 任务4----ForkJoinPool.commonPool-worker-1
     * 2.不加自定义线程池，thenRunAsync方法
     * 任务1----ForkJoinPool.commonPool-worker-1
     * 任务2----ForkJoinPool.commonPool-worker-1
     * 任务3----ForkJoinPool.commonPool-worker-1
     * 任务4----ForkJoinPool.commonPool-worker-1
     * 3.加自定义线程池，thenRun方法
     * 任务1----pool-1-thread-1
     * 任务2----pool-1-thread-1
     * 任务3----pool-1-thread-1
     * 任务4----pool-1-thread-1
     * 4.加自定义线程池，thenRunAsync方法,第几个用到thenRunAsync，开始用默认线程池ForkJoinPool.commonPool
     * 任务1----pool-1-thread-1
     * 任务2----pool-1-thread-1
     * 任务3----ForkJoinPool.commonPool-worker-1
     * 任务4----ForkJoinPool.commonPool-worker-1
     */
    @Test
    public void test01(){
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        try {
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("任务1----" + Thread.currentThread().getName());
                return "AAA";
            },executorService).thenRun(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("任务2----" + Thread.currentThread().getName());
            }).thenRun(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("任务3----" + Thread.currentThread().getName());
            }).thenRun(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("任务4----" + Thread.currentThread().getName());
            });
            voidCompletableFuture.get(2L, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            executorService.shutdown();
        }
    }
}
