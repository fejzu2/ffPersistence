package pl.fejzu.persistence.examples.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ExampleLocationData {

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
}
