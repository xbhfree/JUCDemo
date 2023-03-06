package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ThreadLocalDemo {

    /**
     * 5个销售卖房子，统计各自卖出多少
     */
    @Test
    public void test01(){
        House house = new House();
        for (int i = 0; i < 5; i++) {
            int size = new Random().nextInt(5) + 1;
            new Thread(() -> {
                try {
                    for (int j = 0; j < size; j++) {
                        house.saleValueByThreadLocal();
                    }
                    //Thread.currentThread().getName() 可以变相取i值
                    System.out.println(Thread.currentThread().getName() + "号销售卖出" + house.threadLocal.get() + "套房子");
                } finally {
                    //防止内存泄露
                    house.threadLocal.remove();
                }
            },String.valueOf(i)).start();
        }
    }


    /**
     * 验证内存泄露
     */
    @Test
    public void test02(){
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        House house = new House();
        try {
            for (int i = 0; i < 10; i++) {
                executorService.submit(()->{
                    try {
                        int startInt = house.threadLocal.get();
                        house.saleValueByThreadLocal();
                        int endInt = house.threadLocal.get();
                        System.out.println(Thread.currentThread().getName() + "startInt = " + startInt + " ,endInt=" + endInt);
                    } finally {
                        house.threadLocal.remove();
                    }
                });
            }
        } finally {
            executorService.shutdown();
        }
    }


    /**
     * 强引用示例
     */
    @Test
    public void test03(){
        MyObject myObject = new MyObject();
        System.out.println("gc before MyObject = " + myObject);
        myObject = null;
        System.gc();//人工开启gc
        System.out.println("gc after MyObject = " + myObject);
    }


    /**
     * 软引用 设置虚拟机参数-Xms10m -Xmx10m
     * 运行结果
     * 内存够用，myObjectSoftReference=com.example.jucdemo.MyObject@df921b1
     * invoke finalize method
     * 内存不够用，myObjectSoftReference=null
     * Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
     */
    @Test
    public void test04(){
        SoftReference<MyObject> myObjectSoftReference = new SoftReference<>(new MyObject());
        System.gc();
        try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {throw new RuntimeException(e);}
        System.out.println("内存够用，myObjectSoftReference=" + myObjectSoftReference.get());
        try {
            byte[] bytes = new byte[20 * 1024 * 1024];//设置20m数据，超出内存
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("内存不够用，myObjectSoftReference=" + myObjectSoftReference.get());
        }
    }


    /**
     * 弱引用
     */
    @Test
    public void test05(){
        WeakReference<MyObject> myObjectWeakReference = new WeakReference<>(new MyObject());
        System.out.println("内存够用 gc before，myObjectWeakReference=" + myObjectWeakReference.get());
        System.gc();
        try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {throw new RuntimeException(e);}
        System.out.println("内存够用 gc after，myObjectWeakReference=" + myObjectWeakReference.get());
    }

}

class House{
    //两种初始化
    /*ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };*/
    ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);

    public void saleValueByThreadLocal(){
        threadLocal.set(1 + threadLocal.get());
    }

}

class MyObject{
    @Override
    protected void finalize() throws Throwable {
        System.out.println("invoke finalize method");
        super.finalize();
    }
}
