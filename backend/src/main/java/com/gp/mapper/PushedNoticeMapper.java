package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.pojo.PushedNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gp
 * @since 2023-06-01
 */
@Mapper
public interface PushedNoticeMapper extends BaseMapper<PushedNotice> {

    int insertPushed(@Param("username") String username, @Param("noticeId") Integer noticeId);
}
