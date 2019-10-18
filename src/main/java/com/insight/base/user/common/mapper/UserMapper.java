package com.insight.base.user.common.mapper;

import com.insight.util.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 宣炳刚
 * @date 2019-09-04
 * @remark
 */
@Mapper
public interface UserMapper {

    /**
     * 新增用户
     *
     * @param user 用户DTO
     */
    @Insert("insert ibu_user(id, code, name, account, mobile, email, union_id, password, head_img, remark, is_builtin, creator, creator_id, created_time) values " +
            "(#{id}, #{code}, #{name}, #{account}, #{mobile}, #{email}, #{unionId}, #{password}, #{headImg}, #{remark}, #{isBuiltin}, #{creator}, #{creatorId}, #{createdTime});")
    void addUser(User user);
}
