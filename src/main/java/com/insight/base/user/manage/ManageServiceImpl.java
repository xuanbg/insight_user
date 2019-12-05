package com.insight.base.user.manage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.base.user.common.Core;
import com.insight.base.user.common.dto.PasswordDto;
import com.insight.base.user.common.dto.UserDto;
import com.insight.base.user.common.dto.UserListDto;
import com.insight.base.user.common.mapper.UserMapper;
import com.insight.util.Redis;
import com.insight.util.ReplyHelper;
import com.insight.util.Util;
import com.insight.util.pojo.*;

import java.util.List;

import static com.insight.util.Generator.uuid;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户管理服务
 */
@org.springframework.stereotype.Service
public class ManageServiceImpl implements ManageService {
    private final UserMapper mapper;
    private final Core core;

    /**
     * 构造方法
     *
     * @param mapper UserMapper
     * @param core   Core
     */
    public ManageServiceImpl(UserMapper mapper, Core core) {
        this.mapper = mapper;
        this.core = core;
    }

    /**
     * 查询用户列表
     *
     * @param tenantId 租户ID
     * @param all      是否查询全部用户
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getUsers(String tenantId, boolean all, String keyword, int page, int size) {
        if (tenantId != null && all && keyword == null) {
            return ReplyHelper.invalidParam("查询关键词不能为空");
        }

        PageHelper.startPage(page, size);
        List<UserListDto> scenes = mapper.getUsers(all ? null : tenantId, keyword);
        PageInfo<UserListDto> pageInfo = new PageInfo<>(scenes);

        return ReplyHelper.success(scenes, pageInfo.getTotal());
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    @Override
    public Reply getUser(String id) {
        UserDto user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(user);
    }

    /**
     * 新增用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     * @return Reply
     */
    @Override
    public Reply newUser(LoginInfo info, User dto) {
        String id = uuid();
        Reply reply = core.matchUser(id, dto.getAccount(), dto.getMobile(), dto.getEmail());
        if (reply != null) {
            return reply;
        }

        dto.setId(id);
        String tenantId = info.getTenantId();
        core.addUser(dto, tenantId);
        core.writeLog(info, OperateType.INSERT, "用户管理", id, dto);

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
        String id = dto.getId();
        UserDto user = mapper.getUser(id);
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
        core.writeLog(info, OperateType.UPDATE, "用户管理", id, dto);

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
    public Reply deleteUser(LoginInfo info, String id) {
        UserDto user = mapper.getUser(id);
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
        core.writeLog(info, OperateType.DELETE, "用户管理", id, user);

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
    public Reply changeUserStatus(LoginInfo info, String id, boolean status) {
        UserDto user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        // 更新缓存
        String key = "User:" + id;
        if (Redis.hasKey(key)) {
            Redis.set(key, "invalid", status);
        }

        mapper.updateStatus(id, status);
        core.writeLog(info, OperateType.UPDATE, "用户管理", id, user);

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
        String id = dto.getId();
        UserDto user = mapper.getUser(id);
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
            Redis.set(key, "password", password);
        }

        core.writeLog(info, OperateType.UPDATE, "用户管理", id, user);

        return ReplyHelper.success();
    }

    /**
     * 邀请用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    @Override
    public Reply inviteUser(LoginInfo info, String id) {
        String tenantId = info.getTenantId();
        if (tenantId == null || tenantId.isEmpty()) {
            return ReplyHelper.invalidParam("租户ID不存在,请以租户身份登录");
        }

        UserDto user = mapper.getUser(id);
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
     * 获取日志列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getUserLogs(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<Log> logs = core.getLogs(tenantId, "用户管理", keyword);
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
    public Reply getUserLog(String id) {
        Log log = core.getLog(id);
        if (log == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(log);
    }
}
