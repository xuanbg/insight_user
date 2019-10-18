package com.insight.base.user.common;

import com.insight.base.user.common.mapper.UserMapper;
import com.insight.util.Generator;
import com.insight.util.pojo.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author 宣炳刚
 * @date 2019-09-04
 * @remark 用户管理核心类
 */
@Component
public class Core {
    private final UserMapper mapper;

    /**
     * 构造方法
     *
     * @param mapper UserMapper
     */
    public Core(UserMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 新增用户
     *
     * @param user 用户DTO
     */
    public void addUser(User user) {
        String userId = user.getId();
        if (userId == null || userId.isEmpty()) {
            user.setId(Generator.uuid());
        }

        user.setCreatedTime(LocalDateTime.now());
        String creatorId = user.getCreatorId();
        if (creatorId == null || creatorId.isEmpty()) {
            user.setCreator(user.getName());
            user.setCreatorId(user.getId());
        }

        mapper.addUser(user);
    }
}
