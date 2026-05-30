package com.gp.mapper;

import com.gp.pojo.DeptInf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.pojo.DocumentInf;
import com.gp.pojo.UserInf;
import com.sun.scenario.effect.impl.prism.PrImage;
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
public interface DeptInfMapper extends BaseMapper<DeptInf> {

    List<DeptInf> queryOneDept(DeptInf deptInf);

    List<DeptInf> queryAllDept();

    int addDept(DeptInf deptInf);

    int updateDept(DeptInf deptInf);

    int deleteDept(@Param("id") Integer id);

}
