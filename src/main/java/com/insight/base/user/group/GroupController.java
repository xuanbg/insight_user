package com.insight.base.user.group;

import com.insight.base.user.common.client.LogClient;
import com.insight.base.user.common.client.LogServiceClient;
import com.insight.base.user.common.dto.GroupDto;
import com.insight.base.user.common.dto.OperateType;
import com.insight.base.user.common.dto.UserVo;
import com.insight.utils.Json;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户组管理服务控制器
 */
@RestController
@RequestMapping("/base/user")
public class GroupController {
    private static final String BUSINESS = "UserGroup";
    private final LogServiceClient client;
    private final GroupService service;

    /**
     * 构造方法
     *
     * @param client  Feign客户端
     * @param service 自动注入的Service
     */
    public GroupController(LogServiceClient client, GroupService service) {
        this.client = client;
        this.service = service;
    }

    /**
     * 查询用户组列表
     *
     * @param loginInfo   用户关键信息
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/groups")
    public Reply getGroups(@RequestHeader("loginInfo") String loginInfo, Search search) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        search.setTenantId(info.getTenantId());
        return service.getGroups(search);
    }

    /**
     * 获取用户组详情
     *
     * @param id 用户组ID
     * @return Reply
     */
    @GetMapping("/v1.0/groups/{id}")
    public GroupDto getGroup(@PathVariable Long id) {
        return service.getGroup(id);
    }

    /**
     * 新增用户组
     *
     * @param loginInfo 用户关键信息
     * @param dto  用户组DTO
     * @return Reply
     */
    @PostMapping("/v1.0/groups")
    public Long newGroup(@RequestHeader("loginInfo") String loginInfo, @Valid @RequestBody GroupDto dto) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        var id = service.newGroup(info, dto);
        LogClient.writeLog(info, BUSINESS, OperateType.NEW, id, dto);
        return id;
    }

    /**
     * 编辑用户组
     *
     * @param loginInfo 用户关键信息
     * @param id   用户组ID
     * @param dto  用户组DTO
     */
    @PutMapping("/v1.0/groups/{id}")
    public void editGroup(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @Valid @RequestBody GroupDto dto) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        dto.setId(id);

        service.editGroup(info, dto);
        LogClient.writeLog(info, BUSINESS, OperateType.EDIT, id, dto);
    }

    /**
     * 删除用户组
     *
     * @param loginInfo 用户关键信息
     * @param id   用户组ID
     */
    @DeleteMapping("/v1.0/groups/{id}")
    public void deleteGroup(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.deleteGroup(info, id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, null);
    }

    /**
     * 查询用户组成员
     *
     * @param id     用户组ID
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/groups/{id}/members")
    public Reply getMembers(@PathVariable Long id, Search search) {
        search.setId(id);
        return service.getMembers(search);
    }

    /**
     * 查询用户组可用用户列表
     *
     * @param id 用户组ID
     * @return Reply
     */
    @GetMapping("/v1.0/groups/{id}/others")
    public List<UserVo> getOthers(@PathVariable Long id) {
        return service.getOthers(id);
    }

    /**
     * 添加用户组成员
     *
     * @param loginInfo    用户关键信息
     * @param id      用户组ID
     * @param userIds 用户ID集合
     */
    @PostMapping("/v1.0/groups/{id}/members")
    public void addMembers(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @RequestBody List<Long> userIds) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.addMembers(info, id, userIds);
        LogClient.writeLog(info, BUSINESS, OperateType.NEW, id, userIds);
    }

    /**
     * 移除用户组成员
     *
     * @param loginInfo    用户关键信息
     * @param id      用户组ID
     * @param userIds 用户ID集合
     */
    @DeleteMapping("/v1.0/groups/{id}/members")
    public void removeMembers(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @RequestBody List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException("请选择需要移除的成员");
        }

        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        service.removeMembers(info, id, userIds);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, userIds);
    }

    /**
     * 查询日志
     *
     * @param loginInfo 用户登录信息
     * @param search    查询条件
     * @return 日志集合
     */
    @GetMapping("/v1.0/groups/{id}/logs")
    public Reply getAirportLogs(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, Search search) {
        var info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return client.getLogs(id, "UserGroup", search.getKeyword());
    }

    /**
     * 获取日志
     *
     * @param loginInfo 用户登录信息
     * @param id        日志ID
     * @return 日志VO
     */
    @GetMapping("/v1.0/groups/logs/{id}")
    public Reply getAirportLog(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        var info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return client.getLog(id);
    }
}
