package test.lock;

import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * https://juejin.cn/post/6844903838349000717#comment java 锁自旋
 * <p>
 * 自旋就是在循环判断条件是否满足，那么会有什么问题吗？如果锁被占用很长时间的话，自旋的线程等待的时间也会变长，白白浪费掉处理器资源。
 * 因此在JDK中，自旋操作默认10次，我们可以通过参数“-XX:PreBlockSpin”来设置，当超过来此参数的值，则会使用传统的线程挂起方式来等待锁释放。
 */
public class TestLock {

    static int count = 0;

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        final SimpleSpinningLock simpleSpinningLock = new SimpleSpinningLock();
        for (int i = 0; i < 100; i++) {
            executorService.execute(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    simpleSpinningLock.lock();
                    //假设程序执行2
                    Thread.sleep(20l);
                    ++count;
                    simpleSpinningLock.unLock();
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();
        System.out.println(count);
    }
}

// 多次执行输出均为：100 ，实现了锁的基本功能
