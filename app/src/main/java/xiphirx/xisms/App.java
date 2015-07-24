package xiphirx.xisms;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import xiphirx.xisms.concurrency.NamedPrioritizedThreadFactory;

public class App extends Application {
    private static final Looper MAIN_LOOPER = Looper.getMainLooper();

    private static ExecutorService sExecutor;
    private static ScheduledExecutorService sScheduler;

    private static final Handler sHandler = new Handler(MAIN_LOOPER);

    static {
        final ThreadFactory threadFactory = new NamedPrioritizedThreadFactory("application");
        sExecutor = new ThreadPoolExecutor(4, 10, 5, TimeUnit.SECONDS,
                            new LinkedBlockingDeque<Runnable>(), threadFactory);

        final ThreadFactory scheduledThreadFactory
                = new NamedPrioritizedThreadFactory("application-scheduled");
        sScheduler = Executors.newSingleThreadScheduledExecutor(scheduledThreadFactory);
    }

    public static SmsManager sms() {
        return SmsManager.getDefault();
    }

    public static Future<?> runInBackground(final Runnable runnable) {
        return sExecutor.submit(runnable);
    }

    public static Future<?> scheduleInBackground(final Runnable runnable, final long delayMs) {
        if (delayMs <= 0) {
            return runInBackground(runnable);
        }

        return sScheduler.schedule(runnable, delayMs, TimeUnit.MILLISECONDS);
    }

    public static void runInMain(final Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }

    public static Future<?> scheduleInMain(final Runnable runnable, final long delayMs) {
        final FutureTask<?> futureTask = new FutureTask<>(runnable, null);
        sHandler.postDelayed(futureTask, delayMs);
        return futureTask;
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == MAIN_LOOPER;
    }
}
