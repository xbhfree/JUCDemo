package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

@SpringBootTest
public class CompletableFutureAPIDemo  {

    @Test
    public void test01()  {
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

    /**
     * handle相比于thenApply可以携带异常继续处理后续步骤
     */
    @Test
    public void test02() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("---1-----");
            return 1;
        }, executorService).handle((v, e) -> {
            int i = 10/0;
            System.out.println("---2-----");
            return v + 2;
        }).handle((v, e) -> {
            System.out.println("---3-----");
            return v + 3;
        }).handle((v, e) -> {
            System.out.println("---4-----");
            return v + 4;
        }).whenComplete((v, e) -> {
            System.out.println("---v-----" + v);
        }).exceptionally(e -> {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        });
        System.out.println(future.join());
        System.out.println(Thread.currentThread().getName() + "主线程");
        executorService.shutdown();
    }

    /**
     * thenRun thenApply thenAccept
     */
    @Test
    public void test03(){
        // thenRun A步骤与B步骤无关
        System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenRun(() -> {}).join());
        // 执行完A，A返回，B无返回，B可以用A的数据 accpet类似流的终结语句
        System.out.println("+++" + CompletableFuture.supplyAsync(() -> "resultA").thenApply(r -> r + " resulatC").thenApply(r -> r + " resulatD").thenAccept(r -> System.out.println("?????" + r)).thenApply(r -> r + " resulatGG").thenAccept(r -> System.out.println("____" + r)).join());
        //执行完A，与B结合返回值  apply类似流管道
        System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenApply(r -> r + " resulatB").join());
    }

    /**
     * CompletableFuture之对计算速度选用
     * applyToEither 返回结果更快的任务结果
     */
    @Test
    public void test04(){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "A";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "B";
        });
        //返回执行任务更快的结果为 f
        CompletableFuture<String> future3 = future.applyToEither(future2, f -> {
            return f + " is winner";
        });
        System.out.println(future3.join());
    }

    /**
     * CompletableFuture之对计算结果合并
     */
    @Test
    public void test05(){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "A";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "B";
        });

        CompletableFuture<String> future1 = future.thenCombine(future2, (x, y) -> {
            return x + y;
        });
        System.out.println(future1.join());

    }
}
