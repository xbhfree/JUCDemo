package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class VolatileDemo {

    static volatile boolean flag = true;
    public static void main(String[] args) {

    }

    /**
     * 验证volatile的可见性
     */
    @Test
    public void test01(){
        new Thread(() -> {
            System.out.println("开始循环");
            while (flag){

            }
            System.out.println("循环结束,flag = " + flag);
        },"t1").start();
        try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}
        System.out.println(Thread.currentThread().getName()+ "设置flag为false");
        flag = false;

    }
}
