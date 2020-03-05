package com.yy.activiti.service;

import com.yy.activiti.common.constant.WorkFlowUserStatusName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

/**
 * @author YY
 * @date 2019/8/27
 * @description 流程与业务整合service
 */
@Service
public class WorkFlowService {

    @Autowired
    public ActivityService activityService;


    /**
     * 建站
     * 开始流程
     *
     * @param key
     */
    @Transactional(rollbackFor = Exception.class)
    public void buildingSite(String key) {
        // 建站业务

        // 开起流程
        HashMap<String, Object> map = new HashMap<>(16);
        map.put(WorkFlowUserStatusName.MANAGER, "zs");
        map.put(WorkFlowUserStatusName.SURVEY, "ls");
        map.put(WorkFlowUserStatusName.DESIGNER, "ww");
        map.put(WorkFlowUserStatusName.AUDITOR, "zl");
        map.put(WorkFlowUserStatusName.STORER, "sq");
        activityService.startProcesses(key, null, map);
    }

    /**
     * 派单给勘察人员
     *
     * @param survey
     * @param taskIds
     */
    public void dispatchTask(String survey, String[] taskIds) {
        //TODO 更新业务状态
        // 任务流转
        for (String s : taskIds) {
            HashMap<String, Object> map = new HashMap<>(16);
            map.put(WorkFlowUserStatusName.FLAG, true);
            activityService.completeTask(s, map);
        }
    }

    /**
     * 派单给项目经理
     *
     * @param manager
     * @param taskIds
     */
    public void distribute(String manager, String[] taskIds) {
        //TODO 更新业务状态
        // 流转
        for (String taskId : taskIds) {
            HashMap<String, Object> map = new HashMap<>(16);
            map.put(WorkFlowUserStatusName.FLAG, false);
            map.put(WorkFlowUserStatusName.MANAGER, manager);
            activityService.completeTask(taskId, map);
        }
    }

    /**
     * 保存勘察数据（）
     *
     * @param taskId
     */
    public void saveSurveyForm(String taskId) {
        // TODO 业务
        // 流转到设计阶段
        activityService.completeTask(taskId, new HashMap<>(16));
    }

    /**
     * 送审
     *
     * @param taskId
     */
    public void finishDesign(String taskId) {
        // TODO 业务
        // 流转到审核阶段
        activityService.completeTask(taskId,new HashMap<>(16));
    }

    /**
     * 审核设计
     *
     * @param taskId
     */
    public void verifyDesign(String taskId, String result) {
        // TODO 业务 ，修改站点状态
        // 流转到归档阶段
        activityService.completeTask(taskId,new HashMap<>(16));
    }

    /**
     * 归档
     *
     * @param taskId
     */
    public void keepRecord(String taskId) {
        // TODO 业务
        // 写入业务表中（具体业务数据不应该使用activiti去保存），以供后续查看，修改转态
        // 流转结束
        activityService.completeTask(taskId,new HashMap<>(16));
    }
}
