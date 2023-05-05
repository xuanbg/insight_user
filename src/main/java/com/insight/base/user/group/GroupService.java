package com.insight.base.user.group;

import com.insight.base.user.common.dto.GroupDto;
import com.insight.base.user.common.dto.UserVo;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户组管理服务接口
 */
public interface GroupService {

    /**
     * 查询用户组列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getGroups(Search search);

    /**
     * 获取用户组详情
     *
     * @param id 用户组ID
     * @return Reply
     */
    GroupDto getGroup(Long id);

    /**
     * 新增用户组
     *
     * @param info 用户关键信息
     * @param dto  用户组DTO
     * @return Reply
     */
    Long newGroup(LoginInfo info, GroupDto dto);

    /**
     * 编辑用户组
     *
     * @param info 用户关键信息
     * @param dto  用户组DTO
     */
    void editGroup(LoginInfo info, GroupDto dto);

    /**
     * 删除用户组
     *
     * @param info 用户关键信息
     * @param id   用户组ID
     */
    void deleteGroup(LoginInfo info, Long id);

    /**
     * 查询用户组成员
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getMembers(Search search);

    /**
     * 查询用户组可用用户列表
     *
     * @param id 用户组ID
     * @return Reply
     */
    List<UserVo> getOthers(Long id);

    /**
     * 添加用户组成员
     *
     * @param info    用户关键信息
     * @param id      用户组ID
     * @param userIds 用户ID集合
     */
    void addMembers(LoginInfo info, Long id, List<Long> userIds);

    /**
     * 移除用户组成员
     *
     * @param info    用户关键信息
     * @param id      用户组ID
     * @param userIds 用户ID集合
     */
    void removeMembers(LoginInfo info, Long id, List<Long> userIds);

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getGroupLogs(Search search);

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    Reply getGroupLog(Long id);
}
