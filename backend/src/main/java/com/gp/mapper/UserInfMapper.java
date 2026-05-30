package com.gp.mapper;

import com.gp.pojo.UserInf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gp
 * @since 2023-06-01
 */
@Mapper
public interface UserInfMapper extends BaseMapper<UserInf> {
    List<UserInf> queryOneUser(UserInf userInf);

    List<UserInf> queryAllUser();

    UserInf queryOneUserByUsername(@Param("username") String username);

    int addUser(UserInf userInf);

    int updateUser(UserInf userInf);

    boolean updateUserByLoginName(UserInf userInf);

    int deleteUser(@Param("id") Integer id);
}
