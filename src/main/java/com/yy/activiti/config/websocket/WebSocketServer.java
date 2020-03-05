package com.yy.activiti.config.websocket;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author YY
 * @date 2019/12/17
 * @description
 */
//@Component
@ServerEndpoint("/websocket/{id}")
@Slf4j
public class WebSocketServer implements Serializable {
    private static final long serialVersionUID = -5619737854398120893L;




    /**
     * 接收id(客户端标识)
     */
    private String id = "";

    /**
     * 记录当前在线连接数
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Set
     * 存放每个客户端对应的 WebSocketServer 对象
     */
    private static CopyOnWriteArraySet<WebSocketServer> webSocketServers = new CopyOnWriteArraySet<>();

    /**
     * 与客户端的连接会话，并通过与之发送消息
     */
    private Session session;

    /**
     * 连接成功调用
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        this.session = session;
        addOnlineCount();
        this.id = id;
        webSocketServers.add(this);
        log.info("有新窗口开始监听:" + id + ",当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭时调用
     */
    @OnClose
    public void onClose() {
        //从set中删除
        webSocketServers.remove(this);
        //在线数减1
        reduceOnlineCount();
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("收到来自窗口"+id+"的信息:"+message);
            try {
                this.sendMessage("服务端收到你的消息（"+message+"）向你("+id+")回复了消息");
            } catch (IOException e) {
                e.printStackTrace();
                log.error("发生错误{}",e.getMessage());
            }
    }

    @OnError
    public void onError(Throwable throwable) {
        log.error("发生错误{}",throwable.getMessage());
        throwable.printStackTrace();
    }

    /**
     * 服务器主动推送
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message+ DateUtil.current(true));
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message,String id)  {
        log.info("推送消息到窗口" + id + "，推送内容:" + message);
        if (StrUtil.isBlank(id)){
            webSocketServers.stream().forEach(f->{
                try {
                    f.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("{}发生错误{}",f.id,e.getMessage());
                }
            });
        }else {
            webSocketServers.stream().filter(item->StrUtil.equals(id,item.id)).forEach(item->{
                try {
                    item.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("{}发生错误{}", item.id,e.getMessage());
                }
            });
        }
    }

    private synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private synchronized void reduceOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    private int getOnlineCount() {
        return WebSocketServer.onlineCount;
    }


    /**
     * HashCode的时候大量使用了31这一数字
     * 该数字有一个很好的特性用移位和减法来代替乘法
     * 可以(左移五位)得到更好的性能
     * 31 * i == (32-1) * i == 32i - i = (i<<5) - i
     */
    @Override
    public int hashCode() {
        // 初始值
        int result = 17;
        result = result<<5 + (StrUtil.isBlank(id)?0:this.id.hashCode()) - result;
        result = result<<5 + (ObjectUtil.isNull(session) ?0:this.id.hashCode()) - result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        // instanceof 处理了obj为null的情况
        if (!(obj instanceof WebSocketServer)) {
            return false;
        }
        WebSocketServer webSocketServer = (WebSocketServer) obj;
        // 有需要可以去处理自定义认为相等的
        return StrUtil.equals(this.id,webSocketServer.id);
    }
}
