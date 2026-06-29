package pl.fejzu.persistence.examples.common.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.sync.SyncScope;
import pl.fejzu.persistence.sync.annotation.SynchronizedEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity("example_guilds")
@SynchronizedEntity(scope = SyncScope.SERVER)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ExampleGuild implements PersistenceEntity<String> {

    @Id
    private String id;
    private String name;
    private UUID owner;

    @Builder.Default
    private List<UUID> members = new ArrayList<>();

    private long coins;
}
