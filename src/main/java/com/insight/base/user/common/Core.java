package com.insight.base.user.common;

import com.insight.base.user.common.dto.UserDto;
import com.insight.base.user.common.dto.UserVo;
import com.insight.base.user.common.mapper.UserMapper;
import com.insight.utils.Generator;
import com.insight.utils.Redis;
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
     * 用户处理逻辑
     *
     * @param user 用户DTO
     */
    @Transactional
    public Long processUser(UserDto user) {
        var data = mapper.getUser(user.getId());
        if (data == null) {
            return mapper.userIsExisted(user.getAccount(), user.getMobile(), user.getEmail()) ? null : addUser(user);
        } else {
            updateUser(user, data);
            return null;
        }
    }

    /**
     * 更新用户信息
     *
     * @param user 用户数据
     * @param data 库中数据
     */
    private void updateUser(UserDto user, UserVo data) {
        var userId = user.getId();
        var mobile = data.getMobile();
        if (!user.mobileEquals(mobile)) {
            if (Util.isNotEmpty(mobile)) {
                Redis.deleteKey("ID:" + mobile);
            }
        }

        if (!user.equals(data)) {
            user.setAccount(data.getAccount());
            user.setEmail(data.getEmail());
            user.setHeadImg(data.getHeadImg());
            user.setRemark(data.getRemark());
            mapper.updateUser(user);
        }

        if (!user.getInvalid().equals(data.getInvalid())) {
            mapper.updateStatus(userId, user.getInvalid());
            String key = "User:" + userId;
            if (Redis.hasKey(key)) {
                Redis.setHash(key, "invalid", user.getInvalid());
            }
        }
    }

    /**
     * 新增用户
     *
     * @param user 用户数据
     * @return 用户ID
     */
    private Long addUser(UserDto user) {
        var userId = user.getId();
        var tenantId = user.getTenantId();
        if (userId == null) {
            userId = creator.nextId(3);
            user.setId(userId);
        }

        if (user.getType() == null) {
            user.setType(0);
        }

        if (Util.isEmpty(user.getCode())) {
            String code = newUserCode(tenantId);
            user.setCode(code);
        }

        if (Util.isEmpty(user.getAccount())) {
            user.setAccount(Util.uuid());
        }

        if (Util.isEmpty(user.getPassword())) {
            String pw = Util.md5(tenantId == null ? Util.uuid() : "123456");
            user.setPassword(pw);
        }

        if (user.getBuiltin() == null) {
            user.setBuiltin(false);
        }

        if (user.getCreatorId() == null) {
            user.setCreator(user.getName());
            user.setCreatorId(user.getId());
        }

        user.setCreatedTime(LocalDateTime.now());
        mapper.addUser(user);

        if (tenantId != null) {
            mapper.addRelation(tenantId, userId);
        }

        var orgId = user.getOrgId();
        if (orgId != null) {
            mapper.addOrgMember(userId, orgId);
        }

        var roleIds = user.getRoleIds();
        if (Util.isNotEmpty(roleIds)) {
            mapper.addRoleMember(userId, roleIds);
        }

        return user.getId();
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
        if (mapper.keyIsExisted(userId, account)) {
            throw new BusinessException("账号[" + account + "]已被使用");
        }

        if (Util.isNotEmpty(mobile) && mapper.keyIsExisted(userId, mobile)) {
            throw new BusinessException("手机号[" + mobile + "]已被使用");
        }

        if (Util.isNotEmpty(email) && mapper.keyIsExisted(userId, email)) {
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
            if (mapper.codeIsExisted(tenantId, code)) {
                continue;
            }

            return code;
        }
    }
}
