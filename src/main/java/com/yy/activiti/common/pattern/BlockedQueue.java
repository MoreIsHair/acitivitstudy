package com.yy.activiti.common.pattern;

import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author YY
 * @date 2019/12/23
 * @description 生产者与消费者的缓冲区，泛型T为任务类型
 * <p>  阻塞线程以及唤醒线程必须要使用finally里释放锁 </p>
 * <p>  也可以通过concurrent下的阻塞队列（BlockingQueue，线程同步的）做缓冲区</p>
 */
public class BlockedQueue<T> {

    /**
     * 可重入锁
     */
    private  final Lock lock  = new ReentrantLock();

    /**
     * 生产者进行生产
     */
    private final Condition notFull = lock.newCondition();

    /**
     * 消费者进行消费
     */
    private final Condition notEmpty = lock.newCondition();


    /**
     * 任务集合（线程安全的）
     */
    private Vector<T> taskQueue = new Vector<>();

    /**
     * 队列最大容量
     */
    private final int capacity;

    public BlockedQueue() {
        // 获取当前CPU核数
        this.capacity = Runtime.getRuntime().availableProcessors();
    }

    public BlockedQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 入列操作
     */
    public void enterQueue(T t){
        lock.lock();
        System.out.println("size: " + taskQueue.size() + " capacity: " + capacity);
        try {
            while (taskQueue.size() == capacity){
                // 队列满了之后等待，等待队列不满
                notFull.await();
            }
            System.out.println(Thread.currentThread().getName() + " add task: " + t.toString());
            taskQueue.add(t);
            // 入队后, 通知队列不空了，可以出队
            notEmpty.signal();
        }catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 出列操作
     */
    public T leaveQueue(){
        lock.lock();
        try {
            // 自旋锁 ，不能使用if
            while (taskQueue.size() == 0){
                notEmpty.await();
            }
            // 出队后，通知队列不满，可以继续入队
            notFull.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
        return taskQueue.remove(0);
    }

}
