package org.octree;

import lombok.*;

@AllArgsConstructor
@Builder
@Data
public class OctreeParams {
    @Builder.Default
    private int bucketSize = 32;
    private boolean copyPoints;
    private float minExtent;

    public OctreeParams() {
        bucketSize = 32;
    }
}
