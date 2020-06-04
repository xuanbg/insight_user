package com.insight.base.user.group;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.base.user.common.client.LogClient;
import com.insight.base.user.common.client.LogServiceClient;
import com.insight.base.user.common.dto.GroupDto;
import com.insight.base.user.common.dto.GroupListDto;
import com.insight.base.user.common.dto.UserListDto;
import com.insight.base.user.common.mapper.GroupMapper;
import com.insight.utils.Generator;
import com.insight.utils.ReplyHelper;
import com.insight.utils.Util;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.OperateType;
import com.insight.utils.pojo.Reply;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户组管理服务
 */
@org.springframework.stereotype.Service
public class GroupServiceImpl implements GroupService {
    private static final String BUSINESS = "用户组管理";
    private final GroupMapper mapper;
    private final LogServiceClient client;

    /**
     * 构造方法
     *
     * @param mapper GroupMapper
     * @param client LogServiceClient
     */
    public GroupServiceImpl(GroupMapper mapper, LogServiceClient client) {
        this.mapper = mapper;
        this.client = client;
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
        String id = Util.uuid();
        String tenantId = info.getTenantId();
        dto.setId(id);
        dto.setTenantId(tenantId);
        dto.setCode(newGroupCode(tenantId));
        dto.setBuiltin(false);
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addGroup(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, dto);

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
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, dto);

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
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, group);

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
        List<UserListDto> members = mapper.getMembers(id, keyword);
        PageInfo<UserListDto> pageInfo = new PageInfo<>(members);

        return ReplyHelper.success(members, pageInfo.getTotal());
    }

    /**
     * 查询用户组可用用户列表
     *
     * @param id 用户组ID
     * @return Reply
     */
    @Override
    public Reply getOthers(String id) {
        List<UserListDto> users = mapper.getOthers(id);

        return ReplyHelper.success(users);
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
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, userIds);

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
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, userIds);

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
        return client.getLogs(BUSINESS, keyword, page, size);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getGroupLog(String id) {
        return client.getLog(id);
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
