package com.gp.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.util.Date;

import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

/**
 * <p>
 * 
 * </p>
 *
 * @author gp
 * @since 2023-06-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("document_inf")
public class DocumentInf extends Model<DocumentInf> {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    @TableField("TITLE")
    @Size(min = 2, max = 40, message = "标题长度大小在2-40个字符内")
    private String title;

    /**
     * 文件名称
     */
    @TableField("Filename")
    private String filename;

    /**
     * 描述
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 创建时间
     */
    @TableField("CREATE_DATE")
    @PastOrPresent
    private String createDate;

    /**
     * 用户ID(创建人)
     */
    @TableField("USER_ID")
    private Integer userId;

    private String loginName;

    /**
     * 文件存放路径
     */
    @TableField("FilePath")
    private String filePath;
}
