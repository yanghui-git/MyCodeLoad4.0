package com.test.flowable;

import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.test.flowable.FlowableApplication;
import org.test.flowable.controller.HelloController;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 测试 非会签 ${nrOfCompletedInstances >= 1}
 *
 * 表示只要有一个同意或者拒绝，这个 UserTask 就算过了。
 *
 */
@SpringBootTest(classes = FlowableApplication.class)
class FlowableApplicationTests3 {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;


    /**
     * 开启一个流程
     */
    @Test
    void askForLeave() {
        Map<String, Object> variables = new HashMap();
        //要会签的用户列表 zuzhang jingli dongshizhang
        variables.put("userTasks", Arrays.asList(new String[]{"zuzhang", "jingli", "dongshizhang"}));
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayRequestHQ", variables);
        logger.info("创建请假流程 processId：{}", processInstance.getId());
    }

    /**
     * 组长的 待审批列表,批准
     */
    @Test
    public void leaveList() {
        //找到所有分配给你的任务
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("zuzhang").list();
        for (Task task : tasks) {
            logger.info("任务 ID：{}；任务处理人：{}；任务是否挂起：{}", task.getId(), task.getAssignee(), task.isSuspended());
            //获取流程参数
            Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());
            //只要任一操作存在拒绝,那么你这个会签的最终结果就是拒绝
            if (variables.containsKey("approved") && Boolean.parseBoolean(variables.get("approved").toString()) == false) {
                variables.put("approved", false);
            } else {
                variables.put("approved", true);
            }
            //设置流程varible
            runtimeService.setVariables(task.getExecutionId(), variables);
            taskService.complete(task.getId(), variables);
        }
    }

    /**
     * 经理的 待审批列表,批准
     */
    @Test
    public void leaveList2() {
        //找到所有分配给你的任务
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("jingli").list();
        for (Task task : tasks) {
            logger.info("任务 ID：{}；任务处理人：{}；任务是否挂起：{}", task.getId(), task.getAssignee(), task.isSuspended());
            //获取流程参数
            Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());
            //只要任一操作存在拒绝,那么你这个会签的最终结果就是拒绝
            if (variables.containsKey("approved") && Boolean.parseBoolean(variables.get("approved").toString()) == false) {
                variables.put("approved", false);
            } else {
                variables.put("approved", true);
            }
            //设置流程varible
            runtimeService.setVariables(task.getExecutionId(), variables);
            taskService.complete(task.getId(), variables);
        }
    }


    /**
     * 经理的 测试经理驳回 测试会签有一个失败  打印会签失败 会签失败。。。
     */
    @Test
    public void leaveList22() {
        //找到所有分配给你的任务
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("jingli").list();
        for (Task task : tasks) {
            logger.info("任务 ID：{}；任务处理人：{}；任务是否挂起：{}", task.getId(), task.getAssignee(), task.isSuspended());
            //获取流程参数
            Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());
            variables.put("approved", false);
            //设置流程varible
            runtimeService.setVariables(task.getExecutionId(), variables);

            taskService.complete(task.getId(), variables);
        }
    }

    /**
     * 董事长的 待审批列表,批准
     */
    @Test
    public void leaveList3() {
        //找到所有分配给你的任务
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("dongshizhang").list();
        for (Task task : tasks) {
            logger.info("任务 ID：{}；任务处理人：{}；任务是否挂起：{}", task.getId(), task.getAssignee(), task.isSuspended());
            //获取流程参数
            Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());
            //只要任一操作存在拒绝,那么你这个会签的最终结果就是拒绝
            if (variables.containsKey("approved") && Boolean.parseBoolean(variables.get("approved").toString()) == false) {
                variables.put("approved", false);
            } else {
                variables.put("approved", true);
            }
            //设置流程varible
            runtimeService.setVariables(task.getExecutionId(), variables);

            taskService.complete(task.getId(), variables);
        }
    }

    /**
     * 终止流程
     */
    @Test
    public void stopProcessInstanceById(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance != null) {
            //1、获取终止节点
            List<EndEvent> endNodes = findEndFlowElement(processInstance.getProcessDefinitionId());
            String endId = endNodes.get(0).getId();
            //2、执行终止
            List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstanceId).list();
            List<String> executionIds = new ArrayList<>();
            executions.forEach(execution -> executionIds.add(execution.getId()));
            runtimeService.createChangeActivityStateBuilder().moveExecutionsToSingleActivityId(executionIds, endId).changeState();
            // log.info("终止processInstanceId:{}胜利", processInstanceId);
        } else {
            // log.info("不存在运行的流程实例processInstanceId:{},请确认!", processInstanceId);
        }
    }

    public List findEndFlowElement(String processDefId) {
        Process mainProcess = repositoryService.getBpmnModel(processDefId).getMainProcess();
        Collection<FlowElement> list = mainProcess.getFlowElements();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        return list.stream().filter(f -> f instanceof EndEvent).collect(Collectors.toList());
    }
}

