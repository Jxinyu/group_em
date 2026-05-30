package com.gp.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author gp
 * @since 2023-06-01
 */
@Data
@Accessors(chain = true)
@TableName("notice_inf")
public class NoticeInf extends Model<NoticeInf> {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 公告名称
     */
    @TableField("TITLE")
    private String title;

    /**
     * 公告内容
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 创建时间
     */
    @TableField("CREATE_DATE")
    private String createDate;

    /**
     * 用户ID(公告人)
     */
    @TableField("USER_ID")
    private Integer userId;

    private String loginName;

}
