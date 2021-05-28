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
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.OperateType;
import com.insight.utils.pojo.Reply;
import com.insight.utils.pojo.SearchDto;

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
    private final SnowflakeCreator creator;
    private final GroupMapper mapper;
    private final LogServiceClient client;

    /**
     * 构造方法
     *
     * @param creator 雪花算法ID生成器
     * @param mapper  GroupMapper
     * @param client  LogServiceClient
     */
    public GroupServiceImpl(SnowflakeCreator creator, GroupMapper mapper, LogServiceClient client) {
        this.creator = creator;
        this.mapper = mapper;
        this.client = client;
    }

    /**
     * 查询用户组列表
     *
     * @param tenantId 租户ID
     * @param search   查询实体类
     * @return Reply
     */
    @Override
    public Reply getGroups(Long tenantId, SearchDto search) {
        PageHelper.startPage(search.getPage(), search.getSize());
        List<GroupListDto> groups = mapper.getGroups(tenantId, search.getKeyword());
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
    public Reply getGroup(Long id) {
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
        Long id = creator.nextId(7);
        Long tenantId = info.getTenantId();
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
        Long id = dto.getId();
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
    public Reply deleteGroup(LoginInfo info, Long id) {
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
     * @param id     用户组ID
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getMembers(Long id, SearchDto search) {
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        PageHelper.startPage(search.getPage(), search.getSize());
        List<UserListDto> members = mapper.getMembers(id, search.getKeyword());
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
    public Reply getOthers(Long id) {
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
    public Reply addMembers(LoginInfo info, Long id, List<Long> userIds) {
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
    public Reply removeMembers(LoginInfo info, Long id, List<Long> userIds) {
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
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getGroupLogs(SearchDto search) {
        return client.getLogs(BUSINESS, search.getKeyword(), search.getPage(), search.getSize());
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getGroupLog(Long id) {
        return client.getLog(id);
    }

    /**
     * 获取用户组编码
     *
     * @param tenantId 租户ID
     * @return 用户组编码
     */
    private String newGroupCode(Long tenantId) {
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
