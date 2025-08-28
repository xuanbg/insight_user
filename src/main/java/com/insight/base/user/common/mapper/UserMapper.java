package com.insight.base.user.common.mapper;

import com.insight.base.user.common.dto.FuncPermitDto;
import com.insight.base.user.common.dto.UserVo;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.user.User;
import com.insight.utils.pojo.user.UserDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-09-04
 * @remark 用户DTO
 */
@Mapper
public interface UserMapper {

    /**
     * 获取用户列表
     *
     * @param search 查询实体类
     * @return 用户列表
     */
    @Select("""
            <script>
            select u.id, group_concat(t.name) as tenant, u.type, u.name, u.account, u.mobile, u.email, r.role_name, r.role_id,
                   u.invalid, u.creator, u.created_time
            from ibu_user u
              <if test = 'longSet != null and longSet.size() > 0'>
              join ibo_organize_member m on m.user_id = u.id and m.post_id in
                (<foreach collection = "longSet" item = "item" index = "index" separator = ",">#{item}</foreach>)</if>
              left join ibt_tenant_user a on a.user_id = u.id
              left join ibt_tenant t on t.id = a.tenant_id
              left join (select m.member_id, group_concat(r.name) as role_name, group_concat(r.id) as role_id
                         from ibr_role r
                           join ibr_role_member m on m.role_id = r.id
                         group by m.member_id) r on r.member_id = u.id
            <where>
              <if test = 'tenantId != null'>and a.tenant_id = #{tenantId}</if>
              <if test = 'invalid != null'>and u.invalid = #{invalid}</if>
              <if test = 'keyword != null'>and (u.id = #{keyword} or u.code = #{keyword} or u.account = #{keyword}
              or u.mobile = #{keyword} or u.name like concat('%',#{keyword},'%'))</if>
            </where>
            group by u.id
            </script>
            """)
    List<UserVo> getUsers(Search search);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @Select("select * from ibu_user where id = #{id};")
    User getUser(Long id);

    /**
     * 获取用户功能授权
     *
     * @param id 用户ID
     * @return 功能授权集合
     */
    @Select("""
            select * from (select distinct a.id, null as parent_id, a.`index`, 0 as type, a.`name`, null as remark, null as permit
            from ibv_user_roles r
              join ibr_role_permit p on p.role_id = r.role_id
              join ibs_function f on f.id = p.function_id
              join ibs_navigator m on m.id = f.nav_id
              join ibs_navigator g on g.id = m.parent_id
              join ibs_application a on a.id = g.app_id
            where r.user_id = #{id} union
            select distinct g.id, g.app_id as parent_id, g.`index`, g.type, g.`name`, null as remark, null as permit
            from ibv_user_roles r
              join ibr_role_permit p on p.role_id = r.role_id
              join ibs_function f on f.id = p.function_id
              join ibs_navigator m on m.id = f.nav_id
              join ibs_navigator g on g.id = m.parent_id
            where r.user_id = #{id} union
            select distinct m.id, m.parent_id, m.`index`, m.type, m.`name`, null as remark, null as permit
            from ibv_user_roles r
              join ibr_role_permit p on p.role_id = r.role_id
              join ibs_function f on f.id = p.function_id
              join ibs_navigator m on m.id = f.nav_id
            where r.user_id = #{id} union
            select f.id, f.nav_id as parent_id, f.`index`, min(p.permit) + 3 as type, f.`name`,
            if(min(p.permit) = 0, '禁止', '允许') as remark, min(p.permit) as permit
            from ibv_user_roles r
              join ibr_role_permit p on p.role_id = r.role_id
              join ibs_function f on f.id = p.function_id
            where r.user_id = #{id} group by f.id) t order by t.`index`;
            """)
    List<FuncPermitDto> getUserPermit(Long id);

    /**
     * 匹配关键词的用户是否存在
     *
     * @param user 用户DTO
     * @return 是否存在
     */
    @Select("""
            select count(*) from ibu_user
            where id != #{id} and invalid = 0
            and (account = #{account} or mobile = #{account} or email = #{account}
              or account = #{mobile} or mobile = #{mobile} or email = #{mobile}
              or account = #{email} or mobile = #{email} or email = #{email});
            """)
    Boolean userIsExisted(UserDto user);

    /**
     * 新增用户
     *
     * @param user 用户DTO
     */
    @Insert("""
            insert ibu_user(id, `type`, code, name, account, mobile, email, union_id, password, head_img, remark, builtin, creator, creator_id, created_time)
            values (#{id}, #{type}, #{code}, #{name}, #{account}, #{mobile}, #{email}, #{unionId}, #{password}, #{headImg}, #{remark}, #{builtin},
            #{creator}, #{creatorId}, #{createdTime});
            """)
    void addUser(UserDto user);

    /**
     * 匹配关键词的用户是否存在
     *
     * @param id  用户ID
     * @param key 查询关键词
     * @return 是否存在
     */
    @Select("select count(*) from ibu_user where (account = #{key} or mobile = #{key} or email = #{key}) and id != #{id};")
    Boolean keyIsExisted(@Param("id") Long id, @Param("key") String key);

    /**
     * 获取指定租户下指定编码的用户数量
     *
     * @param tenantId 租户ID
     * @param code     用户编码
     * @return 用户数量
     */
    @Select("""
            <script>
            select count(*)
            from ibu_user u
              <if test = 'tenantId != null'>
              join ibt_tenant_user r on r.user_id = u.id
                and r.tenant_id = #{tenantId}
              </if>
            where u.code = #{code};
            </script>
            """)
    Boolean codeIsExisted(@Param("tenantId") Long tenantId, @Param("code") String code);

