package com.example.service;

import com.example.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhou Jian created on 2022/3/3 21:50
 */
@Service
public class UserService {
    private final List<User> users = new ArrayList<>(200);

    @PostConstruct
    public void initData() {
        int count = 200;
        for (int i = 1; i <= count; i++) {
            User user = new User((long) i, "张三" + i + "号", "1333333333_" + i);
            users.add(user);
        }
    }

    public List<User> getUsers(List<Long> ids) {
        return users.stream().filter(user -> ids.contains(user.getId())).collect(Collectors.toList());
    }

    public User getUserById(Long id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }
}
