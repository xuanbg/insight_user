package com.insight.base.user.service;

import com.insight.base.user.common.dto.MobileDto;
import com.insight.base.user.common.dto.PasswordDto;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.User;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户服务接口
 */
public interface UserService {

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    Reply getUser(String id);

    /**
     * 注册用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    Reply register(User dto);

    /**
     * 更新用户昵称
     *
     * @param id   用户ID
     * @param name 用户昵称
     * @return Reply
     */
    Reply updateName(String id, String name);

    /**
     * 更新用户手机号
     *
     * @param id  用户ID
     * @param dto 手机验证码DTO
     * @return Reply
     */
    Reply updateMobile(String id, MobileDto dto);

    /**
     * 更新用户Email
     *
     * @param id    用户ID
     * @param email Email
     * @return Reply
     */
    Reply updateEmail(String id, String email);

    /**
     * 更新用户头像
     *
     * @param id      用户ID
     * @param headImg 头像
     * @return Reply
     */
    Reply updateHeadImg(String id, String headImg);

    /**
     * 更新用户备注
     *
     * @param id     用户ID
     * @param remark 备注
     * @return Reply
     */
    Reply updateRemark(String id, String remark);

    /**
     * 修改密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    Reply changePassword(PasswordDto dto);

    /**
     * 重置密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    Reply resetPassword(PasswordDto dto);

    /**
     * 设置支付密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    Reply setPayPassword(PasswordDto dto);

    /**
     * 验证支付密码(供服务调用)
     *
     * @param id  用户ID
     * @param key 支付密码(MD5)
     * @return Reply
     */
    Reply verifyPayPw(String id, String key);
}
