package com.gp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gp.pojo.DocumentInf;
import com.gp.mapper.DocumentInfMapper;
import com.gp.service.DocumentInfService;
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
public class DocumentInfServiceImpl extends ServiceImpl<DocumentInfMapper, DocumentInf> implements DocumentInfService {

    @Resource
    private DocumentInfMapper documentInfMapper;


    @Override
    public int insertUploadFile(DocumentInf documentInf, String username) {
        return documentInfMapper.insertUploadFile(documentInf, username);
    }

    @Override
    public PageInfo<DocumentInf> queryDocumentListByPage(DocumentInf documentInf, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<DocumentInf> documentInfs = documentInfMapper.queryDocumentList(documentInf);
        return new PageInfo<>(documentInfs);
    }
}
