package com.insight.base.user.manage;

import com.insight.base.user.common.dto.UserDto;
import com.insight.util.pojo.Reply;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户管理服务接口
 */
public interface Service {

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
     * 新增用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    Reply newUser(UserDto dto);

    /**
     * 编辑用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    Reply editUser(UserDto dto);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return Reply
     */
    Reply deleteUser(String id);

    /**
     * 改变用户禁用/启用状态
     *
     * @param id     用户ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    Reply changeUserStatus(String id, boolean status);

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return Reply
     */
    Reply resetPassword(String id);

    /**
     * 邀请用户
     *
     * @param id 用户ID
     * @return Reply
     */
    Reply inviteUser(String id);
}
