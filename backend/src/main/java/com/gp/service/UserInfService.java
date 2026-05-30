package com.gp.service;

import com.github.pagehelper.PageInfo;
import com.gp.pojo.UserInf;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gp
 * @since 2023-06-01
 */
public interface UserInfService extends IService<UserInf> {
    PageInfo<UserInf> queryOneUser(UserInf userInf, Integer size, Integer currentPage);

    int addUser(UserInf userInf);

    int updateUser(UserInf userInf);

    int deleteUser(@Param("id") Integer id);
}
