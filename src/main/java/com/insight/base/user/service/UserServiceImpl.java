package com.insight.base.user.service;

import com.insight.base.user.common.Core;
import com.insight.base.user.common.client.AuthClient;
import com.insight.base.user.common.client.MessageClient;
import com.insight.base.user.common.dto.*;
import com.insight.base.user.common.mapper.UserMapper;
import com.insight.utils.Json;
import com.insight.utils.Redis;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.Util;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.user.User;

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
    public UserVo getUser(Long id) {
        var user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未读取数据");
        }

        return user;
    }

    /**
     * 注册用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @Override
    public Long register(UserDto dto) {
        // 验证账号|手机号|邮箱是否已存在
        var id = creator.nextId(3);
        core.matchUser(id, dto.getAccount(), dto.getMobile(), dto.getEmail());

        // 验证验证码是否正确
        var code = dto.getCode();
        if (code != null && !code.isEmpty()) {
            var key = Util.md5("1" + dto.getMobile() + code);
            client.verifySmsCode(key);
        }

        dto.setId(id);
        dto.setType(0);
        dto.setCode(null);
        core.addUser(dto);

        return id;
    }

    /**
     * 更新用户昵称
     *
     * @param id   用户ID
     * @param name 用户昵称
     */
    @Override
    public void updateName(Long id, String name) {
        var user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        user.setName(name);
        mapper.updateUser(user.convert(User.class));
    }

    /**
     * 更新用户手机号
     *
     * @param id  用户ID
     * @param dto 手机验证码DTO
     */
    @Override
    public void updateMobile(Long id, MobileDto dto) {
        var user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        var mobile = dto.getMobile();
        var oldMobile = user.getMobile();
        if (mobile == null && oldMobile == null) {
            return;
        }

        if (mobile != null) {
            if (oldMobile != null) {
                throw new BusinessException("已绑定手机号,请先解除绑定");
            }

            var count = mapper.matchUsers(id, mobile);
            if (count > 0) {
                throw new BusinessException("手机号[" + mobile + "]已被使用");
            }
        }

        // 验证手机验证码
        var key = dto.getKey();
        var result = client.verifySmsCode(key);
        var reply = Json.toBean(result, Reply.class);
        if (!reply.getSuccess()) {
            throw new BusinessException(reply.getMessage());
        }

        if (oldMobile != null && !oldMobile.isEmpty()) {
            Redis.deleteKey("ID:" + oldMobile);
        }

        // 持久化数据
        user.setMobile(mobile);
        mapper.updateUser(user.convert(User.class));
    }

    /**
     * 更新用户Email
     *
     * @param id    用户ID
     * @param email Email
     */
    @Override
    public void updateEmail(Long id, String email) {
        var user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        var count = mapper.matchUsers(id, email);
        if (count > 0) {
            throw new BusinessException("Email[" + email + "]已被使用");
        }

        var oldEmail = user.getEmail();
        if (oldEmail != null && !oldEmail.isEmpty()) {
            Redis.deleteKey("ID:" + oldEmail);
        }

        user.setEmail(email);
        mapper.updateUser(user.convert(User.class));
    }

    /**
     * 更新用户头像
     *
     * @param id      用户ID
     * @param headImg 头像
     */
    @Override
    public void updateHeadImg(Long id, String headImg) {
        var user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        user.setHeadImg(headImg);
        mapper.updateUser(user.convert(User.class));
    }

    /**
     * 更新用户备注
     *
     * @param id     用户ID
     * @param remark 备注
     */
    @Override
    public void updateRemark(Long id, String remark) {
        var user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        user.setRemark(remark);
        mapper.updateUser(user.convert(User.class));
    }

    /**
     * 修改密码
     *
     * @param dto 密码DTO
     */
    @Override
    public void changePassword(PasswordDto dto) {
        var id = dto.getId();
        var user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        var key = "User:" + id;
        var pw = Redis.get(key, "password");
        var old = dto.getOld();
        if (old == null || old.isEmpty() || !old.equals(pw)) {
            throw new BusinessException("原密码错误,请输入正确的原密码");
        }

        var password = dto.getPassword();
        Redis.setHash(key, "password", password);
        mapper.updatePassword(id, password);
    }

    /**
     * 重置密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    @Override
    public Reply resetPassword(PasswordDto dto) {
        var result = client.verifySmsCode(dto.getKey());
        var reply = Json.toBean(result, Reply.class);
        if (!reply.getSuccess()) {
            return reply;
        }

        // 验证用户
        var mobile = reply.getBeanFromData(String.class);
        result = authClient.getCode(mobile);
        reply = Json.toBean(result, Reply.class);
        if (!reply.getSuccess()) {
            return reply;
        }

        // 获取旧密码用于计算签名
        var id = Long.valueOf(Redis.get("ID:" + mobile));
        var key = "User:" + id;
        var pw = Redis.get(key, "password");

        // 更新密码
        var password = dto.getPassword();
        Redis.setHash(key, "password", password);
        mapper.updatePassword(id, password);

        // 构造登录数据并返回Token
        var code = reply.getData().toString();
        var sign = Util.md5(Util.md5(mobile + pw) + code);
        var login = new LoginDto();
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
     */
    @Override
    public void setPayPassword(PasswordDto dto) {
        var password = dto.getPassword();
        if (password == null || password.isEmpty()) {
            throw new BusinessException("支付密码不能为空");
        }

        var id = dto.getId();
        var user = mapper.getUser(id);
        if (user == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        var result = client.verifySmsCode(dto.getKey());
        var reply = Json.toBean(result, Reply.class);
        if (!reply.getSuccess()) {
            throw new BusinessException(reply.getMessage());
        }

        Redis.setHash("User:" + id, "payPassword", password);
        mapper.updatePayPassword(id, password);
    }

    /**
     * 验证支付密码(供服务调用)
     *
     * @param id  用户ID
     * @param key 支付密码(MD5)
     */
    @Override
    public void verifyPayPw(Long id, String key) {
        var payPassword = Redis.get("User:" + id, "payPassword");
        if (payPassword == null || payPassword.isEmpty()) {
            throw new BusinessException("当前未设置支付密码,请先设置支付密码");
        }

        if (!payPassword.equals(key)) {
            throw new BusinessException("支付密码错误");
        }
    }
}
