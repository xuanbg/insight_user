package com.insight.base.user.common;

import com.insight.base.user.common.mapper.UserMapper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.Util;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.user.UserDto;
import com.insight.utils.redis.Generator;
import com.insight.utils.redis.HashOps;
import com.insight.utils.redis.KeyOps;
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
        if (mapper.userIsExisted(user)) {
            return null;
        }

        var data = mapper.getUser(user.getId());
        if (data == null) {
            if (user.getId() == null) {
                user.setId(creator.nextId(3));
            }

            if (user.getType() == null) {
                user.setType(0);
            }

            if (Util.isEmpty(user.getCode())) {
                String code = newUserCode(user.getTenantId());
                user.setCode(code);
            }

            if (Util.isEmpty(user.getAccount())) {
                user.setAccount(Util.uuid());
            }

            if (Util.isEmpty(user.getPassword())) {
                String pw = Util.md5(user.getType() > 0 ? "123456" : Util.uuid());
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

            if (user.getTenantId() != null) {
                mapper.addRelation(user.getTenantId(), user.getId());
            }

            var orgId = user.getOrgId();
            if (orgId != null) {
                mapper.addOrgMember(user.getId(), orgId);
            }

            var roleIds = user.getRoleIds();
            if (Util.isNotEmpty(roleIds)) {
                mapper.addRoleMember(user.getId(), roleIds);
            }
        } else {
            // 清理失效缓存数据
            var account = data.getAccount();
            if (!account.equals(user.getAccount())) {
                KeyOps.delete("ID:" + account);
            }

            var mobile = data.getMobile();
            if (Util.isNotEmpty(mobile) && !mobile.equals(user.getMobile())) {
                KeyOps.delete("ID:" + mobile);
            }

            String email = data.getEmail();
            if (Util.isNotEmpty(email) && !email.equals(user.getEmail())) {
                KeyOps.delete("ID:" + email);
            }

            // 更新缓存数据
            var key = "User:" + user.getId();
            if (Util.isEmpty(user.getName())) {
                user.setName(data.getName());
            } else if (KeyOps.hasKey(key)) {
                HashOps.put(key, "name", user.getName());
            }

            if (Util.isEmpty(user.getAccount())) {
                user.setAccount(data.getAccount());
            } else if (KeyOps.hasKey(key)) {
                HashOps.put(key, "account", user.getAccount());
            }

            if (Util.isEmpty(user.getMobile())) {
                user.setMobile(data.getMobile());
            } else if (KeyOps.hasKey(key)) {
                HashOps.put(key, "mobile", user.getMobile());
            }

            if (Util.isEmpty(user.getEmail())) {
                user.setEmail(data.getEmail());
            } else if (KeyOps.hasKey(key)) {
                HashOps.put(key, "email", user.getEmail());
            }

            if (Util.isEmpty(user.getNickname())) {
                user.setNickname(data.getNickname());
            } else if (KeyOps.hasKey(key)) {
                HashOps.put(key, "nickname", user.getNickname());
            }

            if (user.getUnionId() == null) {
                user.setUnionId(data.getUnionId());
            } else if (KeyOps.hasKey(key)) {
                HashOps.put(key, "unionId", user.getUnionId());
            }

            if (Util.isEmpty(user.getHeadImg())) {
                user.setHeadImg(data.getHeadImg());
            } else if (KeyOps.hasKey(key)) {
                HashOps.put(key, "headImg", user.getHeadImg());
            }

            if (Util.isEmpty(user.getRemark())) {
                user.setRemark(data.getRemark());
            } else if (KeyOps.hasKey(key)) {
                HashOps.put(key, "remark", user.getRemark());
            }

            if (user.getInvalid() == null) {
                user.setInvalid(data.getInvalid());
            } else if (KeyOps.hasKey(key)) {
                HashOps.put(key, "invalid", user.getInvalid());
            }
            mapper.updateUser(user);
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
