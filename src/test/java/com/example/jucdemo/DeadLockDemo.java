package com.example.jucdemo;

import java.util.concurrent.TimeUnit;

/**
 * 死锁示例
 */
public class DeadLockDemo {
    public static void main(String[] args) {
        Object a = new Object();
        Object b = new Object();
        new Thread(() -> {
            synchronized (a){
                System.out.println(Thread.currentThread().getName() + "持有a锁，希望获得b锁");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (b){
                    System.out.println(Thread.currentThread().getName() + "持有b锁");
                }
            }
        },"t1").start();

        new Thread(() -> {
            synchronized (b){
                System.out.println(Thread.currentThread().getName() + "持有b锁，希望获得a锁");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (a){
                    System.out.println(Thread.currentThread().getName() + "持有a锁");
                }
            }
        },"t2").start();
    }
}