    /**
     * 匹配租户下的用户数
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 用户数
     */
    @Select("""
            select count(*)
            from ibt_tenant_user
            where tenant_id = #{tenantId}
              and user_id = #{userId}
              and invalid = 0;
            """)
    int matchRelation(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 更新用户
     *
     * @param user 用户DTO
     */
    @Update("""
            update ibu_user set
              name = #{name},
              account = #{account},
              mobile = #{mobile},
              email = #{email},
              nickname = #{nickname},
              union_id = #{unionId},
              head_img = #{headImg},
              remark = #{remark},
              invalid = #{invalid}
            where id = #{id};
            """)
    void updateUser(UserDto user);

    /**
     * 更新密码
     *
     * @param id       用户ID
     * @param password 新密码
     */
    @Update("update ibu_user set password = #{password} where id = #{id};")
    void updatePassword(Long id, String password);

    /**
     * 重置密码
     *
     * @param id       用户ID
     * @param password 新密码
     */
    @Update("update ibu_user set pay_password = #{password} where id = #{id};")
    void updatePayPassword(Long id, String password);

    /**
     * 禁用用户
     *
     * @param tenantId 租户ID
     * @param id       用户ID
     * @param status   禁用/启用状态
     */
    @Update("""
            update ibt_tenant_user set
              invalid = #{status}
            where tenant_id = #{tenantId}
              and user_id = #{id};
            """)
    void disableUser(Long tenantId, Long id, Boolean status);

    /**
     * 禁用/启用用户
     *
     * @param id     用户ID
     * @param status 禁用/启用状态
     */
    @Update("update ibu_user set invalid = #{status} where id = #{id};")
    void updateStatus(Long id, boolean status);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @Delete("""
            delete u, g, t, o, r
            from ibu_user u
              left join ibu_group_member g on g.user_id = u.id
              left join ibt_tenant_user t on t.user_id = u.id
              left join ibo_organize_member o on o.user_id = u.id
              left join ibr_role_member r on r.member_id = u.id
                and r.type = 1
            where u.id = #{id};
            """)
    void deleteUser(Long id);

    /**
     * 获取可邀请用户列表
     *
     * @param search 查询关键词
     * @return 用户列表
     */
    @Select("""
            select u.id, u.code, u.name, u.account, u.mobile, u.remark, u.builtin, u.invalid
            from ibu_user u
              left join ibt_tenant_user r on r.user_id = u.id
                and r.tenant_id = #{tenantId}
            where u.invalid = 0
              and isnull(r.id)
              and (u.account = #{keyword} or u.mobile = #{keyword} or u.`name` like concat('%',#{keyword},'%'))
            """)
    List<UserVo> getOtherUsers(Search search);

    /**
     * 新增租户-用户关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Insert("insert ibt_tenant_user(tenant_id, user_id) values (#{tenantId}, #{userId});")
    void addRelation(long tenantId, long userId);

    /**
     * 加入组织机构
     *
     * @param id    用户ID
     * @param orgId 组织机构ID
     */
    @Insert("insert ibo_organize_member (post_id, user_id) values (#{orgId}, #{id});")
    void addOrgMember(long id, long orgId);

    /**
     * 加入角色成员
     *
     * @param id      用户ID
     * @param roleIds 角色ID集合
     */
    @Insert("""
            <script>insert ibr_role_member (type, role_id, member_id) values
            <foreach collection = "roleIds" item = "item" index = "index" separator = ",">(1, #{item}, #{id})</foreach>
            ;</script>
            """)
    void addRoleMember(long id, List<Long> roleIds);

    /**
     * 删除租户-用户关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Delete("delete from ibt_tenant_user where tenant_id = #{tenantId} and user_id = #{userId};")
    void removeRelation(long tenantId, long userId);

    /**
     * 删除用户-角色关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Delete("""
            <script>delete m from ibr_role r join ibr_role_member m on m.role_id = r.id and m.type = 1 and m.member_id = #{userId}
            <if test = 'tenantId != null'>where r.tenant_id = #{tenantId}</if>
            ;</script>
            """)
    void removeRoleRelation(Long tenantId, Long userId);


    /**
     * 清除绑定设备
     *
     * @param id 用户ID
     */
    @Delete("delete from ibu_user_device where user_id = #{id};")
    void removeDevice(Long id);

    /**
     * 删除用户-用户组关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Delete("""
            delete m from ibu_group g join ibu_group_member m on m.group_id = g.id and m.user_id = #{userId}
            where g.tenant_id = #{tenantId};
            """)
    void removeGroupRelation(long tenantId, long userId);

    /**
     * 删除用户-组织机构关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Delete("""
            delete m from ibo_organize o join ibo_organize_member m on m.post_id = o.id and m.user_id = #{userId}
            where o.tenant_id = #{tenantId};
            """)
    void removeOrganizeRelation(long tenantId, long userId);

    /**
     * 查询符合条件的用户数量
     *
     * @param keyword 查询条件
     * @return 用户数量
     */
    @Select("select count(*) from ibu_user where invalid = 0 and (code = #{keyword} or account = #{keyword} or mobile = #{keyword});")
    int getCount(String keyword);

    @Select("select id, name, account, mobile, union_id from ibu_user;")
    List<User> getAllUser();
}
