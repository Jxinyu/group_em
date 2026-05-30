package com.gp.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
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
@Getter
@Setter
@Accessors(chain = true)
@TableName("dept_inf")
public class DeptInf extends Model<DeptInf> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 部门名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 详细信息
     */
    @TableField("REMARK")
    private String remark;


    public static final String ID = "ID";

    public static final String NAME = "NAME";

    public static final String REMARK = "REMARK";

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
