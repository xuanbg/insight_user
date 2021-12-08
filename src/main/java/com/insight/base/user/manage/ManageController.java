package com.insight.base.user.manage;

import com.insight.base.user.common.dto.PasswordDto;
import com.insight.base.user.common.dto.UserDto;
import com.insight.utils.Json;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.Reply;
import com.insight.utils.pojo.SearchDto;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
     * @param info   用户关键信息
     * @param all    是否查询全部用户
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/users")
    public Reply getUsers(@RequestHeader("loginInfo") String info, @RequestParam(defaultValue = "false") boolean all, SearchDto search) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getUsers(loginInfo.getTenantId(), all, search);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    @GetMapping("/v1.0/users/{id}")
    public Reply getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    /**
     * 获取用户功能授权
     *
     * @param id 用户ID
     * @return Reply
     */
    @GetMapping("/v1.0/users/{id}/functions")
    public Reply getUserPermit(@PathVariable Long id) {
        return service.getUserPermit(id);
    }

    /**
     * 新增用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     * @return Reply
     */
    @PostMapping("/v1.0/users")
    public Reply newUser(@RequestHeader("loginInfo") String info, @Valid @RequestBody UserDto dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.newUser(loginInfo, dto);
    }

    /**
     * 编辑用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     * @return Reply
     */
    @PutMapping("/v1.0/users/{id}")
    public Reply editUser(@RequestHeader("loginInfo") String info, @PathVariable Long id, @Valid @RequestBody UserDto dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setId(id);

        return service.editUser(loginInfo, dto);
    }

    /**
     * 删除用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/users/{id}")
    public Reply deleteUser(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.deleteUser(loginInfo, id);
    }

    /**
     * 禁用用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    @PutMapping("/v1.0/users/{id}/disable")
    public Reply disableUser(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.changeUserStatus(loginInfo, id, true);
    }

    /**
     * 启用用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    @PutMapping("/v1.0/users/{id}/enable")
    public Reply enableUser(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.changeUserStatus(loginInfo, id, false);
    }

    /**
     * 重置用户密码
     *
     * @param info 用户关键信息
     * @param dto  密码DTO
     * @return Reply
     */
    @PutMapping("/v1.0/users/{id}/password")
    public Reply resetPassword(@RequestHeader("loginInfo") String info, @PathVariable Long id, @RequestBody PasswordDto dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setId(id);

        return service.resetPassword(loginInfo, dto);
    }

    /**
     * 获取可邀请用户列表
     *
     * @param info    用户关键信息
     * @param keyword 查询关键词
     * @return Reply
     */
    @GetMapping("/v1.0/users/others")
    public Reply getInviteUsers(@RequestHeader("loginInfo") String info, @RequestParam String keyword) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getInviteUsers(loginInfo, keyword);
    }

    /**
     * 邀请用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    @PostMapping("/v1.0/users/{id}/relation")
    public Reply inviteUser(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.inviteUser(loginInfo, id);
    }

    /**
     * 清退用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/users/{id}/relation")
    public Reply removeUser(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.removeUser(loginInfo, id);
    }

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/users/logs")
    public Reply getUserLogs(SearchDto search) {
        return service.getUserLogs(search);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @GetMapping("/v1.0/users/logs/{id}")
    Reply getUserLog(@PathVariable Long id) {
        return service.getUserLog(id);
    }
}
