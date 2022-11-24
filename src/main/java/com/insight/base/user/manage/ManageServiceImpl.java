package com.insight.base.user.manage;

import com.github.pagehelper.PageHelper;
import com.insight.base.user.common.Core;
import com.insight.base.user.common.client.LogClient;
import com.insight.base.user.common.client.LogServiceClient;
import com.insight.base.user.common.dto.FuncPermitDto;
import com.insight.base.user.common.dto.PasswordDto;
import com.insight.base.user.common.dto.UserDto;
import com.insight.base.user.common.dto.UserVo;
import com.insight.base.user.common.mapper.UserMapper;
import com.insight.utils.Redis;
import com.insight.utils.ReplyHelper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.Util;
import com.insight.utils.pojo.OperateType;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
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
    private final LogServiceClient client;
    private final Core core;

    /**
     * 构造方法
     *
     * @param creator 雪花算法ID生成器
     * @param mapper  UserMapper
     * @param client  LogServiceClient
     * @param core    Core
     */
    public ManageServiceImpl(SnowflakeCreator creator, UserMapper mapper, LogServiceClient client, Core core) {
        this.creator = creator;
        this.mapper = mapper;
        this.client = client;
        this.core = core;
    }

    /**
     * 查询用户列表
     *
     * @param search   查询实体类
     * @return Reply
     */
    @Override
    public Reply getUsers(Search search) {
        if (search.getTenantId() != null && search.getInvalid() && search.getKeyword() == null) {
            return ReplyHelper.invalidParam("查询关键词不能为空");
        }

        var page = PageHelper.startPage(search.getPageNum(), search.getPageSize())
                .setOrderBy(search.getOrderBy()).doSelectPage(() -> mapper.getUsers(search));

        var total = page.getTotal();
        return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    @Override
    public Reply getUser(Long id) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(user);
    }

    /**
     * 获取用户功能授权
     *
     * @param id 用户ID
     * @return Reply
     */
    @Override
    public Reply getUserPermit(Long id) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        List<FuncPermitDto> list = mapper.getUserPermit(id);
        return ReplyHelper.success(list);
    }

    /**
     * 新增用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     * @return Reply
     */
    @Override
    public Reply newUser(LoginInfo info, UserDto dto) {
        Long id = creator.nextId(3);
        Reply reply = core.matchUser(id, dto.getAccount(), dto.getMobile(), dto.getEmail());
        if (reply != null) {
            return reply;
        }

        dto.setId(id);
        Long tenantId = info.getTenantId();
        dto.setTenantId(tenantId);
        dto.setType(tenantId == null ? 1 : 0);
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        core.addUser(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, dto);

        return ReplyHelper.created(id);
    }

    /**
     * 编辑用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     * @return Reply
     */
    @Override
    public Reply editUser(LoginInfo info, UserDto dto) {
        Long id = dto.getId();
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        String account = dto.getAccount();
        String mobile = dto.getMobile();
        String email = dto.getEmail();
        Reply reply = core.matchUser(id, account, mobile, email);
        if (reply != null) {
            return reply;
        }

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

        // 更新数据
        mapper.updateUser(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, dto);

        return ReplyHelper.success();
    }

    /**
     * 删除用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    @Override
    public Reply deleteUser(LoginInfo info, Long id) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
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

        return ReplyHelper.success();
    }

    /**
     * 改变用户禁用/启用状态
     *
     * @param info   用户关键信息
     * @param id     用户ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @Override
    public Reply changeUserStatus(LoginInfo info, Long id, boolean status) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        // 更新缓存
        String key = "User:" + id;
        if (Redis.hasKey(key)) {
            Redis.setHash(key, "invalid", status);
        }

        mapper.updateStatus(id, status);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, user);

        return ReplyHelper.success();
    }

    /**
     * 重置用户密码
     *
     * @param info 用户关键信息
     * @param dto  密码DTO
     * @return Reply
     */
    @Override
    public Reply resetPassword(LoginInfo info, PasswordDto dto) {
        Long id = dto.getId();
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        String password = dto.getPassword();
        if (password == null || password.isEmpty()) {
            password = Util.md5("123456");
        }

        mapper.updatePassword(id, password);
        String key = "User:" + id;
        if (Redis.hasKey(key)) {
            Redis.setHash(key, "password", password);
        }

        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, user);

        return ReplyHelper.success();
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
            return ReplyHelper.invalidParam("租户ID不能为空");
        }

        if (Util.isEmpty(search.getKeyword())) {
            return ReplyHelper.invalidParam("查询关键词不能为空");
        }

        var page = PageHelper.startPage(search.getPageNum(), search.getPageSize())
                .setOrderBy(search.getOrderBy()).doSelectPage(() -> mapper.getOtherUsers(search));

        var total = page.getTotal();
        return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
    }

    /**
     * 邀请用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    @Override
    public Reply inviteUser(LoginInfo info, Long id) {
        Long tenantId = info.getTenantId();
        if (tenantId == null) {
            return ReplyHelper.invalidParam("租户ID不存在,请以租户身份登录");
        }

        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        int count = mapper.matchRelation(tenantId, id);
        if (count == 0) {
            mapper.addRelation(tenantId, id);
        }

        return ReplyHelper.success();
    }

    /**
     * 清退用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    @Override
    @Transactional
    public Reply removeUser(LoginInfo info, Long id) {
        Long tenantId = info.getTenantId();
        mapper.removeRelation(tenantId, id);
        mapper.removeGroupRelation(tenantId, id);
        mapper.removeOrganizeRelation(tenantId, id);
        mapper.removeRoleRelation(tenantId, id);

        return ReplyHelper.success();
    }

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getUserLogs(Search search) {
        return client.getLogs(BUSINESS, search.getKeyword(), search.getPageNum(), search.getPageSize());
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getUserLog(Long id) {
        return client.getLog(id);
    }
}
