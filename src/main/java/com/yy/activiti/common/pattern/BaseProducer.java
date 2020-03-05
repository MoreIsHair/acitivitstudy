package com.yy.activiti.common.pattern;

import java.util.List;

/**
 * @author YY
 * @date 2019/12/23
 * @description 生产者抽象类
 */
public abstract class BaseProducer<T> implements Runnable {

    private BlockedQueue<T> blockedQueue;

    public BaseProducer(BlockedQueue<T> blockedQueue) {
        this.blockedQueue = blockedQueue;
    }

    @Override
    public void run() {
        // 循环为了让线程不停止，一直在工作（产生任务，入列）
        while (true){
            List<T> ts = this.generateTask();
            if (ts != null && ts.size() >0){
                ts.forEach(e-> blockedQueue.enterQueue(e));
            }
        }
    }

    /**
     * 生产任务的具体实现（“模板方法”设计模式，子类需要要实现此方法）
     * @return
     */
    abstract List<T> generateTask();

}
