package com.insight.base.user.group;

import com.github.pagehelper.PageHelper;
import com.insight.base.user.common.dto.GroupDto;
import com.insight.base.user.common.dto.UserVo;
import com.insight.base.user.common.mapper.GroupMapper;
import com.insight.utils.ReplyHelper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.redis.Generator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户组管理服务
 */
@org.springframework.stereotype.Service
public class GroupServiceImpl implements GroupService {
    private final SnowflakeCreator creator;
    private final GroupMapper mapper;

    /**
     * 构造方法
     *
     * @param creator 雪花算法ID生成器
     * @param mapper  GroupMapper
     */
    public GroupServiceImpl(SnowflakeCreator creator, GroupMapper mapper) {
        this.creator = creator;
        this.mapper = mapper;
    }

    /**
     * 查询用户组列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getGroups(Search search) {
        try (var page = PageHelper.startPage(search.getPageNum(), search.getPageSize()).setOrderBy(search.getOrderBy())
                .doSelectPage(() -> mapper.getGroups(search))) {
            var total = page.getTotal();
            return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
        }
    }

    /**
     * 获取用户组详情
     *
     * @param id 用户组ID
     * @return Reply
     */
    @Override
    public GroupDto getGroup(Long id) {
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            throw new BusinessException("ID不存在,未读取数据");
        }

        return group;
    }

    /**
     * 新增用户组
     *
     * @param info 用户关键信息
     * @param dto  用户组DTO
     * @return Reply
     */
    @Override
    public Long newGroup(LoginInfo info, GroupDto dto) {
        Long id = creator.nextId(5);
        Long tenantId = info.getTenantId();
        dto.setId(id);
        dto.setTenantId(tenantId);
        dto.setCode(newGroupCode(tenantId));
        dto.setBuiltin(false);
        dto.setCreator(info.getName());
        dto.setCreatorId(info.getId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addGroup(dto);
        return id;
    }

    /**
     * 编辑用户组
     *
     * @param info 用户关键信息
     * @param dto  用户组DTO
     */
    @Override
    public void editGroup(LoginInfo info, GroupDto dto) {
        Long id = dto.getId();
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        mapper.updateGroup(dto);
    }

    /**
     * 删除用户组
     *
     * @param info 用户关键信息
     * @param id   用户组ID
     */
    @Override
    public void deleteGroup(LoginInfo info, Long id) {
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            throw new BusinessException("ID不存在,未删除数据");
        }

        mapper.deleteGroup(id);
    }

    /**
     * 查询用户组成员
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getMembers(Search search) {
        GroupDto group = mapper.getGroup(search.getId());
        if (group == null) {
            throw new BusinessException("ID不存在,未读取数据");
        }

        try (var page = PageHelper.startPage(search.getPageNum(), search.getPageSize()).setOrderBy(search.getOrderBy())
                .doSelectPage(() -> mapper.getMembers(search))) {
            var total = page.getTotal();
            return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
        }
    }

    /**
     * 查询用户组可用用户列表
     *
     * @param id 用户组ID
     * @return Reply
     */
    @Override
    public List<UserVo> getOthers(Long id) {
        return mapper.getOthers(id);
    }

    /**
     * 添加用户组成员
     *
     * @param info    用户关键信息
     * @param id      用户组ID
     * @param userIds 用户ID集合
     */
    @Override
    public void addMembers(LoginInfo info, Long id, List<Long> userIds) {
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        mapper.addMembers(id, userIds);
    }

    /**
     * 移除用户组成员
     *
     * @param info    用户关键信息
     * @param id      用户组ID
     * @param userIds 用户ID集合
     */
    @Override
    public void removeMembers(LoginInfo info, Long id, List<Long> userIds) {
        GroupDto group = mapper.getGroup(id);
        if (group == null) {
            throw new BusinessException("ID不存在,未删除数据");
        }

        mapper.removeMembers(id, userIds);
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
