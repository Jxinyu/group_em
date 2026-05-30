package com.gp.mapper;

import com.gp.pojo.JobInf;
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
public interface JobInfMapper extends BaseMapper<JobInf> {
    List<JobInf> queryOneJob(JobInf jobInf);

    List<JobInf> queryAllJob();

    int addJob(JobInf jobInf);

    int updateJob(JobInf jobInf);

    int deleteJob(@Param("id") Integer id);
}
