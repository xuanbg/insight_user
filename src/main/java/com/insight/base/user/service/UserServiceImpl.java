package com.insight.base.user.service;

import com.insight.base.user.common.Core;
import com.insight.base.user.common.client.AuthClient;
import com.insight.base.user.common.client.MessageClient;
import com.insight.base.user.common.dto.*;
import com.insight.base.user.common.mapper.UserMapper;
import com.insight.utils.Redis;
import com.insight.utils.ReplyHelper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.Util;
import com.insight.utils.pojo.Reply;
import com.insight.utils.pojo.User;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户服务
 */
@org.springframework.stereotype.Service
public class UserServiceImpl implements UserService {
    private final SnowflakeCreator creator;
    private final UserMapper mapper;
    private final MessageClient client;
    private final AuthClient authClient;
    private final Core core;

    /**
     * 构造方法
     *
     * @param creator    雪花算法ID生成器
     * @param mapper     UserMapper
     * @param client     MessageClient
     * @param authClient AuthClient
     * @param core       Core
     */
    public UserServiceImpl(SnowflakeCreator creator, UserMapper mapper, MessageClient client, AuthClient authClient, Core core) {
        this.creator = creator;
        this.mapper = mapper;
        this.client = client;
        this.authClient = authClient;
        this.core = core;
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
     * 注册用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @Override
    public Reply register(UserDto dto) {
        // 验证账号|手机号|邮箱是否已存在
        Long id = creator.nextId(3);
        Reply reply = core.matchUser(id, dto.getAccount(), dto.getMobile(), dto.getEmail());
        if (reply != null) {
            return reply;
        }

        // 验证验证码是否正确
        String code = dto.getCode();
        if (code != null && !code.isEmpty()) {
            String key = Util.md5("1" + dto.getMobile() + code);
            reply = client.verifySmsCode(key);
            if (!reply.getSuccess()) {
                return reply;
            }
        }

        dto.setId(id);
        dto.setType(0);
        dto.setCode(null);
        core.addUser(dto);

        return ReplyHelper.created(id);
    }

    /**
     * 更新用户昵称
     *
     * @param id   用户ID
     * @param name 用户昵称
     * @return Reply
     */
    @Override
    public Reply updateName(Long id, String name) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        user.setName(name);
        mapper.updateUser(user.convert(User.class));

        return ReplyHelper.success();
    }

    /**
     * 更新用户手机号
     *
     * @param id  用户ID
     * @param dto 手机验证码DTO
     * @return Reply
     */
    @Override
    public Reply updateMobile(Long id, MobileDto dto) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        String mobile = dto.getMobile();
        String oldMobile = user.getMobile();
        if (mobile == null && oldMobile == null) {
            return ReplyHelper.success();
        }

        if (mobile != null) {
            if (oldMobile != null) {
                return ReplyHelper.invalidParam("已绑定手机号,请先解除绑定");
            }

            int count = mapper.matchUsers(id, mobile);
            if (count > 0) {
                return ReplyHelper.invalidParam("手机号[" + mobile + "]已被使用");
            }
        }

        // 验证手机验证码
        String key = dto.getKey();
        Reply reply = client.verifySmsCode(key);
        if (!reply.getSuccess()) {
            return reply;
        }

        if (oldMobile != null && !oldMobile.isEmpty()) {
            Redis.deleteKey("ID:" + oldMobile);
        }

        // 持久化数据
        user.setMobile(mobile);
        mapper.updateUser(user.convert(User.class));

        return ReplyHelper.success();
    }

    /**
     * 更新用户Email
     *
     * @param id    用户ID
     * @param email Email
     * @return Reply
     */
    @Override
    public Reply updateEmail(Long id, String email) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        int count = mapper.matchUsers(id, email);
        if (count > 0) {
            return ReplyHelper.invalidParam("Email[" + email + "]已被使用");
        }

        String oldEmail = user.getEmail();
        if (oldEmail != null && !oldEmail.isEmpty()) {
            Redis.deleteKey("ID:" + oldEmail);
        }

        user.setEmail(email);
        mapper.updateUser(user.convert(User.class));

        return ReplyHelper.success();
    }

    /**
     * 更新用户头像
     *
     * @param id      用户ID
     * @param headImg 头像
     * @return Reply
     */
    @Override
    public Reply updateHeadImg(Long id, String headImg) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        user.setHeadImg(headImg);
        mapper.updateUser(user.convert(User.class));

        return ReplyHelper.success();
    }

    /**
     * 更新用户备注
     *
     * @param id     用户ID
     * @param remark 备注
     * @return Reply
     */
    @Override
    public Reply updateRemark(Long id, String remark) {
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        user.setRemark(remark);
        mapper.updateUser(user.convert(User.class));

        return ReplyHelper.success();
    }

    /**
     * 修改密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    @Override
    public Reply changePassword(PasswordDto dto) {
        Long id = dto.getId();
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        String key = "User:" + id;
        String pw = Redis.get(key, "password");
        String old = dto.getOld();
        if (old == null || old.isEmpty() || !old.equals(pw)) {
            return ReplyHelper.invalidParam("原密码错误,请输入正确的原密码");
        }

        String password = dto.getPassword();
        Redis.setHash(key, "password", password);
        mapper.updatePassword(id, password);

        return ReplyHelper.success();
    }

    /**
     * 重置密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    @Override
    public Reply resetPassword(PasswordDto dto) {
        Reply reply = client.verifySmsCode(dto.getKey());
        if (!reply.getSuccess()) {
            return reply;
        }

        // 验证用户
        String mobile = reply.getData().toString();
        reply = authClient.getCode(mobile);
        if (!reply.getSuccess()) {
            return reply;
        }

        // 获取旧密码用于计算签名
        Long id = Long.valueOf(Redis.get("ID:" + mobile));
        String key = "User:" + id;
        String pw = Redis.get(key, "password");

        // 更新密码
        String password = dto.getPassword();
        Redis.setHash(key, "password", password);
        mapper.updatePassword(id, password);

        // 构造登录数据并返回Token
        String code = reply.getData().toString();
        String sign = Util.md5(Util.md5(mobile + pw) + code);
        LoginDto login = new LoginDto();
        login.setAppId(dto.getAppId());
        login.setTenantId(dto.getTenantId());
        login.setAccount(mobile);
        login.setSignature(sign);

        return authClient.getToken(login);
    }

    /**
     * 设置支付密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    @Override
    public Reply setPayPassword(PasswordDto dto) {
        String password = dto.getPassword();
        if (password == null || password.isEmpty()) {
            return ReplyHelper.invalidParam("支付密码不能为空");
        }

        Long id = dto.getId();
        UserVo user = mapper.getUser(id);
        if (user == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        Reply reply = client.verifySmsCode(dto.getKey());
        if (!reply.getSuccess()) {
            return reply;
        }

        Redis.setHash("User:" + id, "payPassword", password);
        mapper.updatePayPassword(id, password);

        return ReplyHelper.success();
    }

    /**
     * 验证支付密码(供服务调用)
     *
     * @param id  用户ID
     * @param key 支付密码(MD5)
     * @return Reply
     */
    @Override
    public Reply verifyPayPw(Long id, String key) {
        String payPassword = Redis.get("User:" + id, "payPassword");
        if (payPassword == null || payPassword.isEmpty()) {
            return ReplyHelper.fail("当前未设置支付密码,请先设置支付密码");
        }

        return payPassword.equals(key) ? ReplyHelper.success() : ReplyHelper.invalidParam("支付密码错误");
    }
}
