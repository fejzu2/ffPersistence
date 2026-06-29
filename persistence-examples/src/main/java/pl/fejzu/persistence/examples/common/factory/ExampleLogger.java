package pl.fejzu.persistence.examples.common.factory;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExampleLogger {

    private final Logger logger;

    private ExampleLogger(String name) {
        this.logger = Logger.getLogger(name);
    }

    public static ExampleLogger of(Class<?> clazz) {
        return new ExampleLogger(clazz.getName());
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warning(message);
    }

    public void error(String message, Throwable cause) {
        logger.log(Level.SEVERE, message, cause);
    }

    public void error(String message) {
        logger.severe(message);
    }

    public void debug(String message) {
        logger.fine(message);
    }
}
