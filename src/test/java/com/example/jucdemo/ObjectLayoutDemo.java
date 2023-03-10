package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 对象布局测试demo
 */
@SpringBootTest
public class ObjectLayoutDemo {
    /**
     *  测试jol-core  java object layout
     *
     */
    @Test
    public void test01(){
        //System.out.println(VM.current().details());
        Object o = new Object();
        Customer c = new Customer();
        //System.out.println(ClassLayout.parseInstance(o).toPrintable());
        System.out.println(ClassLayout.parseInstance(c).toPrintable());

    }

    /**
     *
     */
    @Test
    public void test02(){

    }
}

/**
 * 1.只有对象头
 *   0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 *   4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *   8     4        (object header)                           a0 48 34 01 (10100000 01001000 00110100 00000001) (20203680)
 *   12    4        (loss due to the next object alignment)
 * 2. 定义变量，满足对象填充
 *  OFFSET  SIZE      TYPE DESCRIPTION                               VALUE
 *       0     4           (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 *       4     4           (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *       8     4           (object header)                           78 59 34 01 (01111000 01011001 00110100 00000001) (20207992)
 *      12     4       int Customer.id                               0
 *      16     1   boolean Customer.flag                             false
 *      17     7           (loss due to the next object alignment)
 * Instance size: 24 bytes
 * Space losses: 0 bytes internal + 7 bytes external = 7 bytes total
 *
 */
class Customer{
    int id;
    boolean flag;

}
