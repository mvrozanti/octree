package org.octree;

import java.util.*;

public class Octree<T extends OctPoint> {

    public Octree() {
    }


    public Octant createOctant(float x, float y, float z, float extent, int startIdx, int endIdx, int size) {

    }

    public boolean findNeighbor(Octant octant, OctPoint query, float minDistance, float maxDistance, int resultIndex) {

    }

    public void radiusNeighbors(Octant octant, OctPoint query, float radius, float sqrRadius, List<Integer> resultIndices) {

    }

    public boolean overlaps(OctPoint query, float radius, float sqrRadius, Octant o) {

    }

    public boolean contains(OctPoint query, float radius, float sqrRadius, Octant o) {

    }

    public boolean inside(OctPoint query, float radius, float sqrRadius, Octant o) {

    }
}
