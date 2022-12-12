package com.insight.base.user.manage;

import com.insight.base.user.common.dto.FuncPermitDto;
import com.insight.base.user.common.dto.UserDto;
import com.insight.base.user.common.dto.UserVo;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户管理服务接口
 */
public interface ManageService {

    /**
     * 查询用户列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getUsers(Search search);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    UserVo getUser(Long id);

    /**
     * 获取用户功能授权
     *
     * @param id 用户ID
     * @return Reply
     */
    List<FuncPermitDto> getUserPermit(Long id);

    /**
     * 新增用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     * @return Reply
     */
    Long newUser(LoginInfo info, UserDto dto);

    /**
     * 编辑用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     */
    void editUser(LoginInfo info, UserDto dto);

    /**
     * 删除用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    void deleteUser(LoginInfo info, Long id);

    /**
     * 改变用户禁用/启用状态
     *
     * @param info   用户关键信息
     * @param id     用户ID
     * @param status 禁用/启用状态
     */
    void changeUserStatus(LoginInfo info, Long id, boolean status);

    /**
     * 重置用户密码
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    void resetPassword(LoginInfo info, Long id);

    /**
     * 获取可邀请用户列表
     *
     * @param search 查询关键词
     * @return Reply
     */
    Reply getInviteUsers(Search search);

    /**
     * 邀请用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    void inviteUser(LoginInfo info, Long id);

    /**
     * 清退用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    void removeUser(LoginInfo info, Long id);

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getUserLogs(Search search);

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    Reply getUserLog(Long id);
}
