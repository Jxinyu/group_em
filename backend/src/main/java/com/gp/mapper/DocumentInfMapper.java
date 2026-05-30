package com.gp.mapper;

import com.gp.pojo.DocumentInf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface DocumentInfMapper extends BaseMapper<DocumentInf> {

    List<DocumentInf> queryOneDocument(DocumentInf documentInf);


    int insertUploadFile(DocumentInf documentInf, @Param("username") String username);

    List<DocumentInf> queryDocumentList(DocumentInf documentInf);
}
