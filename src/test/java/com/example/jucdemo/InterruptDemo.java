package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest
public class InterruptDemo {
    static volatile boolean isStop = false;
    static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    /**
     * 中断机制之通过volatile实现线程中断停止
     */
    @Test
    public void test01(){
        new Thread(() -> {
            while (true){
                if (isStop){
                    System.out.println(Thread.currentThread().getName() + "线程结束,isStop=" + isStop);
                    break;
                }
                System.out.println(Thread.currentThread().getName() + "任务中");
            }
        }, "t1").start();
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            isStop = true;
        },"t2").start();
    }


    /**
     * 中断机制之通过AtomicBoolean实现线程中断停止
     */
    @Test
    public void test02(){
        new Thread(() -> {
            while (true){
                if (atomicBoolean.get()){
                    System.out.println(Thread.currentThread().getName() + "线程结束,atomicBoolean=" + atomicBoolean.get());
                    break;
                }
                System.out.println(Thread.currentThread().getName() + "任务中");
            }
        }, "t1").start();
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            atomicBoolean.set(true);
        },"t2").start();
    }

    /**
     * 中断机制之通过interrupt实现线程中断停止
     */
    @Test
    public void test03(){
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + "线程结束");
                    break;
                }
                System.out.println(Thread.currentThread().getName() + "任务中");
            }
        }, "t1");
        t1.start();
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t1.interrupt();
    }

    /**
     * 测试interrupt标识性与对死亡线程的作用
     */
    @Test
    public void test04() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            int a = 0;
            for (int i = 0; i < 300; i++) {
                System.out.println("运行中 " + i);
                // 调用interrupt方法，活动线程会从阻塞状态立即跳出，将终端标志重置为false，并报InterruptedException
                /*try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
            }
            System.out.println("t1的interrupt标识状态00=" + Thread.currentThread().isInterrupted());
        },"t1");
        t1.start();
        System.out.println("t1的interrupt标识状态01=" + t1.isInterrupted());
        TimeUnit.MILLISECONDS.sleep(1);
        t1.interrupt();
        System.out.println("t1的interrupt标识状态02=" + t1.isInterrupted());
        TimeUnit.SECONDS.sleep(2);
        //对于死亡的线程，其interrupt状态为false
        System.out.println("t1的interrupt标识状态03=" + t1.isInterrupted());
    }

    /**
     * interrupt当线程阻塞时，中断会发生什么
     */
    @Test
    public void test05(){
        Thread t1 = new Thread(() -> {
            while (true) {
                System.out.println("---------hello-------" + Thread.currentThread().isInterrupted());
                if (Thread.currentThread().isInterrupted()){
                    System.out.println("t1线程中断");
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    //如果继续循环，可以在异常处再次调用interrupt方法，默认线程跳出阻塞后会将终端状态设置为false
                    System.out.println("InterruptedException----" + Thread.currentThread().isInterrupted());
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }, "t1");
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t1.interrupt();
        System.out.println("t2打断后t1中断标志01=" + t1.isInterrupted());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("t2打断后t1中断标志02=" + t1.isInterrupted());
    }
}
