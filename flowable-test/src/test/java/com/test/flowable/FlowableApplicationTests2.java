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
 * 测试会签 非会签
 * <p>
 * http://localhost:8001/pic?processId=8bdcfac0-cfa2-11ed-a6e2-4ed2648c1dc4  获取当前执行流程图
 * <p>
 * https://juejin.cn/post/7140903040562757668#comment
 * <p>
 * ${nrOfCompletedInstances == nrOfInstances}
 * <p>
 * 完成条件（多实例）：这里我配置的值是 ${nrOfCompletedInstances== nrOfInstances}，涉及到两个变量，nrOfCompletedInstances 这个表示已经完成审批的实例个数，nrOfInstances 则表示总共的实例个数，也就是当完成审批的实例个数等于总的实例个数的时候，这个节点就算执行完了，换句话说，也就是 zhangsan 将请假申请提交给 javaboy 和 lisi，必须这两个人都审批了，这个节点才算执行完。另外这里还有一个内置的变量可用就是 nrOfActiveInstances 表示未完成审批的实例个数，只不过在本案例中没有用到这个内置变量。
 */
@SpringBootTest(classes = FlowableApplication.class)
class FlowableApplicationTests2 {

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
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayRequest", variables);
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
            List<String> executionIds = new ArrayList<String>();
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

