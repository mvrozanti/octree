package org.octree;

import java.util.*;

public class Octree {

    public static void main(String[] args) {
        Octree octree = new Octree();
    }

    private OctreeParams params;
    protected Octant root;
    private List<PointT> data;
    protected List<Integer> successors;

    public void radiusNeighbors(Octant octant, PointT query, float radius, float sqrRadius, List<Integer> resultIndices) {

    }

    public void initialize(List<PointT> points) {
        initialize(points, new OctreeParams());
    }

    public void initialize(List<PointT> points, List<Integer> indexes) {
        initialize(points, indexes, new OctreeParams());
    }

    public void initialize(List<PointT> points, OctreeParams octreeParams) {
        clear();
        params = octreeParams;

        if (params.isCopyPoints())         /** is this an accurate translation for that condition? **/
            data = new ArrayList<>(points); /*                                                      **/
        else                                /*                                                      **/
            data = points;                  /*                                                      **/

        final int N = points.size();
        successors = Arrays.asList(new Integer[N]);

        List<Double> min = Arrays.asList(new Double[3]);
        List<Double> max = Arrays.asList(new Double[3]);
        min.set(0, points.get(0).x());
        min.set(1, points.get(1).y());
        min.set(2, points.get(2).z());
        max.set(0, min.get(0));
        max.set(1, min.get(1));
        max.set(2, min.get(2));

        for (int i = 0; i < N; ++i) {
            successors.set(i, i + 1);
            PointT p = points.get(i);
            if (p.x() < min.get(0))
                min.set(0, p.x());
            if (p.y() < min.get(1))
                min.set(1, p.y());
            if (p.z() < min.get(2))
                min.set(2, p.z());
            if (p.x() > max.get(0))
                max.set(0, p.x());
            if (p.y() > max.get(1))
                max.set(1, p.y());
            if (p.z() > max.get(2))
                max.set(2, p.z());
        }

        List<Double> ctr = new ArrayList<>();
        ctr.add(min.get(0));
        ctr.add(min.get(1));
        ctr.add(min.get(2));

        double maxextent = 0.5 * (max.get(0) - min.get(0));
        ctr.set(0, ctr.get(0) + maxextent);
        for (int i = 1; i < 3; ++i) {
            double extent = 0.5 * (max.get(i) - min.get(i));
            ctr.set(i, ctr.get(i) + extent);
            if (extent > maxextent)
                maxextent = extent;
        }

        root = createOctant(ctr.get(0), ctr.get(1), ctr.get(2), maxextent, 0, N - 1, N);
    }

    public void initialize(List<PointT> points, List<Integer> indexes, OctreeParams octreeParams) {
        clear();
        params = octreeParams;

        if (params.isCopyPoints())         /** is this an accurate translation for that condition? **/
            data = new ArrayList<>(points); /*                                                      **/
        else                                /*                                                      **/
            data = points;                  /*                                                      **/

        final int N = points.size();
        successors = new ArrayList<>(N);

        if (indexes.isEmpty())
            return;

        int lastIdx = indexes.get(0);
        List<Double> min = new ArrayList<>(3);
        List<Double> max = new ArrayList<>(3);
        min.set(0, points.get(lastIdx).x());
        min.set(1, points.get(lastIdx).y());
        min.set(2, points.get(lastIdx).z());
        max.set(0, min.get(0));
        max.set(1, min.get(1));
        max.set(2, min.get(2));

        for (int i = 1; i < indexes.size(); ++i) {
            int idx = indexes.get(i);
            successors.set(lastIdx, idx);
            PointT p = points.get(idx);
            if (p.x() < min.get(0))
                min.set(0, p.x());
            if (p.y() < min.get(1))
                min.set(1, p.y());
            if (p.z() < min.get(2))
                min.set(2, p.z());
            if (p.x() > max.get(0))
                max.set(0, p.x());
            if (p.y() > max.get(1))
                max.set(1, p.y());
            if (p.z() > max.get(2))
                max.set(2, p.z());

            lastIdx = idx;
        }

        List<Double> ctr = new ArrayList<>(3);
        ctr.add(min.get(0));
        ctr.add(min.get(1));
        ctr.add(min.get(2));

        Double maxextent = 0.5f * (max.get(0) - min.get(0));
        ctr.set(0, ctr.get(0) + maxextent);
        for (int i = 1; i < 3; ++i) {
            Double extent = 0.5f * (max.get(i) - min.get(i));
            ctr.set(i, ctr.get(i) + extent);
            if (extent > maxextent)
                maxextent = extent;
        }

        root = createOctant(ctr.get(0), ctr.get(1), ctr.get(2), maxextent, indexes.get(0), lastIdx, indexes.size());
    }

