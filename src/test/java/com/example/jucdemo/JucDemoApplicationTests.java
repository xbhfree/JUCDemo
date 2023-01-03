package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

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