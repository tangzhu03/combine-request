package com.example.processor;

import com.example.entity.User;
import com.example.entity.UserRequest;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Zhou Jian created on 2022/3/3 21:50
 */
@Component
@Slf4j
public class UserQueryProcessor {
    private final LinkedBlockingQueue<UserRequest> queue = new LinkedBlockingQueue<>(100);
    private final UserService userService;
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("查询user线程%s").daemon(true).build());

    public UserQueryProcessor(UserService userService) {
        this.userService = userService;
    }

    public LinkedBlockingQueue<UserRequest> getQueue() {
        return queue;
    }

    @PostConstruct
    public void process() {
        executorService.scheduleAtFixedRate(() -> {
            int size = queue.size();
            List<Long> ids = new ArrayList<>(size);
            Map<Long, UserRequest> requestMap = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                UserRequest request = queue.poll();
                if (request != null) {
                    long id = Long.parseLong(request.getId());
                    ids.add(id);
                    requestMap.put(id, request);
                }
            }
            List<User> users = userService.getUsers(ids);
            Map<Long, User> collect = users.stream().collect(Collectors.toMap(User::getId, v -> v));
            for (Map.Entry<Long, UserRequest> entry : requestMap.entrySet()) {
                entry.getValue().getFuture().complete(collect.getOrDefault(entry.getKey(), null));
            }
            if (size > 0) {

                log.info("已处理{}个请求", size);
            }
        }, 1000L, 100L, TimeUnit.MILLISECONDS);
    }
}
