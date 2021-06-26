package com.insight.base.user.common.mapper;

import com.insight.base.user.common.dto.FuncPermitDto;
import com.insight.base.user.common.dto.UserDto;
import com.insight.base.user.common.dto.UserListDto;
import com.insight.utils.common.JsonTypeHandler;
import com.insight.utils.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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
     * @param tenantId 租户ID
     * @param type     用户类型
     * @param key      查询关键词
     * @return 用户列表
     */
    @Select("<script>select u.id, u.type, u.code, u.name, u.account, u.mobile, u.remark, u.is_builtin, u.is_invalid from ibu_user u " +
            "<if test = 'tenantId != null'>join ibt_tenant_user r on r.user_id = u.id and r.tenant_id = #{tenantId} </if>" +
            "where 0=0 " +
            "<if test = 'type != null'>and u.type = #{type} </if>" +
            "<if test = 'key != null'>and u.code = #{key} or u.account = #{key} or u.mobile = #{key} or u.name like concat('%',#{key},'%') </if>" +
            "order by u.created_time</script>")
    List<UserListDto> getUsers(@Param("tenantId") Long tenantId, @Param("type") Integer type, @Param("key") String key);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @Results({@Result(property = "openId", column = "open_id", javaType = Map.class, typeHandler = JsonTypeHandler.class)})
    @Select("select id, u.type, code, name, account, mobile, email, union_id, open_id, head_img, remark, is_builtin, is_invalid, creator, creator_id, created_time " +
            "from ibu_user where id = #{id};")
    UserDto getUser(Long id);

    /**
     * 获取用户功能授权
     *
     * @param id 用户ID
     * @return 功能授权集合
     */
    @Select("select * from (select distinct a.id, null as parent_id, a.`index`, 0 as type, a.`name`, null as remark, null as permit from ibv_user_roles r " +
            "join ibr_role_permit p on p.role_id = r.role_id join ibs_function f on f.id = p.function_id join ibs_navigator m on m.id = f.nav_id " +
            "join ibs_navigator g on g.id = m.parent_id join ibs_application a on a.id = g.app_id where r.user_id = #{id} union " +
            "select distinct g.id, g.app_id as parent_id, g.`index`, g.type, g.`name`, null as remark, null as permit from ibv_user_roles r " +
            "join ibr_role_permit p on p.role_id = r.role_id join ibs_function f on f.id = p.function_id join ibs_navigator m on m.id = f.nav_id " +
            "join ibs_navigator g on g.id = m.parent_id where r.user_id = #{id} union " +
            "select distinct m.id, m.parent_id, m.`index`, m.type, m.`name`, null as remark, null as permit from ibv_user_roles r " +
            "join ibr_role_permit p on p.role_id = r.role_id join ibs_function f on f.id = p.function_id " +
            "join ibs_navigator m on m.id = f.nav_id where r.user_id = #{id} union " +
            "select f.id, f.nav_id as parent_id, f.`index`, min(p.permit) + 3 as type, f.`name`, " +
            "case min(p.permit) when 0 then '禁止' else '允许' end as remark, min(p.permit) as permit " +
            "from ibv_user_roles r join ibr_role_permit p on p.role_id = r.role_id join ibs_function f on f.id = p.function_id " +
            "where r.user_id = #{id} group by f.id) t order by t.`index`;")
    List<FuncPermitDto> getUserPermit(Long id);

    /**
     * 新增用户
     *
     * @param user 用户DTO
     */
    @Insert("insert ibu_user(id, u.type, code, name, account, mobile, email, union_id, password, head_img, remark, is_builtin, creator, creator_id, created_time) values " +
            "(#{id}, #{type}, #{code}, #{name}, #{account}, #{mobile}, #{email}, #{unionId}, #{password}, #{headImg}, #{remark}, #{isBuiltin}, #{creator}, #{creatorId}, #{createdTime});")
    void addUser(User user);

    /**
     * 匹配关键词的用户数
     *
     * @param id  用户ID
     * @param key 查询关键词
     * @return 用户数
     */
    @Select("select count(*) from ibu_user where (account = #{key} or mobile = #{key} or email = #{key}) and id != #{id};")
    int matchUsers(@Param("id") Long id, @Param("key") String key);

    /**
     * 获取指定租户下指定编码的用户数量
     *
     * @param tenantId 租户ID
     * @param code     用户编码
     * @return 用户数量
     */
    @Select("<script>select count(*) from ibu_user u " +
            "<if test = 'tenantId != null'>join ibt_tenant_user r on r.user_id = u.id and r.tenant_id = #{tenantId} </if>" +
            "where u.code = #{code};</script>")
    int getUserCount(@Param("tenantId") Long tenantId, @Param("code") String code);

    /**
     * 匹配租户下的用户数
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 用户数
     */
    @Select("select count(*) from ibt_tenant_user where tenant_id = #{tenantId} and user_id = #{userId};")
    int matchRelation(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 更新用户
     *
     * @param user 用户DTO
     */
    @Update("update ibu_user set name = #{name}, account = #{account}, mobile = #{mobile}, email = #{email}, head_img = #{headImg}, remark = #{remark} where id = #{id};")
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
     * 禁用/启用用户
     *
     * @param id     用户ID
     * @param status 禁用/启用状态
     */
    @Update("update ibu_user set is_invalid = #{status} where id = #{id};")
    void updateStatus(Long id, boolean status);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @Delete("delete u, g, t, o, r from ibu_user u left join ibu_group_member g on g.user_id = u.id " +
            "left join ibt_tenant_user t on t.user_id = u.id left join ibo_organize_member o on o.user_id = u.id " +
            "left join ibr_role_member r on r.member_id = u.id and r.type = 1 where u.id = #{id};")
    void deleteUser(Long id);

    /**
     * 获取可邀请用户列表
     *
     * @param tenantId 租户ID
     * @param key      查询关键词
     * @return 用户列表
     */
    @Select("select u.id, u.type, u.code, u.name, u.account, u.mobile, u.remark, u.is_builtin, u.is_invalid from ibu_user u " +
            "left join ibt_tenant_user r on r.user_id = u.id and r.tenant_id = #{tenantId} " +
            "where isnull(r.id) and (u.account = #{key} or u.mobile = #{key} or u.`name` like concat('%',#{key},'%'))")
    List<UserListDto> getOtherUsers(@Param("tenantId") Long tenantId, @Param("key") String key);

    /**
     * 新增租户-用户关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Insert("insert ibt_tenant_user(id, tenant_id, user_id) values (replace(uuid(), '-', ''), #{tenantId}, #{userId});")
    void addRelation(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 删除租户-用户关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Delete("delete from ibt_tenant_user where tenant_id = #{tenantId} and user_id = #{userId};")
    void removeRelation(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 删除租户-角色关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Delete("delete m from ibr_role r join ibr_role_member m on m.role_id = r.id and m.type = 1 and m.member_id = #{userId} where r.tenant_id = #{tenantId};")
    void removeRoleRelation(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 删除租户-用户组关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Delete("delete m from ibu_group g join ibu_group_member m on m.group_id = g.id and m.user_id = #{userId} where g.tenant_id = #{tenantId};")
    void removeGroupRelation(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 删除租户-组织机构关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Delete("delete m from ibo_organize o join ibo_organize_member m on m.post_id = o.id and m.user_id = #{userId} where o.tenant_id = #{tenantId};")
    void removeOrganizeRelation(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}
