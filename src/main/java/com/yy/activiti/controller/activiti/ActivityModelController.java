package com.yy.activiti.controller.activiti;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @author YY
 * @date 2019/8/24
 * @description
 */
@Slf4j
@RestController
@Api(value = "在线流程设计模型")
@RequestMapping("/model")
public class ActivityModelController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 模型列表
     */
    @ApiOperation(value = "模型列表")
    @GetMapping("/list")
    public List<Model> list() {
        return repositoryService.createModelQuery().list();

    }
    /**
     * 创建模型
     */
    @GetMapping("/create")
    public void create(HttpServletRequest request, HttpServletResponse response) {
        try {
            //初始化一个空模型
            Model model = repositoryService.newModel();
            //设置一些默认信息
            String name = "new-process";
            String description = "";
            int revision = 1;
            String key = "process";
            ObjectNode modelNode = objectMapper.createObjectNode();
            modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);
            model.setName(name);
            model.setKey(key);
            model.setMetaInfo(modelNode.toString());
            repositoryService.saveModel(model);
            String id = model.getId();
            // 完善ModelEditorSource
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace",
                    "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.replace("stencilset", stencilSetNode);
            repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"));
            response.sendRedirect(request.getContextPath() + "/static/modeler.html?modelId=" + id);
        } catch (Exception e) {
            log.debug("创建模型失败");
        }
    }

    /**
     * 根据Model部署流程
     */
    @ApiOperation(value = "根据Model部署流程")
    @PostMapping("/deploy/{modelId}")
    public String deploy(@ApiParam(value = "modelId",name = "modelId",required = true) @PathVariable("modelId") String modelId) {
        try {
            Model modelData = repositoryService.getModel(modelId);
            ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            byte[] bpmnBytes = null;

            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            bpmnBytes = new BpmnXMLConverter().convertToXML(model);

            String processName = modelData.getName() + ".bpmn20.xml";
            Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes)).deploy();
            return "部署成功，部署ID=" + deployment.getId();
        } catch (Exception e) {
            log.error("根据模型部署流程失败：modelId={}", modelId, e);
            return "根据模型部署流程失败：modelId="+modelId;
        }
    }


    @ApiOperation(value = "根据Model导出xml文件")
    @GetMapping("/export/{modelId}")
    public void export(@ApiParam(value = "modelId",name = "modelId",required = true) @PathVariable("modelId") String modelId, HttpServletResponse response) {
        try {
            Model modelData = repositoryService.getModel(modelId);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            IOUtils.copy(in, response.getOutputStream());
            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.flushBuffer();
        } catch (Exception e) {
            log.error("导出model的xml文件失败：modelId={}", modelId, e);
        }
    }
    @ApiOperation(value = "删除模型")
    @DeleteMapping(value = "/delete/{modelId}")
    public void delete(@ApiParam(value = "modelId",name = "modelId",required = true) @PathVariable("modelId") String modelId) {
        repositoryService.deleteModel(modelId);
    }
}
