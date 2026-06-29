package pl.fejzu.persistence.core.failure;

import pl.fejzu.persistence.core.queue.SaveTask;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class FailureHandler {

    private static final Logger LOGGER = Logger.getLogger(FailureHandler.class.getName());

    public void onSaveFailure(SaveTask<?> task, Throwable cause) {
        LOGGER.log(Level.SEVERE, "Failed to save entity: " + task.getEntity().getId(), cause);
    }

    public void onLoadFailure(Object id, Throwable cause) {
        LOGGER.log(Level.SEVERE, "Failed to load entity with id: " + id, cause);
    }

    public void onDeleteFailure(Object id, Throwable cause) {
        LOGGER.log(Level.SEVERE, "Failed to delete entity with id: " + id, cause);
    }

    public void onConnectionFailure(String providerName, Throwable cause) {
        LOGGER.log(Level.SEVERE, "Connection failure for provider: " + providerName, cause);
    }
}
