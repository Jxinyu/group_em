package com.gp.mapper;

import com.gp.pojo.NoticeInf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysql.cj.protocol.x.Notice;
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
public interface NoticeInfMapper extends BaseMapper<NoticeInf> {
    List<NoticeInf> queryNoticeList(NoticeInf noticeInf);

    boolean updateNoticeById(NoticeInf noticeInf, @Param("Loginname") String loginName);

    boolean insertNotice(NoticeInf noticeInf, @Param("Loginname") String loginName);


    List<NoticeInf> getPushedByUsernameAndStatus(@Param("username") String username, @Param("status") Integer status);
}
