package com.example.jucdemo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComletableFutureMallDemo {
    static List<NetMall> mallList = Arrays.asList(
            new NetMall("jd"),
            new NetMall("pdd"),
            new NetMall("taobao"));
    static List<String> getPrice(List<NetMall> list, String productName){
        return list
                .stream()
                .map(netMall ->
                        String.format(productName + " in %s price is %.2f", netMall.getNetMallName(), netMall.calcPrice(productName)))
                .collect(Collectors.toList());
    }
    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();
        List<String> mysql = getPrice(mallList, "mysql");
        mysql.forEach(System.out::println);
        Long endTime = System.currentTimeMillis();
        Long time = endTime - startTime;
        System.out.println("time = " + time);
    }
}
@AllArgsConstructor
class NetMall{

    @Getter
    private String netMallName;

    public double calcPrice(String productName){
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
    }
}