package com.gp.service;

import com.github.pagehelper.PageInfo;
import com.gp.pojo.DocumentInf;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gp
 * @since 2023-06-01
 */
public interface DocumentInfService extends IService<DocumentInf> {

    int insertUploadFile(DocumentInf documentInf, @Param("username") String username);

    PageInfo<DocumentInf> queryDocumentListByPage(DocumentInf documentInf,
                                                         int pageNum, int pageSize);
}
