package com.yy.activiti.controller.websocket;

import com.yy.activiti.config.websocket.WebSocketServer;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YY
 * @date 2019/12/18
 * @description 用来测试websocket（跳转js的html，以及模拟服务器主动向客户端推送请求）
 */
@Controller
@Api(tags = "websocket test api")
@RequestMapping("/checkcenter")
public class CheckCenterController {


    @GetMapping("/index/{userId}")
    public String socket(@PathVariable String userId, Model model) {
        model.addAttribute("userId",userId);
        return "socket";
    }

    /**
     *推送数据接口
     */
    @ResponseBody
    @GetMapping("/socket/push/{cid}")
    public Map pushToWeb(@PathVariable String cid) {
        Map result = new HashMap(16);
            String message = "欢迎您"+cid;
            WebSocketServer.sendInfo(message,cid);
            result.put("code", 200);
            result.put("msg", "success");
            return result;
    }
}
