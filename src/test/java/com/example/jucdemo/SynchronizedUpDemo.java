package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * 偏向锁升级demo
 * 001 无状态
 * 101 偏向锁
 *  00 轻量锁
 *  10 重量锁
 */
@SpringBootTest
public class SynchronizedUpDemo {

    /**
     * 测试偏向锁
     * jdk15已经弃用偏向锁相关维护 jep使用_JEP 374：禁用和弃用偏向锁定
     * 需要手动加jvm参数-XX:BiasedLockingStartupDelay=0 取消偏向锁开启延迟
     */
    @Test
    public void test01(){
        Object o = new Object();
        synchronized (o){
            System.out.println(ClassLayout.parseInstance(o).toPrintable());
        }
    }


    /**
     * 测试偏向锁
     * jdk15已经弃用偏向锁相关维护 jep使用_JEP 374：禁用和弃用偏向锁定
     * 暂停5s
     */
    @Test
    public void test02(){
        Object o = new Object();
        try {TimeUnit.SECONDS.sleep(5);} catch (InterruptedException e) {throw new RuntimeException(e);}
        synchronized (o){
            System.out.println(ClassLayout.parseInstance(o).toPrintable());
        }
    }

    /**
     * 轻量锁 00
     */
    @Test
    public void test03(){
        Object o = new Object();
        new Thread(() -> {
            synchronized (o){
                System.out.println(ClassLayout.parseInstance(o).toPrintable());
            }
        },"t1").start();
        try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {throw new RuntimeException(e);}
    }


    /**
     * 轻量锁 10
     */
    @Test
    public void test04(){
        Object o = new Object();
        new Thread(() -> {
            synchronized (o){
                System.out.println(ClassLayout.parseInstance(o).toPrintable());
            }
        },"t1").start();

        new Thread(() -> {
            synchronized (o){
                System.out.println(ClassLayout.parseInstance(o).toPrintable());
            }
        },"t2").start();
        try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {throw new RuntimeException(e);}
    }


    /**
     * 偏向锁之后调用hashCode，升级为轻量锁
     */
    @Test
    public void test05(){
        try {TimeUnit.SECONDS.sleep(5);} catch (InterruptedException e) {throw new RuntimeException(e);}
        Object o = new Object();
        System.out.println("本应是偏向锁");
        System.out.println(ClassLayout.parseInstance(o).toPrintable());
        o.hashCode();
        synchronized (o){
            System.out.println("本应是偏向锁，经过hashCode运算，升级为轻量锁");
            System.out.println(ClassLayout.parseInstance(o).toPrintable());
        }
        try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {throw new RuntimeException(e);}
    }


    /**
     * 偏向锁过程中调用hashCode，直接升级为重量级锁
     */
    @Test
    public void test06(){
        try {TimeUnit.SECONDS.sleep(5);} catch (InterruptedException e) {throw new RuntimeException(e);}
        Object o = new Object();
        synchronized (o){
            o.hashCode();
            System.out.println("偏向锁过程中调用hashCode，立马撤销偏向锁模式，直接升级为重量级锁");
            System.out.println(ClassLayout.parseInstance(o).toPrintable());
        }
        try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {throw new RuntimeException(e);}
    }
}