    public void clear() {

    }

    protected Octant createOctant(Double x, Double y, Double z, Double extent, int startIdx, int endIdx, int size) {
        Octant octant = new Octant();

        octant.setLeaf(true);

        octant.setX(x);
        octant.setY(y);
        octant.setZ(z);

        octant.setExtent(extent);
        octant.setStart(startIdx);
        octant.setEnd(endIdx);
        octant.setSize(size);

        double factor[] = {-0.5, 0.5};

        if (size > params.getBucketSize() && extent > 2 * params.getMinExtent()) {
            octant.setLeaf(false);
            List<PointT> points = new ArrayList(data);

            List<Integer> childStarts = new ArrayList<>(Collections.nCopies(8, 0));
            List<Integer> childEnds = new ArrayList<>(Collections.nCopies(8, 0));
            List<Integer> childSizes = new ArrayList<>(Collections.nCopies(8, 0));

            int idx = startIdx;

            for (int i = 0; i < size; ++i) {
                PointT p = points.get(idx);
                int mortonCode = 0;
                if (p.x() > x)
                    mortonCode |= 1;
                if (p.y() > y)
                    mortonCode |= 2;
                if (p.z() > z)
                    mortonCode |= 4;

                if (childSizes.get(mortonCode) == 0)
                    childStarts.set(mortonCode, idx);
                else
                    successors.set(childEnds.get(mortonCode), idx);
                childSizes.set(mortonCode, childSizes.get(mortonCode) + 1);

                childEnds.set(mortonCode, idx);
                idx = successors.get(idx);
            }

            double childExtent = 0.5 * extent;
            boolean firstTime = true;
            int lastChildIdx = 0;
            for (int i = 0; i < 8; ++i) {
                if (childSizes.get(i) == 0)
                    continue;
                double childX = x + factor[(i & 1) > 0 ? 1 : 0] * extent;
                double childY = y + factor[(i & 2) > 0 ? 1 : 0] * extent;
                double childZ = z + factor[(i & 4) > 0 ? 1 : 0] * extent;

                Octant newOctant = createOctant(
                        childX,
                        childY,
                        childZ,
                        childExtent,
                        childStarts.get(i),
                        childEnds.get(i),
                        childSizes.get(i)
                );
                octant.setChild(i, newOctant);

                if (firstTime)
                    octant.setStart(octant.getChild(i).getStart());
                else
                    successors.set(octant.getChild(lastChildIdx).getEnd(), octant.getChild(i).getEnd());
                lastChildIdx = i;
                octant.setEnd(octant.getChild(i).getEnd());
                firstTime = false;
            }
        }

        return octant;
    }

    protected final boolean findNeighbor(Octant octant, PointT query, float minDistance, float maxDistance, int resultIndex) {
        return false;
    }

    private static boolean overlaps(PointT query, float radius, float sqrRadius, Octant o) {
        return false;
    }

    private static boolean contains(PointT query, float radius, float sqrRadius, Octant o) {
        return false;
    }

    private static boolean inside(PointT query, float radius, float sqrRadius, Octant o) {
        return false;
    }
}
