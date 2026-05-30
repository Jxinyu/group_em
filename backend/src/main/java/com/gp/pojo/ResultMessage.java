package com.gp.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author ljb
 * @create 2023/6/26
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultMessage {
    /**
     * 是否是系统
     */
    private boolean isSystem;

    /**
     * 发给谁
     */
    private String toName;

    /**
     * 消息类型
     */
    private Integer contentType;

    /**
     * 内容
     */
    private Object message;

}
