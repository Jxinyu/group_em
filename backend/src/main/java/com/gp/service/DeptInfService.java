package com.gp.service;

import com.github.pagehelper.PageInfo;
import com.gp.pojo.DeptInf;
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
public interface DeptInfService extends IService<DeptInf> {
    PageInfo<DeptInf> queryOneDept(DeptInf deptInf, Integer size, Integer currentPage);

    int addDept(DeptInf deptInf);

    int updateDept(DeptInf deptInf);

    int deleteDept(@Param("id") Integer id);
}
