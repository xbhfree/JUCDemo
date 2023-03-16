package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁示例
 */
@SpringBootTest
public class ReentrantReadWriteLockDemo {
    /**
     * 1.演示读写锁的读资源不互斥
     * 2.演示读写锁的读写互斥
     */
    @Test
    public void test01() throws InterruptedException {
        MyResource resource = new MyResource();
        for (int i = 0; i < 10; i++) {
            int data = i;
            new Thread(() -> {
                resource.write(data, data*3);
            },String.valueOf(i)).start();
        }
        for (int i = 0; i < 10; i++) {
            int data = i;
            new Thread(() -> {
                resource.read(data);
            },String.valueOf(i)).start();
        }

        try {TimeUnit.SECONDS.sleep(5);} catch (InterruptedException e) {throw new RuntimeException(e);}

        for (int i = 0; i < 3; i++) {
           int data = i;
            new Thread(() -> {
                resource.write(data, data*3);
            },String.valueOf(i)).start();
        }

        try {TimeUnit.SECONDS.sleep(5);} catch (InterruptedException e) {throw new RuntimeException(e);}
    }
}

class MyResource{
    HashMap<Integer, Integer> map = new HashMap<>();

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void write(int key, int value){
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "写操作开始");
            map.put(key, value);
            try {TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException e) {throw new RuntimeException(e);}
            System.out.println(Thread.currentThread().getName() + "写操作完成");
        }finally {
            lock.writeLock().unlock();
        }
    }

    public void read(int key){
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "读操作开始");
            int s = map.get(key);
            //验证多读不互斥
            //try {TimeUnit.MILLISECONDS.sleep(200);} catch (InterruptedException e) {throw new RuntimeException(e);}
            //验证读写互斥
            try {TimeUnit.MILLISECONDS.sleep(1000);} catch (InterruptedException e) {throw new RuntimeException(e);}
            System.out.println(Thread.currentThread().getName() + "读操作完成，结果为" + s);
        }finally {
            lock.readLock().unlock();
        }
    }
}
