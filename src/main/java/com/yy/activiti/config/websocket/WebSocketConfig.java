package com.yy.activiti.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author YY
 * @date 2019/8/28
 * @description
 *    * 也可以创建另一种基于STOMP协议的WebSocket（使用STOMP的好处在于，它完全就是一种消息队列模式）
 *    *  * 继承AbstractWebSocketMessageBrokerConfigurer
 */
// @Configuration
public class WebSocketConfig  {


    /**
     *  *   1. ServerEndpointExporter 是由Spring官方提供的标准实现，
     *  *           用于扫描ServerEndpointConfig配置类和@ServerEndpoint注解实
     *  *   2. 如果使用默认的嵌入式容器 比如Tomcat 则必须手工在上下文提供ServerEndpointExporter。
     *  *   3. 如果使用外部容器部署war包，则不需要提供提供ServerEndpointExporter，
     *  *           因为此时SpringBoot默认将扫描服务端的行为交给外部容器处理，
     *  *           所以需要把WebSocketConfig中这段注入bean的代码注掉。
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
