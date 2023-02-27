package com.example.jucdemo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 借助AtomicReference实现自旋锁测试
 */
public class CASSpinLockDemo {

    AtomicReference<Thread> reference = new AtomicReference<>();

    public void lock(){
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName() + " come in---");
        //reference为null则为true，跳出循环，持有锁
        while (!reference.compareAndSet(null, thread)){

        }
    }

    public void unlock(){
        Thread thread = Thread.currentThread();
        reference.compareAndSet(thread, null);
        System.out.println(thread.getName() + " unlock---");
    }

    static CASSpinLockDemo casSpinLockDemo = new CASSpinLockDemo();

    public static void main(String[] args) {
        new Thread(() -> {
            casSpinLockDemo.lock();
            try {TimeUnit.SECONDS.sleep(5);} catch (InterruptedException e) {throw new RuntimeException(e);}
            casSpinLockDemo.unlock();
        },"a").start();
        try {TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException e) {throw new RuntimeException(e);}
        new Thread(() -> {
            casSpinLockDemo.lock();

            casSpinLockDemo.unlock();
        },"b").start();
    }


}
