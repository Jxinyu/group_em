package com.gp.service;

import com.github.pagehelper.PageInfo;
import com.gp.pojo.JobInf;
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
public interface JobInfService extends IService<JobInf> {
    PageInfo<JobInf> queryOneJob(JobInf jobInf, Integer size, Integer currentPage);

    int addJob(JobInf jobInf);

    int updateJob(JobInf jobInf);

    int deleteJob(@Param("id") Integer id);
}
