package org.octree;

import java.util.*;

public class Octree<T extends Number> {

    public static void main(String[] args) {
        Octree<Integer> octree = new Octree<>();
    }

    private OctreeParams params;
    private Octant<T> root;
    private List<OctPoint<T>> data;
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
        params = octreeParams;

        if (params.getCopyPoints())         /** is this an accurate translation for that condition? **/
            data = new ArrayList<>(points); /*                                                      **/
        else                                /*                                                      **/
            data = points;                  /*                                                      **/


        final int N = points.size();
        successors = new ArrayList<>(N);

        List<T> min = new ArrayList<>(3);
        List<T> max = new ArrayList<>(3);
        min.set(0, points.get(0).x());
        min.set(1, points.get(1).y());
        min.set(2, points.get(2).z());
        max.set(0, min.get(0));
        max.set(1, min.get(1));
        max.set(2, min.get(2));

        for (int i = 0; i < N; ++i) {
            successors.set(i, i + 1);
            OctPoint<T> p = points.get(i);
            if (p.x().floatValue() < min.get(0).floatValue())
                min.set(0, p.x());
            if (p.y().floatValue() < min.get(1).floatValue())
                min.set(1, p.y());
            if (p.z().floatValue() < min.get(2).floatValue())
                min.set(2, p.z());
            if (p.x().floatValue() > max.get(0).floatValue())
                max.set(0, p.x());
            if (p.y().floatValue() > max.get(1).floatValue())
                max.set(1, p.y());
            if (p.z().floatValue() > max.get(2).floatValue())
                max.set(2, p.z());
        }

        List<Float> ctr = new ArrayList<>(3);
        ctr.add(min.get(0).floatValue());
        ctr.add(min.get(1).floatValue());
        ctr.add(min.get(2).floatValue());

        float maxextent = 0.5f * (max.get(0).floatValue() - min.get(0).floatValue());
        ctr.set(0, ctr.get(0) + maxextent);
        for (int i = 1; i < 3; ++i) {
            float extent = 0.5f * (max.get(i).floatValue() - min.get(i).floatValue());
            ctr.set(i, ctr.get(i) + extent);
            if (extent > maxextent)
                maxextent = extent;
        }

        root = createOctant(ctr.get(0), ctr.get(1), ctr.get(2), maxextent, 0, N - 1, N);
    }

    public void initialize(List<OctPoint<T>> points, List<Integer> indexes, OctreeParams octreeParams) {

    }

    public void clear() {

    }

    protected Octant<T> createOctant(float x, float y, float z, float extent, int startIdx, int endIdx, int size) {
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
