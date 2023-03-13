package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 锁消除与锁优化
 */
@SpringBootTest
public class LockClearUpDemo {
    //公用锁
    static Object objectLock = new Object();
    /**
     * 锁消除
     */
    @Test
    public void test01(){
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                m1();
            },String.valueOf(i)).start();
        }
    }

    /**
     * 锁粗化
     */
    @Test
    public void test02(){
        new Thread(() -> {
            synchronized (objectLock){
                System.out.println("111");
            }
            synchronized (objectLock){
                System.out.println("222");
            }
            synchronized (objectLock){
                System.out.println("333");
            }
            synchronized (objectLock){
                System.out.println("444");
            }

            //相当于
            synchronized (objectLock){
                System.out.println("111");
                System.out.println("222");
                System.out.println("333");
                System.out.println("444");
            }
        },"t1").start();
    }

    public void m1(){
        //锁消除问题，JIT编译器会无视o锁，每次new出来的对象锁，相当于不正常的
        Object o = new Object();
        synchronized (o){
            System.out.println("hello LockClearUpDemo------" + o.hashCode() + "\t" + objectLock.hashCode());
        }
    }
}
