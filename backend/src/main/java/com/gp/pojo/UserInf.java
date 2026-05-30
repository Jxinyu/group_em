package com.gp.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;

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
@TableName("user_inf")
public class UserInf extends Model<UserInf> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 登录名
     */
    @TableField("Loginname")
    @NotNull(message = "账号不能为空")
    private String loginname;

    /**
     * 密码
     */
    @TableField("PASSWORD")
    @NotNull(message = "密码不能为空")
    @Size(min = 6, max = 18, message = "密码长度为6 ~ 18位")
    private String password;

    /**
     * 限权：	1:管理员	2:用户
     */
    @TableField("STATUS")
    @NotNull(message = "用户权限不能为空")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("Createdate")
    private String createdate;

    /**
     * 用户名
     */
    @TableField("Username")
    private String username;

    /**
     * 用户邮箱
     */
    @TableField("email")
    @Email(message = "邮箱格式不正确")
    private String email;



    public static final String ID = "ID";

    public static final String LOGINNAME = "Loginname";

    public static final String PASSWORD = "PASSWORD";

    public static final String STATUS = "STATUS";

    public static final String CREATEDATE = "Createdate";

    public static final String USERNAME = "Username";

    public static final String EMAIL = "EMAIL";


    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
