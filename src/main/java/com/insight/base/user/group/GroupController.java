package com.insight.base.user.group;

import com.insight.base.user.common.dto.GroupDto;
import com.insight.util.Json;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.Reply;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-01
 * @remark 用户组管理服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/user")
public class GroupController {
    private final GroupService service;

    /**
     * 构造方法
     *
     * @param service 自动注入的Service
     */
    public GroupController(GroupService service) {
        this.service = service;
    }

    /**
     * 查询用户组列表
     *
     * @param info    用户关键信息
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/groups")
    public Reply getGroups(@RequestHeader("loginInfo") String info, @RequestParam(required = false) String keyword,
                           @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getGroups(loginInfo.getTenantId(), keyword, page, size);
    }

    /**
     * 获取用户组详情
     *
     * @param id 用户组ID
     * @return Reply
     */
    @GetMapping("/v1.0/groups/{id}")
    public Reply getGroup(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        return service.getGroup(id);
    }

    /**
     * 新增用户组
     *
     * @param info 用户关键信息
     * @param dto  用户组DTO
     * @return Reply
     */
    @PostMapping("/v1.0/groups")
    public Reply newGroup(@RequestHeader("loginInfo") String info, @Valid @RequestBody GroupDto dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.newGroup(loginInfo, dto);
    }

    /**
     * 编辑用户组
     *
     * @param info 用户关键信息
     * @param dto  用户组DTO
     * @return Reply
     */
    @PutMapping("/v1.0/groups")
    public Reply editGroup(@RequestHeader("loginInfo") String info, @Valid @RequestBody GroupDto dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.editGroup(loginInfo, dto);
    }

    /**
     * 删除用户组
     *
     * @param info 用户关键信息
     * @param id   用户组ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/groups")
    public Reply deleteGroup(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.deleteGroup(loginInfo, id);
    }

    /**
     * 查询用户组成员
     *
     * @param id      用户组ID
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/groups/{id}/members")
    public Reply getMembers(@PathVariable String id, @RequestParam(required = false) String keyword,
                            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getMembers(id, keyword, page, size);
    }

    /**
     * 添加用户组成员
     *
     * @param info    用户关键信息
     * @param id      用户组ID
     * @param userIds 用户ID集合
     * @return Reply
     */
    @PostMapping("/v1.0/groups/{id}/members")
    public Reply addMembers(@RequestHeader("loginInfo") String info, @PathVariable String id, @RequestBody List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return ReplyHelper.invalidParam("请选择需要添加的成员");
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.addMembers(loginInfo, id, userIds);
    }

    /**
     * 移除用户组成员
     *
     * @param info    用户关键信息
     * @param id      用户组ID
     * @param userIds 用户ID集合
     * @return Reply
     */
    @DeleteMapping("/v1.0/groups/{id}/members")
    public Reply removeMembers(@RequestHeader("loginInfo") String info, @PathVariable String id, @RequestBody List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return ReplyHelper.invalidParam("请选择需要移除的成员");
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.removeMembers(loginInfo, id, userIds);
    }

    /**
     * 获取日志列表
     *
     * @param info    用户关键信息
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/groups/logs")
    public Reply getGroupLogs(@RequestHeader("loginInfo") String info, @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getGroupLogs(loginInfo.getTenantId(), keyword, page, size);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @GetMapping("/v1.0/groups/logs/{id}")
    public Reply getGroupLog(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        return service.getGroupLog(id);
    }
}
