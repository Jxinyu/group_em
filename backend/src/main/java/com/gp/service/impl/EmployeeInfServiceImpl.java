package com.gp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gp.pojo.EmployeeInf;
import com.gp.mapper.EmployeeInfMapper;
import com.gp.service.EmployeeInfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gp
 * @since 2023-06-01
 */
@Service
public class EmployeeInfServiceImpl extends ServiceImpl<EmployeeInfMapper, EmployeeInf> implements EmployeeInfService {

    @Resource
    private EmployeeInfMapper employeeInfMapper;


    @Override
    public PageInfo<EmployeeInf> queryOneEmployee(EmployeeInf employeeInf, Integer size, Integer currentPage) {
        PageHelper.startPage(currentPage, size);
        List<EmployeeInf> employeeInfs = employeeInfMapper.queryOneEmployee(employeeInf);
        return new PageInfo<>(employeeInfs);
    }


    @Override
    public int addEmployee(EmployeeInf employeeInf) {
        return employeeInfMapper.addEmployee(employeeInf);
    }

    @Override
    public int updateEmployee(EmployeeInf employeeInf) {
        return employeeInfMapper.updateEmployee(employeeInf);
    }

    @Override
    public int deleteEmployee(Integer id) {
        return employeeInfMapper.deleteEmployee(id);
    }
}
