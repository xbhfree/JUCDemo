package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest
public class ReEnterLockDemo {
    /**
     * 测试synchronized同步代码块的三层重入调用
     */
    @Test
    public void test01(){
       final Object o = new Object();
       new Thread(() -> {
           synchronized (o){
               System.out.println(Thread.currentThread().getName() + "外层调用");
               synchronized (o){
                   System.out.println(Thread.currentThread().getName() + "中层调用");
                   synchronized (o){
                       System.out.println(Thread.currentThread().getName() + "内层调用");
                   }
               }
           }
       }, "t1").start();
    }


    /**
     * 测试synchronized同步方法的可重入性
     */
    @Test
    public void test02(){
        SyncMethod method = new SyncMethod();
        new Thread(() -> {
            method.m1();
        },"t2").start();
    }

    static ReentrantLock lock = new ReentrantLock();
    /**
     * ReentrantLock显示调用可重入,必须加锁，减锁一一对应
     */
    @Test
    public void test03(){
       new Thread(() -> {
           lock.lock();
           try {
               System.out.println(Thread.currentThread().getName() + " 外层");
               lock.lock();
               try {
                   System.out.println(Thread.currentThread().getName() + " 内层");
               }finally {
                   lock.unlock();
               }
           }finally {
               lock.unlock();
           }
       },"t1").start();
    }

    /**
     * ReentrantLock显示调用可重入,必须加锁，减锁一一对应
     */
    @Test
    public void test04(){
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 外层");
                //少锁一个会有java.lang.IllegalMonitorStateException
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " 内层");
                }finally {
                    //lock.unlock();
                }
            }finally {
                lock.unlock();
            }
        },"t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 外层");
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " 内层");
                }finally {
                    lock.unlock();
                }
            }finally {
                lock.unlock();
            }
        },"t2").start();
    }
}
class SyncMethod{
    public synchronized void m1(){
        System.out.println("m1 come in");
        m2();
        System.out.println("m1 out");
    }

    public synchronized void m2(){
        System.out.println("m2 come in");
        m3();
        System.out.println("m2 out");
    }

    public synchronized void m3(){
        System.out.println("m3 come in");
    }
}