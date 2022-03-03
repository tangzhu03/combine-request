package com.example.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @author Zhou Jian created on 2022/3/3 22:37
 */

class UserControllerTest {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(105);
        OkHttpClient client = new OkHttpClient();
        for (int i = 1; i <= 105; i++) {

            Request request = new Request.Builder().url("http://localhost:8080/user/byId?id=" + i).get().build();
            new Thread(() -> {
                try {
                    countDownLatch.await();
                    Response response = client.newCall(request).execute();
                    String string = Objects.requireNonNull(response.body()).string();
                    System.out.println("string = " + response.code() + " :: " + string);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            countDownLatch.countDown();
        }
    }
}