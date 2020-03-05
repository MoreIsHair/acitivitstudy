package com.yy.activiti.common.pattern;

/**
 * @author YY
 * @date 2019/12/23
 * @description 消费者抽象类
 */
public abstract class BaseConsumer<T> implements Runnable {
    private BlockedQueue<T> blockedQueue;

    public BaseConsumer(BlockedQueue<T> blockedQueue) {
        this.blockedQueue = blockedQueue;
    }

    @Override
    public void run() {
        // 循环为了让线程不停止，一出列，消费任务
        while (true){
            this.exec(blockedQueue.leaveQueue());
        }
    }

    /**
     * 消费任务的具体处理（消费者子类需要实现该方法）
     * @param t 任务
     */
    abstract void exec(T t);
}
