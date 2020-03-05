package com.yy.activiti.common.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YY
 * @date 2019/12/23
 * @description
 */
public class PBTest {
    public static void main(String[] args) {
        BlockedQueue<String> taskQueue = new BlockedQueue<>(10);
        for (int i = 0; i < 2; i++) {
            String producerName = "Producer"+i;
            Thread producer = new Thread(new BaseProducer<String>(taskQueue){
                @Override
                List<String> generateTask() {
                    ArrayList<String> strings = new ArrayList<>(16);
                    for (int i1 = 0; i1 < 10; i1++) {
                        long timestamp = System.currentTimeMillis();
                        strings.add("Task_" + timestamp + "_" + i1);
                    }
                    return strings;
                }
            },producerName);
            producer.start();
        }
        for (int i = 0; i < 5; i++) {
            String consumerName = "Consumer-" + i;
            Thread consumer = new Thread(new BaseConsumer<String>(taskQueue) {
                @Override
                public void exec(String task) {
                    System.out.println(Thread.currentThread().getName() + " do task [" + task + "]");
                    // 休眠一会，模拟任务执行耗时
                    sleep(2000);
                }

                private void sleep(long millis) {
                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, consumerName);
            consumer.start();
        }
    }
}
