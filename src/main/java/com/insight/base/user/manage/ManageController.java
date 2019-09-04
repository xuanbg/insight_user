package com.insight.base.user.manage;

import com.insight.util.pojo.Reply;
import com.insight.util.pojo.User;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户管理服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/user/manage")
public class ManageController {
    private final ManageService service;

    /**
     * 构造方法
     *
     * @param service 自动注入的Service
     */
    public ManageController(ManageService service) {
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
    public Reply getUsers(String key, int page, int size){
        return service.getUsers(key, page, size);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    @GetMapping("/v1.0/users/{id}")
    public Reply getUser(String id){
        return service.getUser(id);
    }

    /**
     * 新增用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @PostMapping("/v1.0/users")
    public Reply newUser(User dto){
        return service.newUser(dto);
    }

    /**
     * 编辑用户
     *
     * @param dto 用户DTO
     * @return Reply
     */
    @PutMapping("/v1.0/users")
    public Reply editUser(User dto){
        return service.editUser(dto);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/users/{id}")
    public Reply deleteUser(String id){
        return service.deleteUser(id);
    }

    /**
     * 改变用户禁用/启用状态
     *
     * @param id     用户ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @PutMapping("/v1.0/users/{id}/status")
    public Reply changeUserStatus(String id, boolean status){
        return service.changeUserStatus(id, status);
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return Reply
     */
    @PutMapping("/v1.0/users/{id}/password")
    public Reply resetPassword(String id){
        return service.resetPassword(id);
    }

    /**
     * 邀请用户
     *
     * @param id 用户ID
     * @return Reply
     */
    @PostMapping("/v1.0/users/{id}/relation")
    public Reply inviteUser(String id){
        return service.inviteUser(id);
    }
}
