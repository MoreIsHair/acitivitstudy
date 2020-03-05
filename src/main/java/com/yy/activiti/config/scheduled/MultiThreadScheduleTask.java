package com.yy.activiti.config.scheduled;

import cn.hutool.core.lang.Console;
import com.yy.activiti.config.websocket.WebSocketServer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * @author YY
 * @date 2019/12/18
 * @description
 *      * 1.基于注解(@Scheduled)
 *          *默认为单线程，开启多个任务时，任务的执行时机会受上一个任务执行时间的影响。
 *      * 2.基于接口（SchedulingConfigurer）
 *      * 3.基于注解设定多线程定时任务
 */
//@Component
public class MultiThreadScheduleTask {

    @Async
    @Scheduled(cron = "0 */1 * * * * ")
    public void first(){
        Console.log("第一个定时任务开始 : {},线程：{}",LocalDateTime.now().toLocalTime(),Thread.currentThread().getName());
        WebSocketServer.sendInfo("定时任务推送socket数据"+Thread.currentThread().getName(),null);
    }
}
