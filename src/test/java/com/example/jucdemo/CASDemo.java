package com.example.jucdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

@SpringBootTest
public class CASDemo {

    /**
     * 原子引用测试类
     */
    @Test
    public void test01(){
        AtomicReference<User> reference = new AtomicReference<>();
        User alisa = new User("alisa", 23);
        User bob = new User("bob", 22);
        reference.set(alisa);
        System.out.println(reference.compareAndSet(alisa, bob) + "\t" + reference.get().toString());
        System.out.println(reference.compareAndSet(alisa, bob) + "\t" + reference.get().toString());
    }


    /**
     * ABA问题单线程
     */
    @Test
    public void test02(){
        User alisa = new User("alisa", 23);
        User bob = new User("bob", 22);
        AtomicStampedReference<User> stampedReference = new AtomicStampedReference<>(alisa, 1);
        System.out.println(stampedReference.getReference() + "\t" + stampedReference.getStamp());
        boolean flag = stampedReference.compareAndSet(alisa, bob, stampedReference.getStamp(), stampedReference.getStamp());
        System.out.println(flag + "\t" + stampedReference.getReference() + "\t" + stampedReference.getStamp());
        flag = stampedReference.compareAndSet(bob, alisa, stampedReference.getStamp(), stampedReference.getStamp());
        System.out.println(flag + "\t" + stampedReference.getReference() + "\t" + stampedReference.getStamp());
    }

    /**
     * ABA多线程错误示例
     */
    @Test
    public void test03(){
        AtomicInteger num  = new AtomicInteger(100);

        new Thread(() -> {
            num.compareAndSet(100, 101);
            try {TimeUnit.MILLISECONDS.sleep(50);} catch (InterruptedException e) {throw new RuntimeException(e);}
            num.compareAndSet(101,100);
        },"t1").start();

        new Thread(() -> {
            try {TimeUnit.MILLISECONDS.sleep(200);} catch (InterruptedException e) {throw new RuntimeException(e);}
            System.out.println(num.compareAndSet(100, 2000) + "\t" + num.get());
        },"t2").start();

        try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}

    }

    /**
     * ABA多线程修正示例
     */
    @Test
    public void test04(){
        AtomicStampedReference<Integer> stampedReference = new AtomicStampedReference<>(100, 1);

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t" + "第1次获得stamp=" + stampedReference.getStamp());
            //暂停500ms，保证t2和t1拿到的版本号相同
            try {TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException e) {throw new RuntimeException(e);}
            stampedReference.compareAndSet(100, 101, stampedReference.getStamp(), stampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t" + "第2次获得stamp=" + stampedReference.getStamp());
            stampedReference.compareAndSet(101, 100, stampedReference.getStamp(), stampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t" + "第3次获得stamp=" + stampedReference.getStamp());
        },"t1").start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t" + "第1次获得stamp=" + stampedReference.getStamp());
            //暂停2s,保证aba问题已经产生
            try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}
            boolean b = stampedReference.compareAndSet(101, 100, stampedReference.getStamp(), stampedReference.getStamp() + 1);
            System.out.println("更新操作=" + b + "\t" + Thread.currentThread().getName() + "\t" + "第2次获得stamp=" + stampedReference.getStamp() + "\t" + "结果为" + stampedReference.getReference());
        },"t2").start();

        try {TimeUnit.SECONDS.sleep(5);} catch (InterruptedException e) {throw new RuntimeException(e);}

    }

}

@Data
@AllArgsConstructor
class User{
    String name;
    int age;
}
