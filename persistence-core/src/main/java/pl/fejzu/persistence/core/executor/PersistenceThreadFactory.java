package pl.fejzu.persistence.core.executor;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public final class PersistenceThreadFactory implements ThreadFactory {

    private final String namePrefix;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, namePrefix + "-" + counter.incrementAndGet());
        thread.setDaemon(true);
        return thread;
    }
}
