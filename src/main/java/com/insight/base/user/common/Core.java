package com.insight.base.user.common;

import com.insight.base.user.common.mapper.UserMapper;
import com.insight.util.Generator;
import com.insight.util.Util;
import com.insight.util.pojo.Log;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.OperateType;
import com.insight.util.pojo.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.insight.util.Generator.uuid;

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
     * @param user 用户DTO
     */
    @Transactional
    public void addUser(User user, String tenantId) {
        String userId = user.getId();
        if (userId == null || userId.isEmpty()) {
            user.setId(Generator.uuid());
        }

        String password = user.getPassword();
        if (password == null || password.isEmpty()){
            user.setPassword(Util.md5("123456"));
        }

        Boolean isBuiltin = user.getBuiltin();
        if (isBuiltin == null) {
            user.setBuiltin(false);
        }

        Boolean isInvalid = user.getInvalid();
        if (isInvalid == null) {
            user.setInvalid(false);
        }

        user.setCreatedTime(LocalDateTime.now());
        String creatorId = user.getCreatorId();
        if (creatorId == null || creatorId.isEmpty()) {
            user.setCreator(user.getName());
            user.setCreatorId(user.getId());
        }

        mapper.addUser(user);
        if (tenantId == null || tenantId.isEmpty()) {
            return;
        }

        mapper.addRelation(tenantId, userId);
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
        log.setId(uuid());
        log.setTenantId(info.getTenantId());
        log.setType(type);
        log.setBusiness(business);
        log.setBusinessId(id);
        log.setContent(content);
        log.setDeptId(info.getDeptId());
        log.setCreator(info.getUserName());
        log.setCreatorId(info.getUserId());
        log.setCreatedTime(LocalDateTime.now());

        mapper.addLog(log);
    }
}
