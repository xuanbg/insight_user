package com.insight.base.user.service;

import com.insight.base.user.common.dto.MobileDto;
import com.insight.base.user.common.dto.PasswordDto;
import com.insight.base.user.common.dto.UserVo;
import com.insight.base.user.common.dto.WechatDto;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.user.UserDto;

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
    UserVo getUser(Long id);

    /**
     * 注册用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    Long register(UserDto dto);

    /**
     * 更新用户昵称
     *
     * @param id   用户ID
     * @param name 用户昵称
     */
    void updateName(Long id, String name);

    /**
     * 更新用户手机号
     *
     * @param id  用户ID
     * @param dto 手机验证码DTO
     */
    void updateMobile(Long id, MobileDto dto);

    /**
     * 更新用户Email
     *
     * @param id    用户ID
     * @param email Email
     */
    void updateEmail(Long id, String email);

    /**
     * 更新用户微信号
     *
     * @param dto 微信DTO
     */
    void updateUnionId(WechatDto dto);

    /**
     * 更新用户头像
     *
     * @param id      用户ID
     * @param headImg 头像
     */
    void updateHeadImg(Long id, String headImg);

    /**
     * 更新用户备注
     *
     * @param id     用户ID
     * @param remark 备注
     */
    void updateRemark(Long id, String remark);

    /**
     * 修改密码
     *
     * @param dto 密码DTO
     */
    void changePassword(PasswordDto dto);

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
     */
    void setPayPassword(PasswordDto dto);

    /**
     * 验证支付密码(供服务调用)
     *
     * @param id  用户ID
     * @param key 支付密码(MD5)
     */
    void verifyPayPw(Long id, String key);
}
