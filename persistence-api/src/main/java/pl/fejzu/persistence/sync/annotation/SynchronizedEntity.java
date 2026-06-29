package pl.fejzu.persistence.sync.annotation;

import pl.fejzu.persistence.sync.SyncDirection;
import pl.fejzu.persistence.sync.SyncScope;
import pl.fejzu.persistence.sync.SyncStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SynchronizedEntity {

    SyncScope scope() default SyncScope.SERVER;

    SyncStrategy defaultStrategy() default SyncStrategy.INSTANT;

    SyncDirection direction() default SyncDirection.BIDIRECTIONAL;
}
