package com.insight.base.user.common;

import com.insight.base.user.common.mapper.UserMapper;
import com.insight.utils.Generator;
import com.insight.utils.ReplyHelper;
import com.insight.utils.Util;
import com.insight.utils.pojo.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-04
 * @remark 用户管理核心类
 */
@Component
public class Core {
    private final UserMapper mapper;

    /**
     * 构造方法
     *
     * @param mapper UserMapper
     */
    public Core(UserMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 新增用户
     *
     * @param user     用户DTO
     * @param tenantId 租户ID
     */
    @Transactional
    public void addUser(User user, String tenantId) {
        // 补完ID
        String userId = user.getId();
        if (userId == null || userId.isEmpty()) {
            user.setId(Util.uuid());
        }

        // 生成用户编码
        String code = newUserCode(tenantId);
        user.setCode(code);

        // 补完账号
        String account = user.getAccount();
        if (account == null || account.isEmpty()) {
            user.setAccount(Util.uuid());
        }

        // 补完密码
        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            String pw = Util.md5(tenantId == null ? Util.uuid() : "123456");
            user.setPassword(pw);
        }

        // 补完内置属性
        Boolean isBuiltin = user.getBuiltin();
        if (isBuiltin == null) {
            user.setBuiltin(false);
        }

        // 补完其它属性
        user.setInvalid(false);
        user.setCreatedTime(LocalDateTime.now());
        String creatorId = user.getCreatorId();
        if (creatorId == null || creatorId.isEmpty()) {
            user.setCreator(user.getName());
            user.setCreatorId(user.getId());
        }

        // 持久化数据
        mapper.addUser(user);
        if (tenantId == null || tenantId.isEmpty()) {
            return;
        }

        mapper.addRelation(tenantId, userId);
    }

    /**
     * 用户是否已经存在
     *
     * @param userId  用户ID
     * @param account 登录账号
     * @param mobile  手机号
     * @param email   邮箱
     * @return Reply
     */
    public Reply matchUser(String userId, String account, String mobile, String email) {
        int count = mapper.matchUsers(userId, account);
        if (count > 0) {
            return ReplyHelper.invalidParam("账号[" + account + "]已被使用");
        }

        count = mapper.matchUsers(userId, mobile);
        if (count > 0) {
            return ReplyHelper.invalidParam("手机号[" + mobile + "]已被使用");
        }

        count = mapper.matchUsers(userId, email);
        if (count > 0) {
            return ReplyHelper.invalidParam("Email[" + email + "]已被使用");
        }

        return null;
    }

    /**
     * 获取操作日志列表
     *
     * @param tenantId 租户ID
     * @param business 业务类型
     * @param key      查询关键词
     * @return 操作日志列表
     */
    public List<Log> getLogs(String tenantId, String business, String key) {
        return mapper.getLogs(tenantId, business, key);
    }

    /**
     * 获取操作日志列表
     *
     * @param id 日志ID
     * @return 操作日志列表
     */
    public Log getLog(String id) {
        return mapper.getLog(id);
    }

    /**
     * 记录操作日志
     *
     * @param info     用户关键信息
     * @param type     操作类型
     * @param business 业务名称
     * @param id       业务ID
     * @param content  日志内容
     */
    @Async
    public void writeLog(LoginInfo info, OperateType type, String business, String id, Object content) {
        Log log = new Log();
        log.setId(Util.uuid());
        log.setTenantId(info.getTenantId());
        log.setType(type);
        log.setBusiness(business);
        log.setBusinessId(id);
        log.setContent(content);
        log.setCreator(info.getUserName());
        log.setCreatorId(info.getUserId());
        log.setCreatedTime(LocalDateTime.now());

        mapper.addLog(log);
    }

    /**
     * 获取用户编码
     *
     * @param tenantId 租户ID
     * @return 用户编码
     */
    private String newUserCode(String tenantId) {
        boolean isTenant = tenantId != null;
        String format = isTenant ? "#6" : "IU#8";
        String group = "User" + (isTenant ? ":" + tenantId : "");
        while (true) {
            String code = Generator.newCode(format, group, !isTenant);
            int count = mapper.getUserCount(tenantId, code);
            if (count > 0) {
                continue;
            }

            return code;
        }
    }
}
