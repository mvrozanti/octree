package io.github.mvrozanti.octree;

import lombok.*;

@AllArgsConstructor
@Builder
@Data
public class OctreeParams {
    @Builder.Default
    private int bucketSize = 32;
    private boolean copyPoints;
    private double minExtent;

    public OctreeParams() {
        bucketSize = 32;
    }
}
