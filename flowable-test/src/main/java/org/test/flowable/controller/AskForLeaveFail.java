package org.test.flowable.controller;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * 拒绝 - 回调接口
 */
public class AskForLeaveFail implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("请假失败。。。");
    }
}
