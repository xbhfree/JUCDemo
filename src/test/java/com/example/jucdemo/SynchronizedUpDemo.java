package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.boot.test.context.SpringBootTest;

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
     * 测试轻量锁
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
}
