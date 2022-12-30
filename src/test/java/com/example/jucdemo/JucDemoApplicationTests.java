package com.example.jucdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JucDemoApplicationTests {

    @Test
    void test01() {
        Thread thread = new Thread(() -> {

        }, "time01");
        thread.start();
    }

}
