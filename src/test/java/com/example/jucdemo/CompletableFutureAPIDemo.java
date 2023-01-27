package com.example.jucdemo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFutureAPIDemo  {
    public static void main(String[] args) throws Exception  {
            CompletableFuture<String> future = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return "abc";
            });
        //System.out.println(future.get());
        //System.out.println(future.get(1L, TimeUnit.SECONDS));
        //System.out.println(future.join());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //算不出来就用getNow内的结果
        //System.out.println(future.getNow("321"));
        //算不出来为true，join输出异步计算命名；算出来为false,join输出计算结果
        System.out.println(future.complete("future") + "\t" + future.join());

    }
}
