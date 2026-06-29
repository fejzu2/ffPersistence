package pl.fejzu.persistence.examples.common.sector;

import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.sector.SectorState;
import pl.fejzu.persistence.sector.core.DefaultSector;
import pl.fejzu.persistence.sector.core.DefaultSectorService;

import java.util.List;

public final class ExampleSectorFactory {

    private static final List<String> SECTOR_NAMES = List.of(
        "lobby", "spawn-1", "spawn-2", "survival-1", "survival-2"
    );

    private ExampleSectorFactory() {}

    public static void registerSectors(SectorService sectorService) {
        SECTOR_NAMES.forEach(name ->
            sectorService.registerSector(
                DefaultSector.builder()
                    .id(SectorId.of(name))
                    .displayName(toDisplayName(name))
                    .serverName(name)
                    .state(SectorState.ONLINE)
                    .priority(name.equals("lobby") ? 10 : 0)
                    .build()
            )
        );
    }

    public static DefaultSectorService buildSectorService(
        DefaultSectorService.Builder builder,
        ExampleLastSectorProvider lastSectorProvider
    ) {
        DefaultSectorService sectorService = builder.build();
        registerSectors(sectorService);
        sectorService.buildConnectService(lastSectorProvider);
        return sectorService;
    }

    private static String toDisplayName(String name) {
        String[] parts = name.split("-");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1));
        }
        return sb.toString();
    }
}
