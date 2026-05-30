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
@TableName("facekey_inf")
public class FacekeyInf extends Model<FacekeyInf> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 百度语音合成所需要的appID
     */
    @TableField("appID")
    private String appID;

    /**
     * 百度语音合成所需要的apiKey
     */
    @TableField("apiKey")
    private String apiKey;

    /**
     * 百度语音合成所需要的secretKey
     */
    @TableField("secretKey")
    private String secretKey;

    /**
     * 人脸对比阀值threshold
     */
    @TableField("threshold")
    private Integer threshold;


    public static final String ID = "Id";

    public static final String APPID = "appID";

    public static final String APIKEY = "apiKey";

    public static final String SECRETKEY = "secretKey";

    public static final String THRESHOLD = "threshold";

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
