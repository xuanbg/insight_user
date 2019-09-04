package com.insight.base.user.common.mapper;

import com.insight.util.pojo.User;
import org.apache.ibatis.annotations.Insert;

/**
 * @author 宣炳刚
 * @date 2019-09-04
 * @remark
 */
public interface UserMapper {

    /**
     * 新增用户
     *
     * @param user 用户DTO
     * @return 受影响行数
     */
    @Insert("")
    int addUser(User user);
}
