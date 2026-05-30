package com.gp.ws;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp.mapper.ChattingMapper;
import com.gp.mapper.GroupChatMapper;
import com.gp.pojo.Chatting;
import com.gp.pojo.GroupChat;
import com.gp.pojo.Message;
import com.gp.utils.JWTUtil;
import com.gp.utils.MessageUtils;
import com.gp.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ljb
 * @create 2023/6/26
 */
@ServerEndpoint(value = "/chat/{type}", configurator = GetHttpSessionConfigurator.class)
@Component
@Slf4j
@Resource
public class ChatEndPoint {

    // 使用线程安全的 ConcurrentHashMap 代替普通的 HashMap 来存储在线用户
    private static final Map<String, ChatEndPoint> onlinePrivacyUsers = new ConcurrentHashMap<>();

    private static final Map<String, ChatEndPoint> onlineGroupUsers = new ConcurrentHashMap<>();

    private static Integer LINK_NUM = 0;

    private Session session;

    private static final String TYPE = "privacy";

    @Resource
    private ChattingMapper chattingMapper;

    @Resource
    private GroupChatMapper groupChatMapper;

    /**
     * 打开时
     *
     * @param session 一场
     * @param config  配置
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("type") String type) {
        LINK_NUM += 1;
        if (LINK_NUM > 100) { // 连接数限制
            try {
                // 向客户端发送连接已达上限的错误消息
                session.getBasicRemote().sendText("连接数已达到上限，无法连接。");
                session.close();
                return;
            } catch (IOException e) {
                log.error("连接关闭异常", e);
            }
        }

        this.session = session;
        String tokens = (String) session.getUserProperties().get("javax.servlet.http.HttpSession");

        String username = JWTUtil.getUsername(tokens);

        if (type.equals("privacy")) { // 私聊
            log.info("私聊用户 建立连接：" + username);
            onlinePrivacyUsers.put(username, this);
            broadcastOnlineUsers(type);
        } else {
            log.info("群聊用户 建立连接：" + username);
            onlineGroupUsers.put(username, this);
            //提示用户连接成功
            try {
                session.getBasicRemote().sendText(MessageUtils.getMessage(true,
                        null, "success", 6));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //广播给所有用户，已在线的用户
            broadcastOnlineUsers(type);
        }


    }

    /**
     * 在消息上
     *
     * @param session 一场
     * @param message 消息
     */
    @OnMessage
    public void onMessage(Session session, String message, @PathParam("type") String type) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Message value = mapper.readValue(message, Message.class);
            String content = value.getMessage();

            String token = (String) session.getUserProperties().get("javax.servlet.http.HttpSession");
            String username = JWTUtil.getUsername(token);

            if (type.equals(TYPE)) {  // 私聊
                String toName = value.getToName(); // 获取发给谁
                // 聊天消息存储数据库
                Chatting chatting = new Chatting(username, toName, content, DateUtil.date().toString());
                try {
                    //region 采用这种方式注入的原因
                    //本质原因：spring管理的都是单例（singleton），和 websocket （多对象）相冲突。
                    // 详细解释：项目启动时初始化，会初始化 websocket （非用户连接的），spring 同时会为其注入 service，该对象的 service 不是
                    // null，被成功注入。但是，由于 spring 默认管理的是单例，所以只会注入一次 service。当新用户进入聊天时，系统又会创建一个新的
                    // websocket 对象，这时矛盾出现了：spring 管理的都是单例，不会给第二个 websocket 对象注入 service，所以导致只要是用户连
                    // 接创建的 websocket 对象，都不能再注入了。
                    // 像 controller 里面有 service， service 里面有 dao。因为 controller，service ，dao 都有是单例，所以注入时不会报
                    // null。但是 websocket 不是单例，所以使用spring注入一次后，后面的对象就不会再注入了，会报null。
                    // ————————————————
                    //endregion
                    if (chattingMapper == null) {
                        this.chattingMapper = (ChattingMapper) SpringContextUtil.getBean("chattingMapper");
                    }
                    chattingMapper.insert(chatting);
                } catch (Exception e) {
                    log.error("数据库操作异常", e);
                    session.getBasicRemote().sendText("消息存储失败");
                }
                sendMessage(toName, MessageUtils.getMessage(false, username, content, 0), type);
            } else { // 群聊
                Integer contentType = value.getContentType();
                if (contentType == 5){ // 心跳检测
                    session.getAsyncRemote().sendText(MessageUtils.getMessage(true, null, "heartBeat", 5));
                    return;
                }
                try {
                    //进行数据库存储
                    GroupChat groupChat = new GroupChat();
                    groupChat.setContent(content)
                            .setUsername(username)
                            .setCreateDate(String.valueOf(DateUtil.date()))
                            .setContentType(contentType)
                            .setChatName("jigechat");
                    if (groupChatMapper == null) {
                        this.groupChatMapper = (GroupChatMapper) SpringContextUtil.getBean("groupChatMapper");
                    }
                    groupChatMapper.insert(groupChat);
                    sendMessage(null, MessageUtils.getMessage(false, username, content, contentType), type);
                } catch (BeansException e) {
                    log.error("数据库操作异常", e);
                    session.getBasicRemote().sendText("消息存储失败");
                }
            }
        } catch (Exception e) {
            log.error("消息处理异常", e);
        }
    }

    /**
     * 关闭时
     *
     * @param session 一场
     */
    @OnClose
    public void onClose(Session session, @PathParam("type") String type) {
        LINK_NUM -= 1;
        String token = (String) session.getUserProperties().get("javax.servlet.http.HttpSession");
        String username = JWTUtil.getUsername(token);
        log.info("用户下线：" + username);

        if (type.equals("privacy")) { // 私聊
            onlinePrivacyUsers.remove(username);
            broadcastOnlineUsers(type);
        } else {
            onlineGroupUsers.remove(username);
            broadcastOnlineUsers(type);
        }

    }

    /**
     * 广播在线用户
     */
    private void broadcastOnlineUsers(String type) {
        String message = MessageUtils.getMessage(true, null, getOnlineUserNames(type), 0);
        broadcastMessage(message, type);
    }

    /**
     * 发送消息
     *
     * @param username 用户名
     * @param message  消息
     */
    private void sendMessage(String username, String message, String type) {
        if (type.equals(TYPE)){ // 发送给某个用户消息
            ChatEndPoint user = onlinePrivacyUsers.get(username);
            if (user != null) {
                user.session.getAsyncRemote().sendText(message);
            } else {
                log.warn("用户不在线：" + username);
                session.getAsyncRemote().sendText("用户不在线");
            }
        }else{
            // 群发消息
            for (ChatEndPoint user : onlineGroupUsers.values()) {
                user.session.getAsyncRemote().sendText(message);
            }
        }

    }

    /**
     * 获取联机用户名
     *
     * @return {@link Set}<{@link String}>
     */
    private Set<String> getOnlineUserNames(String type) {
        if (type.equals(TYPE)) {//私聊
            return onlinePrivacyUsers.keySet();
        } else { //群聊
            return onlineGroupUsers.keySet();
        }

    }

    /**
     * 广播消息
     *
     * @param message 消息
     */
    private void broadcastMessage(String message, String type) {
        if (type.equals(TYPE)) {// 私聊广播消息
            for (ChatEndPoint user : onlinePrivacyUsers.values()) {
                user.session.getAsyncRemote().sendText(message);
            }
        } else { // 群聊
            for (ChatEndPoint user : onlineGroupUsers.values()) {
                user.session.getAsyncRemote().sendText(message);
            }
        }

    }
}

