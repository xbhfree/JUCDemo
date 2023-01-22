package com.example.jucdemo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    /**
     * 异步多线程比价
     * @param list
     * @param productName
     * @return
     */
    static List<String> getPriceCompletableFuture(List<NetMall> list, String productName){
        return list
                .stream()
                .map(netMall -> CompletableFuture.supplyAsync(() ->
                        String.format(productName + " in %s price is %.2f", netMall.getNetMallName(), netMall.calcPrice(productName))))
                .collect(Collectors.toList())
                .stream()
                .map(s -> s.join())
                .collect(Collectors.toList());
    }
    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();
        List<String> mysql = getPrice(mallList, "mysql");
        mysql.forEach(System.out::println);
        Long endTime = System.currentTimeMillis();
        System.out.println("time = " + (endTime - startTime));
        System.out.println("-----------------------------------------");
        Long startTime2 = System.currentTimeMillis();
        List<String> mysql2 = getPriceCompletableFuture(mallList, "mysql");
        mysql2.forEach(System.out::println);
        Long endTime2 = System.currentTimeMillis();
        System.out.println("time = " + (endTime2 - startTime2));

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