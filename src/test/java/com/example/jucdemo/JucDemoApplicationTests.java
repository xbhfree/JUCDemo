package com.example.jucdemo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.concurrent.*;

@SpringBootTest
class JucDemoApplicationTests {

    @Test
    void test01() {
        Thread thread = new Thread(() -> {

        }, "time01");
        thread.start();
    }


    @Test
    public void test02(){
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t 开始运行，是"
                    + (Thread.currentThread().isDaemon() ? "守护线程":"用户线程"));
            while (true){}
        }, "t1");
        t1.setDaemon(true);
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "主线程结束");
    }

    @Test
    public void test03(){
        FutureTask<String> futureTask = new FutureTask<>(new MyThread02());
        Thread thread = new Thread(futureTask);
        thread.start();
        try {
            System.out.println(futureTask.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " end");
    }

    @Test
    public void test04() throws ExecutionException, InterruptedException {

        FutureTask<String> futureTask = new FutureTask<>(()->{
            TimeUnit.MILLISECONDS.sleep(2000);
           return "task end";
        });
        FutureTask<String> futureTask2 = new FutureTask<>(()->{
            TimeUnit.MICROSECONDS.sleep(300);
            return "task end";
        });
        Thread thread = new Thread(futureTask);
        thread.start();
        while (true){
            if (futureTask.isDone()) {
                futureTask.get();
                break;
            }else{
                TimeUnit.MILLISECONDS.sleep(500);
                System.out.println("进度进行中。。。");
            }
        }
        System.out.println(Thread.currentThread().getName() + " end");

    }

    @Test
    public void test05(){
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName());
        },executor);
        try {
            System.out.println(voidCompletableFuture.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();
    }

    @Test
    public void test06(){
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CompletableFuture<String> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            return "hello";
        },executor);
        try {
            System.out.println(voidCompletableFuture.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();
    }

    /**
     * CompletableFuture任务完成通知，轮询优化，默认线程池会使异步线程为守护线程，主线程结束异步线程随之结束，需加额外线程池
     */
    @Test
    public void test07(){
        ExecutorService executor = Executors.newFixedThreadPool(3);
        try {
            CompletableFuture<Integer> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + "come in ----");
                int result = ThreadLocalRandom.current().nextInt(10);
                System.out.println("result = " + result);
                if (result > 3){
                    int i = 10/0;
                }
                return result;
            },executor).whenComplete((v, e) -> {
                if (e == null){
                    System.out.println("计算结果为 " + v);
                }
            }).exceptionally(e -> {
                e.printStackTrace();
                System.out.println(e.getCause() + "-------" + e.getMessage());
                return null;
            });
        }catch (Exception e){
            System.out.println(e.getCause() + "-------" + e.getMessage());
        }finally {
            executor.shutdown();
        }
    }

    @Test
    public void test08(){
        Student student = new Student();
        student.setId(1).setName("lisa");
        System.out.println(student);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "hello";
        });
        //和get类似，但不检查异常
        System.out.println(future.join());
    }
}
class MyThread01 implements Runnable{

    @Override
    public void run() {

    }
}

class MyThread02 implements Callable<String>{

    @Override
    public String call() throws Exception {
        System.out.println("--------MyThread02 come in");
        TimeUnit.SECONDS.sleep(3);
        return "hello Callable";
    }
}
@Data
//允许链式调用
@Accessors(chain = true)
class Student{
    private Integer id;
    private String name;
}