package com.gp.service;

import com.github.pagehelper.PageInfo;
import com.gp.pojo.EmployeeInf;
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
public interface EmployeeInfService extends IService<EmployeeInf> {
    PageInfo<EmployeeInf> queryOneEmployee(EmployeeInf employeeInf, Integer size, Integer currentPage);

    int addEmployee(EmployeeInf employeeInf);

    int updateEmployee(EmployeeInf employeeInf);

    int deleteEmployee(@Param("id") Integer id);
}
