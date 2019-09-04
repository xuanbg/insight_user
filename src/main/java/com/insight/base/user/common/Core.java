package com.insight.base.user.common;

import com.insight.base.user.common.mapper.UserMapper;
import com.insight.util.Generator;
import com.insight.util.pojo.User;

import java.util.Date;

/**
 * @author 宣炳刚
 * @date 2019-09-04
 * @remark
 */
public class Core {
private final UserMapper mapper;

    /**
     * 构造方法
     * @param mapper UserMapper
     */
    public Core(UserMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 新增用户
     *
     * @param user 用户DTO
     * @return 受影响行数
     */
    public boolean addUser(User user) {
        user.setId(Generator.uuid());
        user.setCreatedTime(new Date());
        String creatorId = user.getCreatorId();
        if (creatorId == null || creatorId.isEmpty()) {
            user.setCreator(user.getName());
            user.setCreatorId(user.getId());
        }

        return mapper.addUser(user) > 0;
    }
}
