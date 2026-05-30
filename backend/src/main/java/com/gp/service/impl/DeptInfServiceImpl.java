package com.gp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gp.pojo.DeptInf;
import com.gp.mapper.DeptInfMapper;
import com.gp.service.DeptInfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mybatis.spring.SqlSessionTemplate;
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
public class DeptInfServiceImpl extends ServiceImpl<DeptInfMapper, DeptInf> implements DeptInfService {


    @Resource
    private DeptInfMapper deptInfMapper;


    @Override
    public PageInfo<DeptInf> queryOneDept(DeptInf deptInf, Integer size, Integer currentPage) {
        PageHelper.startPage(currentPage, size);
        List<DeptInf> deptInfs = deptInfMapper.queryOneDept(deptInf);
        return new PageInfo<>(deptInfs);
    }


    @Override
    public int addDept(DeptInf deptInf) {
        return deptInfMapper.addDept(deptInf);
    }

    @Override
    public int updateDept(DeptInf deptInf) {
        return deptInfMapper.updateDept(deptInf);
    }

    @Override
    public int deleteDept(Integer id) {
        return deptInfMapper.deleteDept(id);
    }
}
