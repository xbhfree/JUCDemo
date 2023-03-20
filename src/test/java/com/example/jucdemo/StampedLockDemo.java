package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * 邮戳锁示例
 * 笔记转代码脚本
 */
@SpringBootTest
public class StampedLockDemo {

    static int number = 10;

    StampedLock lock = new StampedLock();

    public void write(){
        long stamp = lock.writeLock();
        try {
            System.out.println(Thread.currentThread().getName() + "写入中。。。");
            number = number + 10;
        }finally {
            System.out.println(Thread.currentThread().getName() + "写入完成");
            lock.unlockWrite(stamp);
        }
    }

    public void read(){
        long stamp = lock.readLock();
        for (int i = 0; i < 3; i++) {
            try {TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {throw new RuntimeException(e);}
            System.out.println(Thread.currentThread().getName() + "正在读取，第" + i + "秒，当前number=" + number);
        }
        try {
            System.out.println("读取完成");
        }finally {
            lock.unlockRead(stamp);
        }

    }


    public void tryOptimisticRead(){
        long stamp = lock.tryOptimisticRead();
        for (int i = 0; i < 3; i++) {
            try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {throw new RuntimeException(e);}
            System.out.println(Thread.currentThread().getName() + "正在乐观读取，第" + i + "秒，当前number=" + number + "，修改标记=" + lock.validate(stamp));
        }
        //影响性能，产生bug
        //Thread.interrupted();
        if (!lock.validate(stamp)){
            long l = lock.readLock();
            try {
                System.out.println(Thread.currentThread().getName() + "读取过程中有修改，乐观变悲观");
                System.out.println(Thread.currentThread().getName() + "number=" + number);
            }finally {
                lock.unlockRead(l);
            }
        }
        try {
            System.out.println(Thread.currentThread().getName() + "读取完成");
        }finally {
            lock.tryUnlockRead();
        }

    }
    /**
     * 传统读写
     */
    @Test
    public void test01(){
        new Thread(() -> {
            read();
        },"readThread").start();

        try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {throw new RuntimeException(e);}

        new Thread(() -> {
            write();
        },"writeThread").start();

        try {TimeUnit.SECONDS.sleep(10);} catch (InterruptedException e) {throw new RuntimeException(e);}

        System.out.println(Thread.currentThread().getName() + ",number=" + number);
    }


    /**
     * 乐观读写
     */
    @Test
    public void test02(){
        new Thread(() -> {
            tryOptimisticRead();
        },"OptimisticReadThread").start();

        try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}

        new Thread(() -> {
            write();
        },"writeThread").start();

        try {TimeUnit.SECONDS.sleep(10);} catch (InterruptedException e) {throw new RuntimeException(e);}

        System.out.println(Thread.currentThread().getName() + ",number=" + number);
    }
}
