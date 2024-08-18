package com.insight.base.user.personal;

import com.insight.base.user.common.Core;
import com.insight.base.user.common.client.AuthClient;
import com.insight.base.user.common.client.MessageClient;
import com.insight.base.user.common.dto.*;
import com.insight.base.user.common.mapper.UserMapper;
import com.insight.utils.Util;
import com.insight.utils.WechatHelper;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.user.User;
import com.insight.utils.pojo.user.UserDto;
import com.insight.utils.redis.Redis;
import org.springframework.stereotype.Service;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户服务
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final MessageClient client;
    private final AuthClient authClient;
    private final Core core;

    /**
     * 构造方法
     *
     * @param mapper     UserMapper
     * @param client     MessageClient
     * @param authClient AuthClient
     * @param core       Core
     */
    public UserServiceImpl(UserMapper mapper, MessageClient client, AuthClient authClient, Core core) {
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
        var data = getUserById(id);
        return data.convert(UserVo.class);
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
        core.matchUser(0L, dto.getAccount(), dto.getMobile(), dto.getEmail());

        // 验证验证码是否正确
        var code = dto.getCode();
        if (code != null && !code.isEmpty()) {
            var key = Util.md5("1" + dto.getMobile() + code);
            client.verifySmsCode(key);
        }

        dto.setType(1);
        dto.setCode(null);
        return core.processUser(dto);
    }

    /**
     * 更新用户昵称
     *
     * @param id   用户ID
     * @param name 用户昵称
     */
    @Override
    public void updateName(Long id, String name) {
        var data = getUserById(id);

        data.setName(name);
        mapper.updateUser(data);
    }

    /**
     * 更新用户手机号
     *
     * @param id  用户ID
     * @param dto 手机验证码DTO
     */
    @Override
    public void updateMobile(Long id, MobileDto dto) {
        var mobile = dto.getMobile();
        var data = getUserById(id);
        if (Util.isNotEmpty(mobile)) {
            if (mapper.keyIsExisted(id, mobile)) {
                throw new BusinessException("手机号[" + mobile + "]已被使用");
            }

            // 验证手机验证码
            var key = dto.getKey();
            var reply = client.verifySmsCode(key);
            if (!reply.getSuccess()) {
                throw new BusinessException(reply.getMessage());
            }
        }

        if (Util.isNotEmpty(data.getMobile())) {
            Redis.deleteKey("ID:" + data.getMobile());
        }

        var key = "User:" + id;
        Redis.setHash(key, "mobile", mobile);
        Redis.set("ID:" + mobile, id.toString());

        // 持久化数据
        data.setMobile(mobile);
        mapper.updateUser(data);
    }

    /**
     * 更新用户Email
     *
     * @param id    用户ID
     * @param email Email
     */
    @Override
    public void updateEmail(Long id, String email) {
        var data = getUserById(id);
        if (Util.isNotEmpty(email) && mapper.keyIsExisted(id, email)) {
            throw new BusinessException("Email[" + email + "]已被使用");
        }

        if (Util.isNotEmpty(data.getEmail())) {
            Redis.deleteKey("ID:" + data.getEmail());
        }

        var key = "User:" + id;
        Redis.setHash(key, "email", email);
        Redis.set("ID:" + email, id.toString());

        data.setEmail(email);
        mapper.updateUser(data);
    }

    /**
     * 更新用户微信号
     *
     * @param dto 微信DTO
     */
    @Override
    public void updateUnionId(WechatDto dto) {
        var code = dto.getCode();
        var wechatAppId = dto.getWeChatAppId();
        var key = "WeChatApp:" + wechatAppId;
        var secret = Redis.get(key, "secret");
        var wechatUser = WechatHelper.getUserInfo(code, wechatAppId, secret);
        if (wechatUser == null) {
            throw new BusinessException("微信授权失败");
        }

        var unionId = wechatUser.getUnionid();
        if (unionId == null || unionId.isEmpty()) {
            throw new BusinessException("未取得微信用户的UnionID");
        }

        var userId = dto.getId();
        var data = getUserById(userId);
        if (Util.isNotEmpty(data.getUnionId())) {
            Redis.deleteKey("ID:" + data.getUnionId());
        }

        key = "User:" + userId;
        Redis.setHash(key, "unionId", unionId);
        Redis.setHash(key, "nickname", wechatUser.getNickname());
        Redis.set("ID:" + unionId, userId.toString());

        data.setNickname(wechatUser.getNickname());
        data.setUnionId(unionId);
        data.setHeadImg(wechatUser.getHeadimgurl());
        Redis.setHash(key, "headImg", wechatUser.getHeadimgurl());

        mapper.updateUser(data);
    }

    /**
     * 更新用户头像
     *
     * @param id      用户ID
     * @param headImg 头像
     */
    @Override
    public void updateHeadImg(Long id, String headImg) {
        var data = getUserById(id);
        data.setHeadImg(headImg);
        mapper.updateUser(data);

        var key = "User:" + id;
        if (Redis.hasKey(key)) {
            Redis.setHash(key, "headImg", headImg);
        }
    }

    /**
     * 更新用户备注
     *
     * @param id     用户ID
     * @param remark 备注
     */
    @Override
    public void updateRemark(Long id, String remark) {
        var data = getUserById(id);
        data.setRemark(remark);
        mapper.updateUser(data);
    }

    /**
     * 修改密码
     *
     * @param dto 密码DTO
     */
    @Override
    public void changePassword(PasswordDto dto) {
        var id = dto.getId();
        var data = getUserById(id);

        var old = dto.getOld();
        if (Util.isEmpty(old) || !old.equals(data.getPassword())) {
            throw new BusinessException("原密码错误,请输入正确的原密码");
        }

        var key = "User:" + id;
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
        var reply = client.verifySmsCode(dto.getKey());
        if (!reply.getSuccess()) {
            return reply;
        }

        // 验证用户
        var mobile = reply.getData().toString();
        var result = authClient.generateCode(new CodeDto(mobile));
        if (!result.getSuccess()) {
            return result;
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
        var code = result.getData().toString();
        var sign = Util.md5(Util.md5(mobile + pw) + code);
        var login = new LoginDto();
        login.setAppId(dto.getAppId());
        login.setTenantId(dto.getTenantId());
        login.setAccount(mobile);
        login.setSignature(sign);

        return authClient.generateToken(login);
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
        var user = getUserById(id);
        var reply = client.verifySmsCode(dto.getKey());
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
