package com.gp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gp.pojo.NoticeInf;
import com.gp.mapper.NoticeInfMapper;
import com.gp.service.NoticeInfService;
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
public class NoticeInfServiceImpl extends ServiceImpl<NoticeInfMapper, NoticeInf> implements NoticeInfService {
    @Resource
    private NoticeInfMapper noticeInfMapper;

    @Override
    public PageInfo<NoticeInf> queryNoticeList(NoticeInf noticeInf, Integer currentPage, Integer size) {
        PageHelper.startPage(currentPage, size);
        List<NoticeInf> noticeInfs = noticeInfMapper.queryNoticeList(noticeInf);
        return new PageInfo<>(noticeInfs);
    }

    @Override
    public boolean updateNoticeById(NoticeInf noticeInf, String loginName) {
        return noticeInfMapper.updateNoticeById(noticeInf, loginName);
    }

    @Override
    public boolean insertNotice(NoticeInf noticeInf, String loginName) {
        return noticeInfMapper.insertNotice(noticeInf, loginName);
    }
}
