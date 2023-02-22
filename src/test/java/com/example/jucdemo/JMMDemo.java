package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JMMDemo {

    @Test
    public void test01(){

    }
}

/**
 *  1. 次序规则： 先来后到，保证可见性
 *   2. 锁定规则： 时间上，针对锁，锁释放后才能再获取
 *   3. volatile变量规则： 时间上，保证可见性
 *   4. 传递规则 A->B->C 得出结果A先行于C
 */
class TestDemo{
    // ++value非原子性操作，无法保证锁定规则
    // 多个线程调用无法保证次序规则
    private int value = 0;

    public int getValue(){
        return value;
    }

    public void setValue(int value) {
        this.value = ++value;
    }
}
//保证happens-before
class TestDemoAmend{
    private volatile int value = 0;

    public int getValue(){
        return value; //volatile保证读取操作可见性
    }

    public synchronized void setValue(int value) {
        this.value = ++value; //synchronized保证复合操作原子性
    }
}
