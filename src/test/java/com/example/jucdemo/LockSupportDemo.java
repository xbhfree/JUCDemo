package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

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
}
