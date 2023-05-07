package com.insight.base.user.manage;

import com.insight.base.user.common.client.LogServiceClient;
import com.insight.base.user.common.dto.FuncPermitDto;
import com.insight.base.user.common.dto.UserVo;
import com.insight.utils.Json;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.user.UserDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户管理服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/user/manage")
public class ManageController {
    private final LogServiceClient client;
    private final ManageService service;

    /**
     * 构造方法
     *
     * @param client Feign客户端
     * @param service 自动注入的Service
     */
    public ManageController(LogServiceClient client, ManageService service) {
        this.client = client;
        this.service = service;
    }

    /**
     * 查询用户列表
     *
     * @param info   用户关键信息
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/users")
    public Reply getUsers(@RequestHeader("loginInfo") String info, Search search) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        search.setTenantId(loginInfo.getTenantId());
        search.setOwnerId(loginInfo.getOrgId());
        return service.getUsers(search);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return Reply
     */
    @GetMapping("/v1.0/users/{id}")
    public UserVo getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    /**
     * 获取用户功能授权
     *
     * @param id 用户ID
     * @return Reply
     */
    @GetMapping("/v1.0/users/{id}/functions")
    public List<FuncPermitDto> getUserPermit(@PathVariable Long id) {
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
    public Long newUser(@RequestHeader("loginInfo") String info, @Valid @RequestBody UserDto dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.newUser(loginInfo, dto);
    }

    /**
     * 编辑用户
     *
     * @param info 用户关键信息
     * @param dto  用户DTO
     */
    @PutMapping("/v1.0/users/{id}")
    public void editUser(@RequestHeader("loginInfo") String info, @PathVariable Long id, @Valid @RequestBody UserDto dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setId(id);

        service.editUser(loginInfo, dto);
    }

    /**
     * 删除用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @DeleteMapping("/v1.0/users/{id}")
    public void deleteUser(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.deleteUser(loginInfo, id);
    }

    /**
     * 禁用用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @PutMapping("/v1.0/users/{id}/disable")
    public void disableUser(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.changeUserStatus(loginInfo, id, true);
    }

    /**
     * 启用用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @PutMapping("/v1.0/users/{id}/enable")
    public void enableUser(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.changeUserStatus(loginInfo, id, false);
    }

    /**
     * 重置用户密码
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @PutMapping("/v1.0/users/{id}/password")
    public void resetPassword(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.resetPassword(loginInfo, id);
    }

    /**
     * 获取可邀请用户列表
     *
     * @param info   用户关键信息
     * @param search 查询关键词
     * @return Reply
     */
    @GetMapping("/v1.0/users/others")
    public Reply getInviteUsers(@RequestHeader("loginInfo") String info, Search search) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        search.setTenantId(loginInfo.getTenantId());
        return service.getInviteUsers(search);
    }

    /**
     * 邀请用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @PostMapping("/v1.0/users/{id}/relation")
    public void inviteUser(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.inviteUser(loginInfo, id);
    }

    /**
     * 清退用户
     *
     * @param info 用户关键信息
     * @param id   用户ID
     */
    @DeleteMapping("/v1.0/users/{id}/relation")
    public void removeUser(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.removeUser(loginInfo, id);
    }

    /**
     * 查询符合条件的用户数量
     *
     * @param keyword 查询条件
     * @return 用户数量
     */
    @GetMapping("/v1.0/users/count")
    public int getUserCount(@RequestParam String keyword) {
        return service.getUserCount(keyword);
    }

    /**
     * 查询日志
     *
     * @param loginInfo 用户登录信息
     * @param search    查询条件
     * @return 日志集合
     */
    @GetMapping("/v1.0/users/{id}/logs")
    public Reply getAirportLogs(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, Search search) {
        var info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return client.getLogs(id, "UserManage", search.getKeyword());
    }

    /**
     * 获取日志
     *
     * @param loginInfo 用户登录信息
     * @param id        日志ID
     * @return 日志VO
     */
    @GetMapping("/v1.0/users/logs/{id}")
    public Reply getAirportLog(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        var info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return client.getLog(id);
    }
}
