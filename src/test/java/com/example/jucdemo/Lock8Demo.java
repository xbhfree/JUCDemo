package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class Lock8Demo {
    /**
     * 1.标准访问有ab两个线程，先打印邮件还是短信
     * 2.sendEmail加入暂停2s
     * 3.添加一个普通hello方法
     * 4.两部手机
     * 5.两个静态同步方法，1部手机
     * 6.两个静态同步方法，2部手机
     * 7.1个静态同步方法，1个普通同步方法，1部手机
     * 8.1个静态同步方法，1个普通同步方法，2部手机
     *
     *
     */

    /**
     * 标准打印
     * 结果：
     * 发送邮件
     * 发送短信
     */
    @Test
    public void test01(){
        Phone phone = new Phone();
        new Thread(() -> {
            phone.sendEmail();
        }, "a").start();
        //保证a先跑
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            phone.sendSMS();
        }, "b").start();
    }

    /**
     * 打印非锁方法，且发送邮件有2s时间暂停
     * 结果：
     * hello
     * 发送邮件
     */
    @Test
    public void test02(){
        Phone phone = new Phone();
        new Thread(() -> {
            phone.sendEmail();
        }, "a").start();
        //保证a先跑
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            phone.hello();
        }, "b").start();
    }


}

/**
 * 资源类
 */
class Phone{
    public synchronized void sendEmail(){
        //加入时间等待，调用同一对象依旧是按照顺序打印
        /*try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        System.out.println("发送邮件");
    }

    public synchronized void sendSMS(){
        System.out.println("发送短信");
    }

    public void hello(){
        System.out.println("hello");
    }
}


class StaticPhone{
    public static synchronized void sendEmail(){
        //加入时间等待，调用同一对象依旧是按照顺序打印
        /*try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        System.out.println("发送邮件");
    }

    public static synchronized void sendSMS(){
        System.out.println("发送短信");
    }

    public void hello(){
        System.out.println("hello");
    }
}

/**
 * 锁的三种方式
 */
class LockMethod{
    //第一种
    public synchronized void firstLockMethod(){

    }
    //第二种
    public static synchronized void secondLockMethod(){

    }
    //第三种
    public void thirdLockMethod(){
        synchronized (this){

        }
    }
}