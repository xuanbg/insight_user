package com.insight.base.user.manage;

import com.insight.util.pojo.Reply;
import com.insight.util.pojo.User;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户管理服务
 */
@org.springframework.stereotype.Service
public class ManageServiceImpl implements ManageService {
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
     * 新增用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @Override
    public Reply newUser(User dto) {
        return null;
    }

    /**
     * 编辑用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @Override
    public Reply editUser(User dto) {
        return null;
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return Reply
     */
    @Override
    public Reply deleteUser(String id) {
        return null;
    }

    /**
     * 改变用户禁用/启用状态
     *
     * @param id     用户ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @Override
    public Reply changeUserStatus(String id, boolean status) {
        return null;
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return Reply
     */
    @Override
    public Reply resetPassword(String id) {
        return null;
    }

    /**
     * 邀请用户
     *
     * @param id 用户ID
     * @return Reply
     */
    @Override
    public Reply inviteUser(String id) {
        return null;
    }
}
