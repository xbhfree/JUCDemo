package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class VolatileDemo {

    static volatile boolean flag = true;
    public static void main(String[] args) {

    }

    /**
     * 验证volatile的可见性
     */
    @Test
    public void test01(){
        new Thread(() -> {
            System.out.println("开始循环");
            while (flag){

            }
            System.out.println("循环结束,flag = " + flag);
        },"t1").start();
        try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}
        System.out.println(Thread.currentThread().getName()+ "设置flag为false");
        flag = false;

    }

    static volatile int num = 0;
    AtomicInteger atomicInteger = new AtomicInteger(0);
    /**
     * 验证volatile无原子性
     */
    @Test
    public void test02(){
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    atomicInteger.addAndGet(1);
                    num++;
                }
            },String.valueOf(i)).start();
        }
        try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}
        System.out.println("num = " + num);
        System.out.println(atomicInteger.get());
    }
}
