package pl.fejzu.persistence.storage;

import java.util.concurrent.CompletableFuture;

public interface StorageProvider {

    CompletableFuture<Void> connect();

    CompletableFuture<Void> disconnect();

    boolean isConnected();

    String getProviderName();
}
