package com.insight.base.user.manage;

import com.github.pagehelper.PageHelper;
import com.insight.base.user.common.Core;
import com.insight.base.user.common.client.LogClient;
import com.insight.base.user.common.client.LogServiceClient;
import com.insight.base.user.common.client.OrgClient;
import com.insight.base.user.common.dto.FuncPermitDto;
import com.insight.base.user.common.dto.UserDto;
import com.insight.base.user.common.dto.UserVo;
import com.insight.base.user.common.mapper.UserMapper;
import com.insight.utils.Redis;
import com.insight.utils.ReplyHelper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.Util;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.*;
import com.insight.utils.pojo.message.OperateType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户管理服务
 */
@org.springframework.stereotype.Service
public class ManageServiceImpl implements ManageService {
    private static final String BUSINESS = "用户管理";
    private final SnowflakeCreator creator;
    private final UserMapper mapper;
    private final LogServiceClient logClient;
    private final OrgClient client;
    private final Core core;

    /**
     * 构造方法
     *
     * @param creator   雪花算法ID生成器
     * @param mapper    UserMapper
     * @param logClient LogServiceClient
     * @param client    Feign客户端
     * @param core      Core
     */
    public ManageServiceImpl(SnowflakeCreator creator, UserMapper mapper, LogServiceClient logClient, OrgClient client, Core core) {
        this.creator = creator;
        this.mapper = mapper;
        this.logClient = logClient;
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
            List<TreeVo> orgList = client.getSubOrganizes(orgId).getListFromData(TreeVo.class);
            search.setLongSet(orgList.stream().map(BaseVo::getId).toList());
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
        UserVo user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未读取数据");
        }

        return user;
    }

    /**
     * 获取用户功能授权
     *
     * @param id 用户ID
     * @return Reply
     */
    @Override
    public List<FuncPermitDto> getUserPermit(Long id) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未读取数据");
        }

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
        Long id = creator.nextId(3);
        core.matchUser(id, dto.getAccount(), dto.getMobile(), dto.getEmail());

        dto.setId(id);
        Long tenantId = info.getTenantId();
        dto.setTenantId(tenantId);
        dto.setType(tenantId == null ? 1 : 0);
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        core.addUser(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, dto);

        return id;
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
        UserVo user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        String account = dto.getAccount();
        String mobile = dto.getMobile();
        String email = dto.getEmail();
        if (account == null) {
            account = user.getAccount();
            dto.setAccount(account);
        }

        if (mobile == null) {
            mobile = user.getMobile();
            dto.setMobile(mobile);
        }

        if (email == null) {
            email = user.getEmail();
            dto.setEmail(email);
        }

        if (dto.getHeadImg() == null) {
            dto.setHeadImg(user.getHeadImg());
        }

        if (dto.getRemark() == null) {
            dto.setRemark(user.getRemark());
        }

        core.matchUser(id, account, mobile, email);

        // 清理失效缓存数据
        String oldAccount = user.getAccount();
        if (!account.equals(oldAccount)) {
            Redis.deleteKey("ID:" + oldAccount);
        }

        String oldMobile = user.getMobile();
        if (oldMobile != null && !oldMobile.isEmpty()) {
            Redis.deleteKey("ID:" + oldMobile);
        }

        String oldEmail = user.getEmail();
        if (oldEmail != null && !oldEmail.isEmpty()) {
            Redis.deleteKey("ID:" + oldEmail);
        }

        var roleIds = dto.getRoleIds();
        if (roleIds != null && !roleIds.isEmpty()) {
            mapper.removeRoleRelation(info.getTenantId(), id);
            mapper.addRoleMember(id, roleIds);
        }

        mapper.updateUser(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, dto);
    }

    /**
     * 删除用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @Override
    public void deleteUser(LoginInfo info, Long id) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未删除数据");
        }

        // 清理缓存
        Redis.deleteKey("ID:" + user.getAccount());
        Redis.deleteKey("ID:" + user.getMobile());
        Redis.deleteKey("ID:" + user.getEmail());
        Redis.deleteKey("ID:" + user.getUnionId());
        Redis.deleteKey("User:" + id);
        Redis.deleteKey("UserToken:" + id);

        // 删除数据
        mapper.deleteUser(id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, user);
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
        var user = core.changeUserStatus(id, status);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, user);
    }

    /**
     * 重置用户密码
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @Override
    public void resetPassword(LoginInfo info, Long id) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        var password = Util.md5("123456");
        mapper.updatePassword(id, password);
        String key = "User:" + id;
        if (Redis.hasKey(key)) {
            Redis.setHash(key, "password", password);
        }

        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, user);
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

        UserVo user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

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
        mapper.removeRelation(tenantId, id);
        mapper.removeGroupRelation(tenantId, id);
        mapper.removeOrganizeRelation(tenantId, id);
        mapper.removeRoleRelation(tenantId, id);
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
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getUserLogs(Search search) {
        return logClient.getLogs(BUSINESS, search.getKeyword(), search.getPageNum(), search.getPageSize());
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getUserLog(Long id) {
        return logClient.getLog(id);
    }
}
