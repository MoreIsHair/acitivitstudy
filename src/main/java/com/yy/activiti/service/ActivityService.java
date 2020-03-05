package com.yy.activiti.service;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author YY
 * @date 2019/8/24
 * @description 工作流相关可能用到的相关服务 （与业务无关）
 */
@Service
@Slf4j
public class ActivityService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 启动流程
     */
    public void startProcesses(String pdKey, String businessKey, Map<String, Object> map) {
        // 流程Key，业务表id
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(pdKey, businessKey, map);
        log.debug("流程启动成功，流程id:{}", pi.getId());
    }

    /**
     * 查询所有部署的key
     */
    public List<String> findAllDeployKey() {
        return repositoryService.createDeploymentQuery().list().stream().map(Deployment::getKey).collect(Collectors.toList());
    }

    /**
     * 查询所有流程定义的key
     */
    public List<String> findAllPDKey() {
        return repositoryService.createProcessDefinitionQuery().list().stream().map(ProcessDefinition::getKey).collect(Collectors.toList());
    }

    /**
     * <p>描述: 根据用户id查询待办任务列表</p>
     * 根据状态以及流程分类
     */
    public Map<String, Map<String, List<Task>>> findTasksByUserId(String userId) {
        List<Task> resultTask = taskService.createTaskQuery().taskAssignee(userId).list();
        Map<String, List<Task>> collect = resultTask.stream().collect(Collectors.groupingBy(Task::getName));
        HashMap<String, Map<String, List<Task>>> stringHashMapHashMap = new HashMap<>(16);
        collect.forEach((key, value) -> {
            Map<String, List<Task>> collect1 = value.stream().collect(Collectors.groupingBy(Task::getTaskDefinitionKey));
            stringHashMapHashMap.put(key, collect1);
        });
        return stringHashMapHashMap;
    }

    /**
     * 通过任务ID查看流程中的任务对象
     */
    public Task findTaskById(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    /**
     * <p>描述:完成任务（任务审批）</p>
     * 可以在map中与流程图中对应的参数，从而控制流程分支方向
     *
     * @param taskId 任务id
     */
    public void completeTask(String taskId, Map<String, Object> map) {
        taskService.complete(taskId, map);
    }

    /**
     * 查找用户参与过的历史流程（包含流程中的任务）
     * <p>
     * *   返回的数据:
     * *   *   map<processDefinitionId,<processInstanceId,historicTaskInstance>>
     * </p>
     */
    public Map<String, Map<String, List<HistoricTaskInstance>>> findAllHistoryByAssignee(String assignee) {
        HashMap<String, Map<String, List<HistoricTaskInstance>>> history = new HashMap<>(16);
        Map<String, List<HistoricTaskInstance>> collect = historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee).list()
                .stream().collect(Collectors.groupingBy(HistoricTaskInstance::getProcessDefinitionId));
        collect.forEach((key, value) -> {
            Map<String, List<HistoricTaskInstance>> collect1 = value.stream().collect(Collectors.groupingBy(HistoricTaskInstance::getProcessInstanceId));
            history.put(key, collect1);
        });
        return history;
    }

}
