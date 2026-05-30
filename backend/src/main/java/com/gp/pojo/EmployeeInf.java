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
import org.hibernate.validator.constraints.Range;

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
@TableName("employee_inf")
public class EmployeeInf extends Model<EmployeeInf> {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 部门主键
     */
    @TableField("DEPT_ID")
    @NotNull(message = "请选择部门")
    private Integer deptId;

    private String dept;

    /**
     * 职位主键
     */
    @TableField("JOB_ID")
    @NotNull(message = "请选择职位")
    private Integer jobId;

    private String job;

    /**
     * 名字
     */
    @TableField("NAME")
    @NotBlank(message = "名字不为空")
    private String name;

    /**
     * 身份证
     */
    @TableField("CARD_ID")
    @Pattern(regexp = "^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X)$", message = "身份证格式错误")
    private String cardId;

    /**
     * 住址
     */
    @TableField("ADDRESS")
    private String address;

    /**
     * 邮编
     */
    @TableField("POST_CODE")
    @Pattern(regexp = "[1-9]\\d{5}(?!\\d)", message = "邮编格式错误")
    private String postCode;

    /**
     * 电话
     */
    @TableField("TEL")
    private String tel;

    /**
     * 手机
     */
    @TableField("PHONE")
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "手机号码格式错误")
    private String phone;

    /**
     * QQ号
     */
    @TableField("QQ_NUM")
    @Pattern(regexp = "[1-9][0-9]{4,}", message = "QQ号格式错误")
    private String qqNum;

    /**
     * 电子邮件
     */
    @TableField("EMAIL")
    @Email(message = "邮箱格式错误")
    private String email;

    /**
     * 性别：1：男  2：女
     */
    @TableField("SEX")
    @Range(min = 1, max = 2, message = "性别选择范围错误")
    private Integer sex;

    /**
     * 政治面貌
     */
    @TableField("PARTY")
    private String party;

    /**
     * 出生日期
     */
    @TableField("BIRTHDAY")
    @NotBlank(message = "出生日期应该不为空")
    private String birthday;

    /**
     * 民族
     */
    @TableField("RACE")
    @NotBlank(message = "民族应该不为空")
    private String race;

    /**
     * 学历
     */
    @TableField("EDUCATION")
    @NotBlank(message = "学历应该不为空")
    private String education;

    /**
     * 专业
     */
    @TableField("SPECIALITY")
    @NotBlank(message = "专业应该不为空")
    private String speciality;

    /**
     * 特长
     */
    @TableField("HOBBY")
    private String hobby;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 创建日期
     */
    @TableField("CREATE_DATE")
    private String createDate;
}
