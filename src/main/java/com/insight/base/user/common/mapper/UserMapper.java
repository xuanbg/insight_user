package com.insight.base.user.common.mapper;

import com.insight.base.user.common.dto.FuncPermitDto;
import com.insight.base.user.common.dto.UserDto;
import com.insight.base.user.common.dto.UserListDto;
import com.insight.util.common.JsonTypeHandler;
import com.insight.util.pojo.Log;
import com.insight.util.pojo.User;
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
     * @param key      查询关键词
     * @return 用户列表
     */
    @Select("<script>select u.id, u.code, u.name, u.account, u.mobile, u.remark, u.is_builtin, u.is_invalid from ibu_user u " +
            "<if test = 'tenantId != null'>join ibt_tenant_user r on r.user_id = u.id and r.tenant_id = #{tenantId} </if>" +
            "<if test = 'key != null'>where u.code = #{key} or u.account = #{key} or u.mobile = #{key} or u.name like concat('%',#{key},'%') </if>" +
            "order by u.created_time desc</script>")
    List<UserListDto> getUsers(@Param("tenantId") String tenantId, @Param("key") String key);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @Results({@Result(property = "openId", column = "open_id", javaType = Map.class, typeHandler = JsonTypeHandler.class)})
    @Select("select id, code, name, account, mobile, email, union_id, open_id, head_img, remark, is_builtin, is_invalid, creator, creator_id, created_time " +
            "from ibu_user where id = #{id};")
    UserDto getUser(String id);

    /**
     * 获取用户功能授权
     *
     * @param id 用户ID
     * @return 功能授权集合
     */
    @Select("select * from (select distinct a.id, null as parent_id, a.`index`, 0 as type, a.`name`, null as remark, null as permit from ibv_user_roles r " +
            "join ibr_role_func_permit p on p.role_id = r.role_id join ibs_function f on f.id = p.function_id join ibs_navigator m on m.id = f.nav_id " +
            "join ibs_navigator g on g.id = m.parent_id join ibs_application a on a.id = g.app_id where r.user_id = #{id} union all " +
            "select distinct g.id, g.app_id as parent_id, g.`index`, g.type, g.`name`, null as remark, null as permit from ibv_user_roles r " +
            "join ibr_role_func_permit p on p.role_id = r.role_id join ibs_function f on f.id = p.function_id join ibs_navigator m on m.id = f.nav_id " +
            "join ibs_navigator g on g.id = m.parent_id where r.user_id = #{id} union all " +
            "select distinct m.id, m.parent_id, m.`index`, m.type, m.`name`, null as remark, null as permit from ibv_user_roles r " +
            "join ibr_role_func_permit p on p.role_id = r.role_id join ibs_function f on f.id = p.function_id " +
            "join ibs_navigator m on m.id = f.nav_id where r.user_id = #{id} union all " +
            "select f.id, f.nav_id as parent_id, f.`index`, p.permit + 3 as type, f.`name`, case p.permit when 0 then '禁止' else '允许' end as remark, p.permit " +
            "from ibv_user_roles r join ibr_role_func_permit p on p.role_id = r.role_id join ibs_function f on f.id = p.function_id " +
            "where r.user_id = #{id}) t order by t.`index`;")
    List<FuncPermitDto> getUserPermit(String id);

    /**
     * 新增用户
     *
     * @param user 用户DTO
     */
    @Insert("insert ibu_user(id, code, name, account, mobile, email, union_id, password, head_img, remark, is_builtin, creator, creator_id, created_time) values " +
            "(#{id}, #{code}, #{name}, #{account}, #{mobile}, #{email}, #{unionId}, #{password}, #{headImg}, #{remark}, #{isBuiltin}, #{creator}, #{creatorId}, #{createdTime});")
    void addUser(User user);

    /**
     * 匹配关键词的用户数
     *
     * @param id  用户ID
     * @param key 查询关键词
     * @return 用户数
     */
    @Select("select count(*) from ibu_user where (account = #{key} or mobile = #{key} or email = #{key}) and id != #{id};")
    int matchUsers(@Param("id") String id, @Param("key") String key);

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
    int getUserCount(@Param("tenantId") String tenantId, @Param("code") String code);

    /**
     * 匹配租户下的用户数
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 用户数
     */
    @Select("select count(*) from ibt_tenant_user where tenant_id = #{tenantId} and user_id = #{userId};")
    int matchRelation(@Param("tenantId") String tenantId, @Param("userId") String userId);

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
    void updatePassword(String id, String password);

    /**
     * 重置密码
     *
     * @param id       用户ID
     * @param password 新密码
     */
    @Update("update ibu_user set pay_password = #{password} where id = #{id};")
    void updatePayPassword(String id, String password);

    /**
     * 禁用/启用用户
     *
     * @param id     用户ID
     * @param status 禁用/启用状态
     */
    @Update("update ibu_user set is_invalid = #{status} where id = #{id};")
    void updateStatus(String id, boolean status);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @Delete("delete u, g, t, o, r from ibu_user u left join ibu_group_member g on g.user_id = u.id " +
            "left join ibt_tenant_user t on t.user_id = u.id left join ibo_organize_member o on o.user_id = u.id " +
            "left join ibr_role_member r on r.member_id = u.id and r.type = 1 where u.id = #{id};")
    void deleteUser(String id);

    /**
     * 新增租户-用户关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Insert("insert ibt_tenant_user(id, tenant_id, user_id) values (replace(uuid(), '-', ''), #{tenantId}, #{userId});")
    void addRelation(@Param("tenantId") String tenantId, @Param("userId") String userId);

    /**
     * 记录操作日志
     *
     * @param log 日志DTO
     */
    @Insert("insert ibl_operate_log(id, tenant_id, type, business, business_id, content, dept_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{type}, #{business}, #{businessId}, #{content, typeHandler = com.insight.util.common.JsonTypeHandler}, " +
            "#{deptId}, #{creator}, #{creatorId}, #{createdTime});")
    void addLog(Log log);

    /**
     * 获取操作日志列表
     *
     * @param tenantId 租户ID
     * @param business 业务类型
     * @param key      查询关键词
     * @return 操作日志列表
     */
    @Select("<script>select id, type, business, business_id, dept_id, creator, creator_id, created_time " +
            "from ibl_operate_log where business = #{business} " +
            "<if test = 'tenantId != null'>and tenant_id = #{tenantId} </if>" +
            "<if test = 'tenantId == null'>and tenant_id is null </if>" +
            "<if test = 'key!=null'>and (type = #{key} or business = #{key} or business_id = #{key} or " +
            "dept_id = #{key} or creator = #{key} or creator_id = #{key}) </if>" +
            "order by created_time</script>")
    List<Log> getLogs(@Param("tenantId") String tenantId, @Param("business") String business, @Param("key") String key);

    /**
     * 获取操作日志列表
     *
     * @param id 日志ID
     * @return 操作日志列表
     */
    @Results({@Result(property = "content", column = "content", javaType = Object.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from ibl_operate_log where id = #{id};")
    Log getLog(String id);
}
