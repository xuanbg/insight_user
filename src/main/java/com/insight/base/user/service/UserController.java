package com.insight.base.user.service;

import com.insight.base.user.common.dto.PasswordDto;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.User;
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
     * 查询用户列表
     *
     * @param key  查询关键词
     * @param page 分页页码
     * @param size 每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/users")
    public Reply getUsers(String key, int page, int size) {
        return service.getUsers(key, page, size);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    @GetMapping("/v1.0/users/{id}")
    public Reply getUser(@PathVariable String id) {
        return service.getUser(id);
    }

    /**
     * 注册用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @PostMapping("/v1.0/users")
    public Reply register(User dto) {
        return service.register(dto);
    }

    /**
     * 更新用户信息
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @PutMapping("/v1.0/users")
    public Reply updateUser(User dto) {
        return service.updateUser(dto);
    }

    /**
     * 修改密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    @PutMapping("/v1.0/users/password")
    public Reply changePassword(PasswordDto dto) {
        return service.changePassword(dto);
    }

    /**
     * 重置密码
     *
     * @param dto 密码DTO
     * @return Reply
     */
    @PostMapping("/v1.0/users/password")
    public Reply resetPassword(PasswordDto dto) {
        return service.resetPassword(dto);
    }

    /**
     * 设置支付密码
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @PostMapping("/v1.0/users/password/pay")
    public Reply setPayPassword(User dto) {
        return service.setPayPassword(dto);
    }

    /**
     * 验证支付密码(供服务调用)
     *
     * @param id  用户ID
     * @param key 支付密码(MD5)
     * @return Reply
     */
    @GetMapping("/v1.0/users/{id}/password/pay?key={key}")
    public Reply verifyPayPw(@PathVariable String id, @RequestParam String key) {
        return service.verifyPayPw(id, key);
    }
}
