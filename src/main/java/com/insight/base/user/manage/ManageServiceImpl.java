package com.insight.base.user.manage;

import com.github.pagehelper.PageHelper;
import com.insight.base.user.common.Core;
import com.insight.base.user.common.client.OrgClient;
import com.insight.base.user.common.dto.FuncPermitDto;
import com.insight.base.user.common.dto.UserVo;
import com.insight.base.user.common.mapper.UserMapper;
import com.insight.utils.ReplyHelper;
import com.insight.utils.Util;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.base.TreeBase;
import com.insight.utils.pojo.user.User;
import com.insight.utils.pojo.user.UserDto;
import com.insight.utils.redis.HashOps;
import com.insight.utils.redis.KeyOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户管理服务
 */
@org.springframework.stereotype.Service
public class ManageServiceImpl implements ManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageServiceImpl.class);
    private final UserMapper mapper;
    private final OrgClient client;
    private final Core core;

    /**
     * 构造方法
     *
     * @param mapper UserMapper
     * @param client Feign客户端
     * @param core   Core
     */
    public ManageServiceImpl(UserMapper mapper, OrgClient client, Core core) {
        this.mapper = mapper;
        this.client = client;
        this.core = core;
    }

    /**
     * 查询用户列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getUsers(Search search) {
        var orgId = search.getOwnerId();
        if (orgId != null) {
            List<TreeBase> orgList = client.getSubOrganizes(orgId).getListFromData(TreeBase.class);
            search.setLongSet(orgList.stream().map(TreeBase::getId).toList());
        }

        try (var page = PageHelper.startPage(search.getPageNum(), search.getPageSize()).setOrderBy(search.getOrderBy())
                .doSelectPage(() -> mapper.getUsers(search))) {
            var total = page.getTotal();
            return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
        }
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    @Override
    public UserVo getUser(Long id) {
        var data = getUserById(id);
        return data.convert(UserVo.class);
    }

    /**
     * 获取用户功能授权
     *
     * @param id 用户ID
     * @return Reply
     */
    @Override
    public List<FuncPermitDto> getUserPermit(Long id) {
        var data = getUserById(id);
        return mapper.getUserPermit(id);
    }

    /**
     * 新增用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     * @return Reply
     */
    @Override
    public Long newUser(LoginInfo info, UserDto dto) {
        core.matchUser(0L, dto.getAccount(), dto.getMobile(), dto.getEmail());

        Long tenantId = info.getTenantId();
        dto.setTenantId(tenantId);
        dto.setType(tenantId == null ? 0 : 1);
        dto.setCreator(info.getName());
        dto.setCreatorId(info.getId());
        return core.processUser(dto);
    }

    /**
     * 编辑用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     */
    @Override
    @Transactional
    public void editUser(LoginInfo info, UserDto dto) {
        Long id = dto.getId();
        var data = getUserById(id);

        String account = dto.getAccount();
        String mobile = dto.getMobile();
        String email = dto.getEmail();

        if (dto.getRemark() == null) {
            dto.setRemark(data.getRemark());
        }

        core.matchUser(id, account, mobile, email);
        core.processUser(dto);

        var roleIds = dto.getRoleIds();
        if (roleIds != null && !roleIds.isEmpty()) {
            mapper.removeRoleRelation(info.getTenantId(), id);
            mapper.addRoleMember(id, roleIds);
        }
    }

    /**
     * 删除用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @Override
    public void deleteUser(LoginInfo info, Long id) {
        var data = getUserById(id);

        // 清理缓存
        KeyOps.delete("ID:" + data.getAccount());
        KeyOps.delete("ID:" + data.getMobile());
        KeyOps.delete("ID:" + data.getEmail());
        KeyOps.delete("ID:" + data.getUnionId());
        KeyOps.delete("User:" + id);
        KeyOps.delete("UserToken:" + id);

        // 删除数据
        mapper.deleteUser(id);
    }

    /**
     * 改变用户禁用/启用状态
     *
     * @param info   用户关键信息
     * @param id     用户ID
     * @param status 禁用/启用状态
     */
    @Override
    public void changeUserStatus(LoginInfo info, Long id, boolean status) {
        var user = mapper.getUser(id);
        if (user == null) {
            return;
        }

        if (info.getTenantId() != null) {
            mapper.disableUser(info.getTenantId(), id, status);
            return;
        }

        // 更新缓存
        String key = "User:" + id;
        if (KeyOps.hasKey(key)) {
            HashOps.put(key, "invalid", status);
        }

        mapper.updateStatus(id, status);
    }

    /**
     * 重置用户密码
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @Override
    public void resetPassword(LoginInfo info, Long id) {
        var data = getUserById(id);
        if ("123".equals(data.getPassword())){
            throw new BusinessException("该用户密码已废弃，不能重置密码");
        }

        var password = Util.md5("123456");
        mapper.updatePassword(id, password);
        String key = "User:" + id;
        if (KeyOps.hasKey(key)) {
            HashOps.put(key, "password", password);
        }
    }

    /**
     * 获取可邀请用户列表
     *
     * @param search 查询关键词
     * @return Reply
     */
    @Override
    public Reply getInviteUsers(Search search) {
        Long tenantId = search.getTenantId();
        if (tenantId == null) {
            throw new BusinessException("租户ID不能为空");
        }

        if (Util.isEmpty(search.getKeyword())) {
            throw new BusinessException("查询关键词不能为空");
        }

        try (var page = PageHelper.startPage(search.getPageNum(), search.getPageSize()).setOrderBy(search.getOrderBy())
                .doSelectPage(() -> mapper.getOtherUsers(search))) {
            var total = page.getTotal();
            return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
        }
    }

    /**
     * 邀请用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @Override
    public void inviteUser(LoginInfo info, Long id) {
        Long tenantId = info.getTenantId();
        if (tenantId == null) {
            throw new BusinessException("租户ID不存在,请以租户身份登录");
        }

        var data = getUserById(id);
        int count = mapper.matchRelation(tenantId, id);
        if (count == 0) {
            mapper.addRelation(tenantId, id);
        }
    }

    /**
     * 清退用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @Override
    @Transactional
    public void removeUser(LoginInfo info, Long id) {
        Long tenantId = info.getTenantId();
        var data = getUserById(id);
        mapper.removeRelation(tenantId, id);
        mapper.removeGroupRelation(tenantId, id);
        mapper.removeOrganizeRelation(tenantId, id);
        mapper.removeRoleRelation(tenantId, id);
    }

    /**
     * 清除绑定设备
     *
     * @param id 用户ID
     */
    @Override
    public void removeDevice(Long id) {
        mapper.removeDevice(id);
    }

    /**
     * 查询符合条件的用户数量
     *
     * @param keyword 查询条件
     * @return 用户数量
     */
    @Override
    public int getUserCount(String keyword) {
        if (Util.isEmpty(keyword)) {
            throw new BusinessException("查询关键词不能为空");
        }

        return mapper.getCount(keyword);
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户
     */
    private User getUserById(Long id) {
        var user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("指定的用户不存在");
        }

        return user;
    }
}
