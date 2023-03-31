package org.test.flowable.controller;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * 拒绝 - 回调接口
 */
public class HQFail implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("会签失败。。。");
    }
}
