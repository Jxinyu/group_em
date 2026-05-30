package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.pojo.WaitNotice;
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
public interface WaitNoticeMapper extends BaseMapper<WaitNotice> {

    int isExist(WaitNotice waitNotice);

    List<WaitNotice> getListByNoticeId(@Param("nId") Integer nId);
}
