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
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.OperateType;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.User;
import org.springframework.transaction.annotation.Transactional;

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
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getUsers(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<UserListDto> scenes = mapper.getUsers(tenantId, keyword);
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
        String tenantId = info.getTenantId();
        dto.setId(id);

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
        int count = mapper.matchUsers(id, account);
        if (count > 0) {
            return ReplyHelper.invalidParam("账号[" + account + "]已被使用");
        }

        String mobile = dto.getMobile();
        count = mapper.matchUsers(id, mobile);
        if (count > 0) {
            return ReplyHelper.invalidParam("手机号[" + mobile + "]已被使用");
        }

        String email = dto.getEmail();
        count = mapper.matchUsers(id, email);
        if (count > 0) {
            return ReplyHelper.invalidParam("Email[" + email + "]已被使用");
        }

        // 清理失效缓存数据
        String oldAccount = user.getAccount();
        if (!account.equals(oldAccount)) {
            Redis.deleteKey("ID:" + oldAccount);
        }

        String oldMobile = user.getMobile();
        if (mobile != null && !mobile.equals(oldMobile) && oldMobile != null && !oldMobile.isEmpty()) {
            Redis.deleteKey("ID:" + oldMobile);
        }

        String oldEmail = user.getEmail();
        if (email != null && !email.equals(oldEmail) && oldEmail != null && !oldEmail.isEmpty()) {
            Redis.deleteKey("ID:" + oldEmail);
        }

        // 更新数据
        mapper.editUser(dto);
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
    @Transactional
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
        mapper.deleteRelation(id);
        mapper.deleteOrgMember(id);
        mapper.deleteGroupMember(id);
        mapper.deleteRoleMember(id);
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

        mapper.changeUserStatus(id, status);
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

        mapper.resetPassword(id, password);
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
}
