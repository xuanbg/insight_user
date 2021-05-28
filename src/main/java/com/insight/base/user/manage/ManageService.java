package com.insight.base.user.manage;

import com.insight.base.user.common.dto.PasswordDto;
import com.insight.base.user.common.dto.UserDto;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.Reply;
import com.insight.utils.pojo.SearchDto;
import com.insight.utils.pojo.User;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户管理服务接口
 */
public interface ManageService {

    /**
     * 查询用户列表
     *
     * @param tenantId 租户ID
     * @param all      是否查询全部用户
     * @param search   查询实体类
     * @return Reply
     */
    Reply getUsers(Long tenantId, boolean all, SearchDto search);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    Reply getUser(Long id);

    /**
     * 获取用户功能授权
     *
     * @param id 用户ID
     * @return Reply
     */
    Reply getUserPermit(Long id);

    /**
     * 新增用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     * @return Reply
     */
    Reply newUser(LoginInfo info, User dto);

    /**
     * 编辑用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     * @return Reply
     */
    Reply editUser(LoginInfo info, UserDto dto);

    /**
     * 删除用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    Reply deleteUser(LoginInfo info, Long id);

    /**
     * 改变用户禁用/启用状态
     *
     * @param info   用户关键信息
     * @param id     用户ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    Reply changeUserStatus(LoginInfo info, Long id, boolean status);

    /**
     * 重置用户密码
     *
     * @param info 用户关键信息
     * @param dto  密码DTO
     * @return Reply
     */
    Reply resetPassword(LoginInfo info, PasswordDto dto);

    /**
     * 获取可邀请用户列表
     *
     * @param info    用户关键信息
     * @param keyword 查询关键词
     * @return Reply
     */
    Reply getInviteUsers(LoginInfo info, String keyword);

    /**
     * 邀请用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    Reply inviteUser(LoginInfo info, Long id);

    /**
     * 清退用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    Reply removeUser(LoginInfo info, Long id);

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getUserLogs(SearchDto search);

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    Reply getUserLog(Long id);
}
