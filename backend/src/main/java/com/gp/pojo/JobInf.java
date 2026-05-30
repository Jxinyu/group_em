package com.gp.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
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
@TableName("job_inf")
public class JobInf extends Model<JobInf> {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 职位名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 详细信息
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 所属部门
     */
    @TableField("DEPT_ID")
    private Integer deptId;

    private String dept;


}
