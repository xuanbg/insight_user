package com.insight.base.user.group;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.base.user.common.Core;
import com.insight.base.user.common.dto.GroupDto;
import com.insight.base.user.common.dto.GroupListDto;
import com.insight.base.user.common.dto.MemberListDto;
import com.insight.base.user.common.mapper.GroupMapper;
import com.insight.util.Generator;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.Log;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.OperateType;
import com.insight.util.pojo.Reply;

import java.time.LocalDateTime;
import java.util.List;

import static com.insight.util.Generator.uuid;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户组管理服务
 */
@org.springframework.stereotype.Service
public class GroupServiceImpl implements GroupService {
    private final GroupMapper mapper;
    private final Core core;

    /**
     * 构造方法
     *
     * @param mapper GroupMapper
     * @param core   Core
     */
    public GroupServiceImpl(GroupMapper mapper, Core core) {
        this.mapper = mapper;
        this.core = core;
    }

    /**
     * 查询用户组列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getGroups(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<GroupListDto> groups = mapper.getGroups(tenantId, keyword);
        PageInfo<GroupListDto> pageInfo = new PageInfo<>(groups);

        return ReplyHelper.success(groups, pageInfo.getTotal());
    }

    /**
     * 获取用户组详情
     *
     * @param id 用户组ID
     * @return Reply
     */
    @Override
    public Reply getGroup(String id) {
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(group);
    }

    /**
     * 新增用户组
     *
     * @param info 用户关键信息
     * @param dto  用户组DTO
     * @return Reply
     */
    @Override
    public Reply newGroup(LoginInfo info, GroupDto dto) {
        String id = uuid();
        String tenantId = info.getTenantId();
        dto.setId(id);
        dto.setTenantId(tenantId);
        dto.setCode(newGroupCode(tenantId));
        dto.setBuiltin(false);
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addGroup(dto);
        core.writeLog(info, OperateType.INSERT, "用户组管理", id, dto);

        return ReplyHelper.created(id);
    }

    /**
     * 编辑用户组
     *
     * @param info 用户关键信息
     * @param dto  用户组DTO
     * @return Reply
     */
    @Override
    public Reply editGroup(LoginInfo info, GroupDto dto) {
        String id = dto.getId();
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.updateGroup(dto);
        core.writeLog(info, OperateType.UPDATE, "用户组管理", id, dto);

        return ReplyHelper.success();
    }

    /**
     * 删除用户组
     *
     * @param info 用户关键信息
     * @param id   用户组ID
     * @return Reply
     */
    @Override
    public Reply deleteGroup(LoginInfo info, String id) {
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        mapper.deleteGroup(id);
        core.writeLog(info, OperateType.DELETE, "用户组管理", id, group);

        return ReplyHelper.success();
    }

    /**
     * 查询用户组成员
     *
     * @param id      用户组ID
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @Override
    public Reply getMembers(String id, String keyword, int page, int size) {
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        PageHelper.startPage(page, size);
        List<MemberListDto> members = mapper.getMembers(id, keyword);
        PageInfo<MemberListDto> pageInfo = new PageInfo<>(members);

        return ReplyHelper.success(members, pageInfo.getTotal());
    }

    /**
     * 添加用户组成员
     *
     * @param info    用户关键信息
     * @param id      用户组ID
     * @param userIds 用户ID集合
     * @return Reply
     */
    @Override
    public Reply addMembers(LoginInfo info, String id, List<String> userIds) {
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.addMembers(id, userIds);
        core.writeLog(info, OperateType.INSERT, "用户组管理", id, userIds);

        return ReplyHelper.success();
    }

    /**
     * 移除用户组成员
     *
     * @param info    用户关键信息
     * @param id      用户组ID
     * @param userIds 用户ID集合
     * @return Reply
     */
    @Override
    public Reply removeMembers(LoginInfo info, String id, List<String> userIds) {
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        mapper.removeMembers(id, userIds);
        core.writeLog(info, OperateType.DELETE, "用户组管理", id, userIds);

        return ReplyHelper.success();
    }

    /**
     * 获取日志列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getGroupLogs(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<Log> logs = core.getLogs(tenantId, "用户组管理", keyword);
        PageInfo<Log> pageInfo = new PageInfo<>(logs);

        return ReplyHelper.success(logs, pageInfo.getTotal());
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getGroupLog(String id) {
        Log log = core.getLog(id);
        if (log == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(log);
    }

    /**
     * 获取用户组编码
     *
     * @param tenantId 租户ID
     * @return 用户组编码
     */
    private String newGroupCode(String tenantId) {
        String group = "UserGroup:" + tenantId;
        while (true) {
            String code = Generator.newCode("#4", group, false);
            int count = mapper.getGroupCount(tenantId, code);
            if (count > 0) {
                continue;
            }

            return code;
        }
    }
}
