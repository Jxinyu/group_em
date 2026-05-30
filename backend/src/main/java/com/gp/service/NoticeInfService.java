package com.gp.service;

import com.github.pagehelper.PageInfo;
import com.gp.pojo.NoticeInf;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gp
 * @since 2023-06-01
 */
public interface NoticeInfService extends IService<NoticeInf> {
    PageInfo<NoticeInf> queryNoticeList(NoticeInf noticeInf, Integer currentPage, Integer size);
    boolean updateNoticeById(NoticeInf noticeInf, String loginName);

    boolean insertNotice(NoticeInf noticeInf, @Param("Loginname") String loginName);
}
