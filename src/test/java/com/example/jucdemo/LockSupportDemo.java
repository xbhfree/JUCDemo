package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程等待，唤醒测试demo
 */
@SpringBootTest
public class LockSupportDemo {

    /**
     * 测试Object的wait和notify方法
     */
    @Test
    public void test01() throws InterruptedException {
        Object objectLock = new Object();
        new Thread(() -> {
            //必须先wait再notify，顺序不对则无法唤醒
            /*try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            synchronized (objectLock){
               System.out.println("come in---");
               try {
                   objectLock.wait();
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
               System.out.println("object已经被唤醒");
           }
        },"t1").start();

        TimeUnit.MILLISECONDS.sleep(200);
        new Thread(() -> {
            synchronized (objectLock){
                objectLock.notify();
                System.out.println("发起唤醒object操作");
            }
        }, "t2").start();
    }

    /**
     * 测试juc的await和signal方法
     */
    @Test
    public void test02(){
        //Reentrant可重入的
        Lock lock = new ReentrantLock();
        //condition 状况，状态；条件，环境；疾病；条款；<旧>社会地位
        Condition condition = lock.newCondition();
        new Thread(() -> {
            //必须先睡再唤醒
            /*try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            lock.lock();
            try {
                System.out.println("come in---");
                condition.await();
                System.out.println("condition已经被唤醒");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                lock.unlock();
            }
        },"t1").start();
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            lock.lock();
            try {
                condition.signal();
                System.out.println("发出condition唤醒命令");
            }finally {
                lock.unlock();
            }

        },"t2").start();
    }

    /**
     * 测试LockSupport对于线程唤醒等待的需求
     * 1.不需锁
     * 2.可先发通行证，再校验，但要成对
     * 3.许可证只有一个，不可多对多
     */
    @Test
    public void test03() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("come in---");
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + "已经被唤醒");
        }, "t1");
        t1.start();
        new Thread(() -> {
            LockSupport.unpark(t1);
            System.out.println("向t1线程发送唤醒指令");
        },"t2").start();
        TimeUnit.SECONDS.sleep(5);
    }
}
