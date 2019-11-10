package com.insight.base.user.common.mapper;

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
 * @remark
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
     * 获取指定租户下指定编码的模板数量
     *
     * @param tenantId 租户ID
     * @param code     模板编码
     * @return 模板数量
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
    @Delete("delete from ibu_user where id = #{id};")
    void deleteUser(String id);

    /**
     * 删除租户-用户关系
     *
     * @param id 用户ID
     */
    @Delete("delete from ibt_tenant_user where user_id = #{id};")
    void deleteRelation(String id);

    /**
     * 删除组织机构成员关系
     *
     * @param id 用户ID
     */
    @Delete("delete from ibo_organize_member where user_id = #{id};")
    void deleteOrgMember(String id);

    /**
     * 删除用户组成员关系
     *
     * @param id 用户ID
     */
    @Delete("delete from ibu_group_member where user_id = #{id};")
    void deleteGroupMember(String id);

    /**
     * 删除角色成员关系
     *
     * @param id 用户ID
     */
    @Delete("delete from ibr_role_member where type = 1 and member_id = #{id};")
    void deleteRoleMember(String id);

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
