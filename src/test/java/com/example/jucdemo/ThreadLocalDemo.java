package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

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
                for (int j = 0; j < size; j++) {
                    house.saleValueByThreadLocal();
                }
                //Thread.currentThread().getName() 可以变相取i值
                System.out.println(Thread.currentThread().getName() + "号销售卖出" + house.threadLocal.get() + "套房子");
            },String.valueOf(i)).start();
        }
    }


    /**
     *
     */
    @Test
    public void test02(){

    }


    /**
     *
     */
    @Test
    public void test03(){

    }


    /**
     *
     */
    @Test
    public void test04(){

    }


    /**
     *
     */
    @Test
    public void test05(){

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
