package test.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class SimpleSpinningLock {

    /**
     * 持有锁的线程，null表示锁未被线程持有
     */
    private AtomicReference<Thread> ref = new AtomicReference<>();

    public void lock() {
        Thread currentThread = Thread.currentThread();
        while (!ref.compareAndSet(null, currentThread)) {
            //当ref为null的时候compareAndSet返回true，反之为false
            //通过循环不断的自旋判断锁是否被其他线程持有
        }
    }

    public void unLock() {
        Thread cur = Thread.currentThread();
        if (ref.get() != cur) {
            //exception ...
        }

        log.info("线程已释放锁" + cur);

        ref.set(null);
    }
}

