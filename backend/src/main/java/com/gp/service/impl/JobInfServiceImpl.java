package com.gp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gp.pojo.JobInf;
import com.gp.mapper.JobInfMapper;
import com.gp.service.JobInfService;
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
public class JobInfServiceImpl extends ServiceImpl<JobInfMapper, JobInf> implements JobInfService {

    @Resource
    private JobInfMapper jobInfMapper;

    @Override
    public PageInfo<JobInf> queryOneJob(JobInf jobInf, Integer size, Integer currentPage) {
        PageHelper.startPage(currentPage, size);
        List<JobInf> jobInfs = jobInfMapper.queryOneJob(jobInf);
        return new PageInfo<>(jobInfs);
    }

    @Override
    public int addJob(JobInf jobInf) {
        return jobInfMapper.addJob(jobInf);
    }

    @Override
    public int updateJob(JobInf jobInf) {
        return jobInfMapper.updateJob(jobInf);
    }

    @Override
    public int deleteJob(Integer id) {
        return jobInfMapper.deleteJob(id);
    }
}
