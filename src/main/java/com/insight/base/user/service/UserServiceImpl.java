package com.insight.base.user.service;

import com.insight.base.user.common.dto.PasswordDto;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.User;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户服务
 */
@org.springframework.stereotype.Service
public class UserServiceImpl implements UserService {
    /**
     * 查询用户列表
     *
     * @param key  查询关键词
     * @param page 分页页码
     * @param size 每页记录数
     * @return Reply
     */
    @Override
    public Reply getUsers(String key, int page, int size) {
        return null;
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    @Override
    public Reply getUser(String id) {
        return null;
    }

    /**
     * 注册用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @Override
    public Reply register(User dto) {
        return null;
    }

    /**
     * 更新用户信息
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @Override
    public Reply updateUser(User dto) {
        return null;
    }

    /**
     * 修改密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    @Override
    public Reply changePassword(PasswordDto dto) {
        return null;
    }

    /**
     * 重置密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    @Override
    public Reply resetPassword(PasswordDto dto) {
        return null;
    }

    /**
     * 设置支付密码
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @Override
    public Reply setPayPassword(User dto) {
        return null;
    }

    /**
     * 验证支付密码(供服务调用)
     *
     * @param id  用户ID
     * @param key 支付密码(MD5)
     * @return Reply
     */
    @Override
    public Reply verifyPayPw(String id, String key) {
        return null;
    }
}
