package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.pojo.Chatting;
import com.gp.pojo.GroupChat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gp
 * @since 2023-06-01
 */
@Mapper
public interface GroupChatMapper extends BaseMapper<GroupChat> {

    List<GroupChat> getChatByUsernameAndChatName(@Param("chatName") String chatName);
}
