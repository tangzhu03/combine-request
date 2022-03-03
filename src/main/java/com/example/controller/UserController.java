package com.example.controller;

import com.example.entity.User;
import com.example.entity.UserRequest;
import com.example.processor.UserQueryProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Zhou Jian created on 2022/3/3 21:45
 */
@RestController
@Slf4j
public class UserController {

    private final UserQueryProcessor processor;

    public UserController(UserQueryProcessor processor) {
        this.processor = processor;
    }

    @GetMapping("/user/byId")
    public ResponseEntity<User> getUserById(@RequestParam("id") String id) {
        log.info("userId:{}", id);
        UserRequest request = new UserRequest(id, new CompletableFuture<>());
        boolean notFull = false;
        try {
            notFull = processor.getQueue().add(request);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.TOO_MANY_REQUESTS);
        }
        if (notFull) {
            User user = null;
            try {
                user = request.getFuture().get(1000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.info(e.getMessage(), e);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.TOO_MANY_REQUESTS);
    }
}
