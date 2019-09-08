package com.insight.base.user.service;

import com.insight.base.user.common.dto.PasswordDto;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.User;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户服务接口
 */
public interface UserService {

    /**
     * 查询用户列表
     *
     * @param key  查询关键词
     * @param page 分页页码
     * @param size 每页记录数
     * @return Reply
     */
    Reply getUsers(String key, int page, int size);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    Reply getUser(String id);

    /**
     * 注册用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    Reply register(User dto);

    /**
     * 更新用户信息
     *
     * @param dto 用户DTO
     * @return Reply
     */
    Reply updateUser(User dto);

    /**
     * 修改密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    Reply changePassword(PasswordDto dto);

    /**
     * 重置密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    Reply resetPassword(PasswordDto dto);

    /**
     * 设置支付密码
     *
     * @param dto 用户DTO
     * @return Reply
     */
    Reply setPayPassword(User dto);

    /**
     * 验证支付密码(供服务调用)
     *
     * @param id  用户ID
     * @param key 支付密码(MD5)
     * @return Reply
     */
    Reply verifyPayPw(String id, String key);
}
