package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

/**
 * @author Zhou Jian created on 2022/3/3 21:51
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequest {
    private String id;
    private CompletableFuture<User> future;
}
