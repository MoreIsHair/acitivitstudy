package com.yy.activiti.controller.business;

import cn.hutool.core.map.MapUtil;
import com.yy.activiti.service.ActivityService;
import com.yy.activiti.service.WorkFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YY
 * @date 2019/8/26
 * @description
 */
@RestController
@Api("业务流程API")
public class WorkFlowController {

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/findAllKey")
    public Map findAllKey(){
        HashMap<String, List<String>> deloy = MapUtil.of("deloy", activityService.findAllDeployKey());
        deloy.put("pdkey",activityService.findAllPDKey());
        return deloy;
    }

    @GetMapping("/findAllHistoryByAssignee")
    public Map findAllHistoryByAssignee(){
        return activityService.findAllHistoryByAssignee("zs");
    }

    @PostMapping("/buildingSite/{key}")
    @ApiOperation("建立站点")
    public void buildingSite(@ApiParam("key") @PathVariable("key") String key){
        // 业务(站点表中插入数据)
        // 发起个人流程
        workFlowService.buildingSite(key);
    }

    @PostMapping("/dispatchTask/{key}")
    @ApiOperation("派单给勘察人员")
    public void dispatchTask(@ApiParam("key") @PathVariable("key") String survey,String[] taskIds){
        // 业务(更新站点转态：待派发->待勘察)
        // 流转
        workFlowService.dispatchTask(survey,taskIds);
    }

    @PostMapping("/distribute/{userId}")
    @ApiOperation("派单给项目经理")
    public void distribute(@ApiParam(value = "manager") @PathVariable("manager") String manager,String[] taskIds){
        // 控制流程流转
        workFlowService.distribute(manager,taskIds);
    }

    @GetMapping("/upload")
    @ApiOperation("上传文件")
    public void completeTask(@ApiParam(value = "file",required = true)@RequestParam("file") MultipartFile file){
    }


    @PostMapping("/getSurveyForm")
    @ApiOperation("获取勘察数据页面表单以及数据")
    public void getSurveyForm(@ApiParam(value = "taskId") @PathVariable("taskId") String taskId,
                               @ApiParam(value = "isapprove") @PathVariable("isapprove")String isapprove){
        // 存在一个动态表单
        // 存在与手机端交互
    }

    @PostMapping("/saveSurveyForm")
    @ApiOperation("保存勘察数据")
    public void saveSurveyForm(@ApiParam(value = "taskId") @PathVariable("taskId") String taskId){
        // 业务
        // 流转
        // 存在与手机端交互
        workFlowService.saveSurveyForm(taskId);
    }

    @PostMapping("/Analytics")
    @ApiOperation("智能分析")
    public void analytics(){
        // 调用CAD接口
    }

    @PostMapping("/saveAnalytics")
    @ApiOperation("保存智能分析")
    public void saveAnalytics(){

    }

    @PostMapping("/finishDesign")
    @ApiOperation("送审")
    public void finishDesign(String taskId){
        // 业务
        // 流转
        workFlowService.finishDesign(taskId);
    }

    @PostMapping("/verifyQuestionList")
    @ApiOperation("审核设计出现的问题列表")
    public void verifyQuestionList(){

    }

    @PostMapping("/historyVerifyRecord")
    @ApiOperation("历史审核记录")
    public void historyVerifyRecord(){

    }

    @PostMapping("/verifyDesign")
    @ApiOperation("审核设计")
    public void verifyDesign(@ApiParam(value = "taskId",name = "taskId") @RequestParam("taskId") String taskId,
            @ApiParam(value = "是否通过",name = "result") @RequestParam("result") String result){
        // 业务
        // 流转（通过，或者驳回）
        workFlowService.verifyDesign(taskId,result);
    }

    @PostMapping("/keepRecord")
    @ApiOperation("归档")
    public void keepRecord(String taskId){
        // 业务（业务数据中心插入响应的站点归档记录，以及修改站点状态）
        workFlowService.keepRecord(taskId);
    }

    @PostMapping("/keepRecordList")
    @ApiOperation("已归档列表")
    public void keepRecordList(String taskId){

    }


    @PostMapping("/taskList/{userId}")
    @ApiOperation("个人所有待办任务")
    public Map<String, Map<String, List<Task>>> taskList(@ApiParam(value = "userId") @PathVariable("userId") String userId){
        return activityService.findTasksByUserId(userId);
    }

    @PostMapping("/taskListByStatus/{userId}/{status}")
    @ApiOperation("个人不同阶段待办任务列表")
    public void taskListByStatus(@ApiParam(value = "userId") @PathVariable("userId") String userId,
                       @ApiParam(value = "userId") @PathVariable("status") String status){
    }

}
