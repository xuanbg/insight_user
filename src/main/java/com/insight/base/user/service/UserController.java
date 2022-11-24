package com.insight.base.user.service;

import com.insight.base.user.common.dto.MobileDto;
import com.insight.base.user.common.dto.PasswordDto;
import com.insight.base.user.common.dto.UserDto;
import com.insight.utils.Json;
import com.insight.utils.ReplyHelper;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/user")
public class UserController {
    private final UserService service;

    /**
     * 构造方法
     *
     * @param service 自动注入的Service
     */
    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * 获取当前用户详情
     *
     * @param info 用户关键信息
     * @return Reply
     */
    @GetMapping("/v1.0/users/myself")
    public Reply getUser(@RequestHeader("loginInfo") String info) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getUser(loginInfo.getUserId());
    }

    /**
     * 注册用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @PostMapping("/v1.0/users")
    public Reply register(@RequestBody UserDto dto) {
        return service.register(dto);
    }

    /**
     * 更新用户昵称
     *
     * @param info 用户关键信息
     * @param name 昵称
     * @return Reply
     */
    @PutMapping("/v1.0/users/name")
    public Reply updateName(@RequestHeader("loginInfo") String info, @RequestBody String name) {
        if (name == null || name.isEmpty()) {
            return ReplyHelper.invalidParam("昵称不能为空");
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.updateName(loginInfo.getUserId(), name);
    }

    /**
     * 更新用户手机号
     *
     * @param info 用户关键信息
     * @param dto  手机验证码DTO
     * @return Reply
     */
    @PutMapping("/v1.0/users/mobile")
    public Reply updateMobile(@RequestHeader("loginInfo") String info, @RequestBody MobileDto dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.updateMobile(loginInfo.getUserId(), dto);
    }

    /**
     * 更新用户Email
     *
     * @param info  用户关键信息
     * @param email Email
     * @return Reply
     */
    @PutMapping("/v1.0/users/email")
    public Reply updateEmail(@RequestHeader("loginInfo") String info, @RequestBody String email) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.updateEmail(loginInfo.getUserId(), email);
    }

    /**
     * 更新用户头像
     *
     * @param info    用户关键信息
     * @param headImg 头像
     * @return Reply
     */
    @PutMapping("/v1.0/users/head")
    public Reply updateHeadImg(@RequestHeader("loginInfo") String info, @RequestBody String headImg) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.updateHeadImg(loginInfo.getUserId(), headImg);
    }

    /**
     * 更新用户备注
     *
     * @param info   用户关键信息
     * @param remark 备注
     * @return Reply
     */
    @PutMapping("/v1.0/users/remark")
    public Reply updateRemark(@RequestHeader("loginInfo") String info, @RequestBody String remark) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.updateRemark(loginInfo.getUserId(), remark);
    }

    /**
     * 修改密码
     *
     * @param info 用户关键信息
     * @param dto  密码DTO
     * @return Reply
     */
    @PutMapping("/v1.0/users/password")
    public Reply changePassword(@RequestHeader("loginInfo") String info, @RequestBody PasswordDto dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setId(loginInfo.getUserId());

        return service.changePassword(dto);
    }

    /**
     * 重置密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    @PostMapping("/v1.0/users/password")
    public Reply resetPassword(@RequestBody PasswordDto dto) {
        return service.resetPassword(dto);
    }

    /**
     * 设置支付密码
     *
     * @param info 用户关键信息
     * @param dto  密码DTO
     * @return Reply
     */
    @PostMapping("/v1.0/users/password/pay")
    public Reply setPayPassword(@RequestHeader("loginInfo") String info, @RequestBody PasswordDto dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setId(loginInfo.getUserId());

        return service.setPayPassword(dto);
    }

    /**
     * 验证支付密码(供服务调用)
     *
     * @param info 用户关键信息
     * @param key  支付密码(MD5)
     * @return Reply
     */
    @GetMapping("/v1.0/users/password/pay?key={key}")
    public Reply verifyPayPw(@RequestHeader("loginInfo") String info, @RequestParam String key) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.verifyPayPw(loginInfo.getUserId(), key);
    }
}
