package com.insight.base.user.common;

import com.insight.base.user.common.dto.UserDto;
import com.insight.base.user.common.mapper.UserMapper;
import com.insight.utils.Generator;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.Util;
import com.insight.utils.pojo.base.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author 宣炳刚
 * @date 2019-09-04
 * @remark 用户管理核心类
 */
@Component
public class Core {
    private final SnowflakeCreator creator;
    private final UserMapper mapper;

    /**
     * 构造方法
     *
     * @param creator 雪花算法ID生成器
     * @param mapper  UserMapper
     */
    public Core(SnowflakeCreator creator, UserMapper mapper) {
        this.creator = creator;
        this.mapper = mapper;
    }

    /**
     * 新增用户
     *
     * @param user 用户DTO
     */
    @Transactional
    public void addUser(UserDto user) {
        // 补完ID
        Long userId = user.getId();
        if (userId == null) {
            userId = creator.nextId(3);
            user.setId(userId);
        }

        // 补完类型
        Integer type = user.getType();
        if (type == null) {
            user.setType(0);
        }

        // 生成用户编码
        Long tenantId = user.getTenantId();
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
        Long creatorId = user.getCreatorId();
        if (creatorId == null) {
            user.setCreator(user.getName());
            user.setCreatorId(user.getId());
        }

        // 持久化数据
        mapper.addUser(user);
        if (tenantId != null) {
            mapper.addRelation(tenantId, userId);
        }

        var orgId = user.getOrgId();
        if (orgId != null){
            mapper.addOrgMember(userId, orgId);
        }

        var roleIds = user.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()){
            return;
        }

        mapper.addRoleMember(userId, roleIds);
    }

    /**
     * 用户是否已经存在
     *
     * @param userId  用户ID
     * @param account 登录账号
     * @param mobile  手机号
     * @param email   邮箱
     */
    public void matchUser(Long userId, String account, String mobile, String email) {
        int count = mapper.matchUsers(userId, account);
        if (count > 0) {
            throw new BusinessException("账号[" + account + "]已被使用");
        }

        count = mapper.matchUsers(userId, mobile);
        if (count > 0) {
            throw new BusinessException("手机号[" + mobile + "]已被使用");
        }

        count = mapper.matchUsers(userId, email);
        if (count > 0) {
            throw new BusinessException("Email[" + email + "]已被使用");
        }
    }

    /**
     * 获取用户编码
     *
     * @param tenantId 租户ID
     * @return 用户编码
     */
    private String newUserCode(Long tenantId) {
        boolean isTenant = tenantId != null;
        String group = isTenant ? "Base:User:" + tenantId : "Base:User";
        String format = isTenant ? "#6" : "IU#8";
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
