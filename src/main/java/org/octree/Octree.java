package org.octree;

import java.util.*;

public class Octree<T> {

    public static void main(String[] args) {
        Octree<Integer> octree = new Octree<>();
    }

    private OctreeParams params;
    private Octant<T> root;
    private final List<OctPoint<T>> data;
    private List<Integer> successors;

    public Octree() {
        root = new Octant<T>();
        data = new ArrayList<>();
    }

    public void initialize() {
        initialize(new OctreeParams());
    }

    public void initialize(OctreeParams octreeParams) {

    }

    public void initialize(List<OctPoint<T>> points) {
        initialize(points, new OctreeParams());
    }

    public void initialize(List<OctPoint<T>> points, List<Integer> indexes) {
        initialize(points, indexes, new OctreeParams());
    }

    public void initialize(List<OctPoint<T>> points, OctreeParams octreeParams) {
        clear();

    }

    public void initialize(List<OctPoint<T>> points, List<Integer> indexes, OctreeParams octreeParams) {

    }

    public void clear() {

    }

    protected Octant createOctant(float x, float y, float z, float extent, int startIdx, int endIdx, int size) {
        return null;
    }

    protected final boolean findNeighbor(Octant octant, OctPoint query, float minDistance, float maxDistance, int resultIndex) {
        return false;
    }

    protected void radiusNeighbors(Octant octant, OctPoint query, float radius, float sqrRadius, List<Integer> resultIndices) {

    }

    protected static boolean overlaps(OctPoint query, float radius, float sqrRadius, Octant o) {
        return false;
    }

    protected static boolean contains(OctPoint query, float radius, float sqrRadius, Octant o) {
        return false;
    }

    protected static boolean inside(OctPoint query, float radius, float sqrRadius, Octant o) {
        return false;
    }
}
