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
}
