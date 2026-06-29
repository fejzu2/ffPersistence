package pl.fejzu.persistence.sync.annotation;

import pl.fejzu.persistence.sync.SyncScope;
import pl.fejzu.persistence.sync.SyncStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SynchronizedField {

    SyncStrategy strategy() default SyncStrategy.INSTANT;

    long delayMs() default 0;

    SyncScope scope() default SyncScope.SERVER;

    boolean includeInSnapshot() default true;
}
