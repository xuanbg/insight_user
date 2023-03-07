package com.insight.base.user.common.mapper;

import com.insight.base.user.common.dto.GroupDto;
import com.insight.base.user.common.dto.GroupListDto;
import com.insight.base.user.common.dto.UserListDto;
import com.insight.utils.pojo.base.Search;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019/12/4
 * @remark 用户组组DTO
 */
@Mapper
public interface GroupMapper {

    /**
     * 获取用户组列表
     *
     * @param search      查询关键词
     * @return 用户组列表
     */
    @Select("<script>select id, code, name, remark, builtin from ibu_group where tenant_id = #{tenantId} " +
            "<if test = 'keyword != null'>and (code = #{keyword} or name like concat('%',#{keyword},'%')) </if>" +
            "</script>")
    List<GroupListDto> getGroups(Search search);

    /**
     * 获取用户组详情
     *
     * @param id 用户组ID
     * @return 用户组详情
     */
    @Select("select * from ibu_group where id = #{id};")
    GroupDto getGroup(Long id);

    /**
     * 新增用户组
     *
     * @param group 用户组DTO
     */
    @Insert("insert ibu_group(id, tenant_id, code, name, remark, builtin, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{code}, #{name}, #{remark}, #{builtin}, #{creator}, #{creatorId}, #{createdTime});")
    void addGroup(GroupDto group);

    /**
     * 更新用户组
     *
     * @param group 用户组DTO
     */
    @Update("update ibu_group set name = #{name}, remark = #{remark} where id = #{id};")
    void updateGroup(GroupDto group);

    /**
     * 删除用户组
     *
     * @param id 用户组ID
     */
    @Delete("delete g, m from ibu_group g left join ibu_group_member m on m.group_id = g.id where g.id = #{id};")
    void deleteGroup(Long id);

    /**
     * 查询用户组成员
     *
     * @param search 查询关键词
     * @return 用户组成员集合
     */
    @Select("<script>select u.id, u.code, u.name, u.account, u.mobile, u.remark, u.builtin, u.invalid from ibu_group_member m join ibu_user u on u.id = m.user_id " +
            "<if test = 'keyword != null'>and (u.code = #{keyword} or u.account = #{keyword} or u.name like concat('%',#{keyword},'%')) </if>" +
            "where m.group_id = #{id}</script>")
    List<UserListDto> getMembers(Search search);

    /**
     * 查询用户组可用用户列表
     *
     * @param id 用户组ID
     * @return 用户列表
     */
    @Select("select u.id, u.code, u.name, u.account, u.mobile, u.remark, u.builtin, u.invalid from ibu_user u " +
            "join ibt_tenant_user t on t.user_id = u.id join ibu_group g on g.tenant_id = t.tenant_id and g.id = #{id} " +
            "left join ibu_group_member m on m.group_id = g.id and m.user_id = u.id where isnull(m.id)")
    List<UserListDto> getOthers(Long id);

    /**
     * 添加用户组成员
     *
     * @param id      用户组ID
     * @param userIds 用户ID集合
     */
    @Insert("<script>insert ibu_group_member (group_id, user_id) values " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "(#{id}, #{item})</foreach>;</script>")
    void addMembers(@Param("id") Long id, @Param("list") List<Long> userIds);

    /**
     * 移除用户组成员
     *
     * @param id      用户组ID
     * @param userIds 用户ID集合
     */
    @Delete("<script>delete from ibu_group_member where group_id = #{id} and user_id in " +
            "(<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "#{item}</foreach>);</script>")
    void removeMembers(@Param("id") Long id, @Param("list") List<Long> userIds);

    /**
     * 获取指定租户下指定编码的用户组数量
     *
     * @param tenantId 租户ID
     * @param code     用户组编码
     * @return 用户组数量
     */
    @Select("select count(*) from ibu_group where tenant_id = #{tenantId} and code = #{code}")
    int getGroupCount(@Param("tenantId") Long tenantId, @Param("code") String code);
}
