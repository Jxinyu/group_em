package com.gp.ws;

import com.gp.utils.JWTUtil;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 得到http会话配置器
 *
 * @author ljb
 * @create 2023/6/26
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    /**
     * 修改握手
     *
     * @param sec      证券交易委员会
     * @param request  请求
     * @param response 响应
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 获取cookie 从getHeaders拿到token
        String[] cookies = request.getHeaders().get("cookie").get(0).split("=");
        String cookie = cookies[cookies.length - 1];

        // 将token对象存储到对象中
        sec.getUserProperties().put(HttpSession.class.getName(), cookie);
    }
}
