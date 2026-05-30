package com.gp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp.pojo.ResultMessage;

/**
 * @author ljb
 * @create 2023/6/26
 */
public class MessageUtils {
    /**
     * 获取消息实体,用于返回前端
     *
     * @param isSystem  is系统
     * @param fromName  来自谁
     * @param message   消息
     * @return {@link String}
     */
    public static String getMessage(boolean isSystem, String fromName, Object message, Integer contentType){

        ResultMessage result = new ResultMessage();
        result.setSystem(isSystem);
        result.setMessage(message);
        result.setContentType(contentType);
        if (fromName != null){
            result.setToName(fromName);
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
