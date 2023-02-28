package com.example.jucdemo;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;

/**
 * 原子操作类测试demo
 */
@SpringBootTest
public class AtomicDemo {

    private static final int threadSize = 50;
    /**
     * 测试原子类的原子性，50个线程同时做加1操作
     */
    @Test
    public void test01(){
        MyNumber myNumber = new MyNumber();
        //countDownLatch保证线程全部执行完毕
        CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        for (int i = 0; i < threadSize; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        myNumber.addPlusPlus();
                    }
                }finally {
                    countDownLatch.countDown();
                }
            },String.valueOf(i)).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(myNumber.getAtomicInteger().get());
    }

    /**
     * 数组类型原子类
     */
    @Test
    public void test02(){
        //三种初始化方式
        //AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(5);
        //AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[5]);
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[]{1, 2, 3, 4, 5});
        for (int i = 0; i < atomicIntegerArray.length(); i++) {
            System.out.println(atomicIntegerArray.get(i));
        }

        System.out.println(atomicIntegerArray.getAndSet(0, 1222) + "\t" + atomicIntegerArray.get(0));
        try {TimeUnit.MILLISECONDS.sleep(20);} catch (InterruptedException e) {throw new RuntimeException(e);}
        System.out.println(atomicIntegerArray.getAndIncrement(0) + "\t" + atomicIntegerArray.get(0));
    }

    /**
     * 测试AtomicMarkableReference
     */
    @Test
    public void test03(){
        AtomicMarkableReference<Integer> reference = new AtomicMarkableReference<>(100, false);
        new Thread(() -> {
            boolean marked = reference.isMarked();
            System.out.println(Thread.currentThread().getName() + " mark初始状态" + marked);
            try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {throw new RuntimeException(e);}
            reference.compareAndSet(100, 2000, marked, !marked);
        },"t1").start();

        new Thread(() -> {
            boolean marked = reference.isMarked();
            System.out.println(Thread.currentThread().getName() + " mark初始状态" + marked);
            try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}
            boolean b = reference.compareAndSet(100, 3000, marked, !marked);
            System.out.println(Thread.currentThread().getName() + " CAS修改结果" + b);
            System.out.println(Thread.currentThread().getName() + "\t isMarked=" + reference.isMarked());
            System.out.println(Thread.currentThread().getName() + "\t getReference=" + reference.getReference());
        },"t2").start();

        try {TimeUnit.SECONDS.sleep(5);} catch (InterruptedException e) {throw new RuntimeException(e);}
    }


    /**
     * 测试对象的属性修改原子类案例1
     */
    @Test
    public void test04() throws InterruptedException {
        BankAccount bankAccount = new BankAccount();
        CountDownLatch countDownLatch = new CountDownLatch(threadSize);

        for (int i = 0; i < threadSize; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        //bankAccount.addPlusPlus();
                        bankAccount.atomicAddPlusPlus(bankAccount);
                    }
                } finally {
                    countDownLatch.countDown();
                }
            },"t1").start();
        }
        countDownLatch.await();
        System.out.println(bankAccount.getMoney());
    }


    /**
     * 测试对象的属性修改原子类案例2
     * AtomicReferenceFieldUpdater
     * 需求：多线程并发调用一个类的初始化，如果未被初始化，将执行初始化工作
     * 要求只能初始化一次，只有一个线程操作成功
     */
    @Test
    public void test05() throws InterruptedException {
        MyVar myVar = new MyVar();
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                    myVar.initMyVar(myVar);
            },String.valueOf(i)).start();
        }
        try {TimeUnit.SECONDS.sleep(5);} catch (InterruptedException e) {throw new RuntimeException(e);}
    }
}
@Data
class MyNumber{
    AtomicInteger atomicInteger = new AtomicInteger();

    /**
     * 模仿i++
     */
    public void addPlusPlus(){
        atomicInteger.getAndIncrement();
    }
}

@Data
class BankAccount{
    String bankName = "icbc";

    public volatile int money = 0;

    public void addPlusPlus(){
        money++;
    }

    AtomicIntegerFieldUpdater<BankAccount> fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(BankAccount.class, "money");

    public void atomicAddPlusPlus(BankAccount bankAccount){
        fieldUpdater.getAndIncrement(bankAccount);
    }
}


class MyVar{
    public volatile Boolean isInit = Boolean.FALSE;

    AtomicReferenceFieldUpdater<MyVar, Boolean> fieldUpdater = AtomicReferenceFieldUpdater.newUpdater(MyVar.class, Boolean.class, "isInit");

    public void initMyVar(MyVar myVar){
        if (fieldUpdater.compareAndSet(myVar, Boolean.FALSE, Boolean.TRUE)){
            System.out.println(Thread.currentThread().getName() + "\t" + "正在初始化,需要2s钟---");
            try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}
            System.out.println(Thread.currentThread().getName() + "初始化完成");
        }else{
            System.out.println(Thread.currentThread().getName() + "\t" + "有其他线程已经在初始化---");
        }
    }
}