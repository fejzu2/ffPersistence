package pl.fejzu.persistence.core.settings;

import pl.fejzu.persistence.settings.*;

import java.util.ArrayList;
import java.util.List;

public final class SettingsValidator {

    public ValidationResult validate(PersistenceSettings settings) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        validateCache(settings.getCache(), errors, warnings);
        validateLoading(settings.getLoading(), errors, warnings);
        validatePlayerLifecycle(settings.getPlayerLifecycle(), errors, warnings);
        validateProxy(settings.getProxy(), errors, warnings);
        validateSync(settings.getSync(), errors, warnings);
        validateSector(settings.getSector(), errors, warnings);
        validateStorage(settings.getStorage(), errors, warnings);
        validateCommunication(settings.getCommunication(), errors, warnings);

        return new ValidationResult(errors, warnings);
    }

    public void validateOrThrow(PersistenceSettings settings) {
        ValidationResult result = validate(settings);
        if (!result.isValid()) {
            throw new InvalidSettingsException(result);
        }
    }

    private void validateCache(CacheSettings cache, List<String> errors, List<String> warnings) {
        if (!cache.isEnabled()) return;
        if (cache.getMaxSize() <= 0) {
            errors.add("cache.maxSize must be > 0");
        }
        if (cache.getExpireAfterWrite().isNegative() || cache.getExpireAfterWrite().isZero()) {
            errors.add("cache.expireAfterWrite must be positive");
        }
        if (cache.getExpireAfterAccess().isNegative() || cache.getExpireAfterAccess().isZero()) {
            errors.add("cache.expireAfterAccess must be positive");
        }
        if (cache.getMaxSize() < 100) {
            warnings.add("cache.maxSize is very small (" + cache.getMaxSize() + "); consider increasing it");
        }
    }

    private void validateLoading(LoadingSettings loading, List<String> errors, List<String> warnings) {
        if (!loading.isEnabled()) return;
        if (loading.getLoadTimeoutSeconds() <= 0) {
            errors.add("loading.loadTimeoutSeconds must be > 0");
        }
        if (loading.getAnimationIntervalMs() <= 0) {
            errors.add("loading.animationIntervalMs must be > 0");
        }
        if (loading.getTitle() == null || loading.getTitle().isBlank()) {
            warnings.add("loading.title is blank; players will see an empty loading title");
        }
        if (loading.getLoadTimeoutSeconds() > 30) {
            warnings.add("loading.loadTimeoutSeconds is " + loading.getLoadTimeoutSeconds()
                + "s; long timeouts may cause players to be stuck on loading screen");
        }
    }

    private void validatePlayerLifecycle(PlayerLifecycleSettings lc, List<String> errors, List<String> warnings) {
        if (lc.getLoadTimeoutSeconds() <= 0) {
            errors.add("playerLifecycle.loadTimeoutSeconds must be > 0");
        }
        if (!lc.isSaveOnQuit() && !lc.isSaveOnShutdown()) {
            warnings.add("playerLifecycle: both saveOnQuit and saveOnShutdown are disabled; data will not be persisted automatically");
        }
        if (lc.getSaveInterval().isNegative() || lc.getSaveInterval().isZero()) {
            errors.add("playerLifecycle.saveInterval must be positive");
        }
        if (lc.getKickMessage() == null || lc.getKickMessage().isBlank()) {
            warnings.add("playerLifecycle.kickMessage is blank");
        }
    }

    private void validateProxy(ProxySettings proxy, List<String> errors, List<String> warnings) {
        if (!proxy.isEnabled()) return;
        if (proxy.getTransferTimeoutSeconds() <= 0) {
            errors.add("proxy.transferTimeoutSeconds must be > 0");
        }
        if (proxy.getFallbackServer() == null || proxy.getFallbackServer().isBlank()) {
            warnings.add("proxy.fallbackServer is blank; failed transfers will have no fallback");
        }
    }

    private void validateSync(SyncSettings sync, List<String> errors, List<String> warnings) {
        if (!sync.isEnabled()) return;
        if (sync.getDebounceDelayMs() < 0) {
            errors.add("sync.debounceDelayMs must be >= 0");
        }
        if (sync.getBatchInterval().isNegative() || sync.getBatchInterval().isZero()) {
            errors.add("sync.batchInterval must be positive");
        }
    }

    private void validateSector(SectorSettings sector, List<String> errors, List<String> warnings) {
        if (!sector.isEnabled()) return;
        if (sector.getTransferTimeoutSeconds() <= 0) {
            errors.add("sector.transferTimeoutSeconds must be > 0");
        }
        if (sector.getCurrentSector() == null || sector.getCurrentSector().isBlank()) {
            errors.add("sector.currentSector must not be blank when sectors are enabled");
        }
    }

    private void validateStorage(StorageSettings storage, List<String> errors, List<String> warnings) {
        if (storage.getConnectTimeoutMs() <= 0) {
            errors.add("storage.connectTimeoutMs must be > 0");
        }
        if (storage.getSocketTimeoutMs() <= 0) {
            errors.add("storage.socketTimeoutMs must be > 0");
        }
        if (storage.getMaxPoolSize() <= 0) {
            errors.add("storage.maxPoolSize must be > 0");
        }
        if (storage.getMaxRetries() < 0) {
            errors.add("storage.maxRetries must be >= 0");
        }
    }

    private void validateCommunication(CommunicationSettings comm, List<String> errors, List<String> warnings) {
        if (!comm.isEnabled()) return;
        if (comm.getRequestTimeoutMs() <= 0) {
            errors.add("communication.requestTimeoutMs must be > 0");
        }
        if (comm.getChannelsPrefix() == null || comm.getChannelsPrefix().isBlank()) {
            errors.add("communication.channelsPrefix must not be blank");
        }
    }

    public static final class ValidationResult {

        private final List<String> errors;
        private final List<String> warnings;

        ValidationResult(List<String> errors, List<String> warnings) {
            this.errors = List.copyOf(errors);
            this.warnings = List.copyOf(warnings);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return errors;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            errors.forEach(e -> sb.append("[ERROR] ").append(e).append('\n'));
            warnings.forEach(w -> sb.append("[WARN]  ").append(w).append('\n'));
            return sb.toString().stripTrailing();
        }
    }

    public static final class InvalidSettingsException extends RuntimeException {

        private final ValidationResult result;

        InvalidSettingsException(ValidationResult result) {
            super("Invalid PersistenceSettings:\n" + result);
            this.result = result;
        }

        public ValidationResult getResult() {
            return result;
        }
    }
}
