package xiphirx.xisms.concurrency;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread factory that allows you to name threads and specify a default priority
 */
public class NamedPrioritizedThreadFactory implements ThreadFactory {
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    private final AtomicInteger mThreadNumber = new AtomicInteger(1);
    private final ThreadGroup mGroup;
    private final String mNamePrefix;
    private final int mPriority;

    /**
     * Creates a new instance with the specified name utilized for all threads / pools created
     * </p>
     * This also sets the default priority to NORM_PRIORITY - 3
     *
     * @param name to be utilized for all threads / pools created from this factory
     */
    public NamedPrioritizedThreadFactory(final String name) {
        this(name, Thread.NORM_PRIORITY - 3);
    }

    public NamedPrioritizedThreadFactory(final String name, final int defaultPriority) {
        SecurityManager securityManager = System.getSecurityManager();
        mGroup = (securityManager != null) ?
                securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        mNamePrefix = name + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        mPriority = defaultPriority;
    }

    /**
     * Creates a new thread that is named by the previously set name when instantiating this factory
     * The priority of the thread is set to the default priority previously set when instantiating
     * the factory instance. The default priority utilized by this class is NORM_PRIORIY - 3
     *
     * @param runnable to run on a thread
     * @return a new {@link Thread}
     */
    public Thread newThread(final Runnable runnable) {
        final Thread thread
                = new Thread(mGroup, runnable, mNamePrefix + mThreadNumber.getAndIncrement(), 0);
        thread.setDaemon(false);
        thread.setPriority(mPriority);

        return thread;
    }

    /**
     * @return the default priority
     */
    public int getDefaultPriority() {
        return mPriority;
    }
}
