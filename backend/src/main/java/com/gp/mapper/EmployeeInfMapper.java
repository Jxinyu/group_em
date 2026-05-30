package com.gp.mapper;

import com.gp.pojo.EmployeeInf;
import com.gp.pojo.EmployeeInf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.pojo.UserInf;
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
public interface EmployeeInfMapper extends BaseMapper<EmployeeInf> {
    List<EmployeeInf> queryOneEmployee(EmployeeInf employeeInf);

    List<EmployeeInf> queryAllEmployee();

    int addEmployee(EmployeeInf employeeInf);

    int updateEmployee(EmployeeInf employeeInf);

    int deleteEmployee(@Param("id") Integer id);
}
