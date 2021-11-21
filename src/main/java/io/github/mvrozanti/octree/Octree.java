package io.github.mvrozanti.octree;

import io.github.mvrozanti.octree.distance.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public class Octree {

    public static void main(String[] args) {
        Octree octree = new Octree();
    }

    private OctreeParams params;
    protected Octant root;
    private List<PointT> data;
    protected List<Integer> successors;

    private void radiusNeighbors(Octant octant, PointT query, double radius, double sqrRadius, List<Integer> resultIndices, DistanceType distanceType) {
        List<PointT> points = data;

        // if search ball S(q,r) contains octant, simply add point indexes.
        if (contains(query, sqrRadius, octant, distanceType)) {
            int idx = octant.getStart();
            for (int i = 0; i < octant.getSize(); ++i) {
                resultIndices.add(idx);
                idx = successors.get(idx);
            }

            return;  // early pruning.
        }

        if (octant.isLeaf()) {
            int idx = octant.getStart();
            for (int i = 0; i < octant.getSize(); ++i) {
                PointT p = points.get(idx);
                double dist = distanceType.compute(query, p);
                if (dist < sqrRadius) resultIndices.add(idx);
                idx = successors.get(idx);
            }

            return;
        }

        // check whether child nodes are in range.
        for (int c = 0; c < 8; ++c) {
            if (octant.getChild(c).getSize() == 0) continue; // what means child == 0?
            if (!overlaps(query, radius, sqrRadius, octant.getChild(c), distanceType)) continue;
            radiusNeighbors(octant.getChild(c), query, radius, sqrRadius, resultIndices, distanceType);
        }
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
        min.set(1, points.get(0).y());
        min.set(2, points.get(0).z());
        max.set(0, min.get(0));
        max.set(1, min.get(1));
        max.set(2, min.get(2));

        for (int i = 0; i < N; i++) {
            successors.set(i, i + 1);
//            System.out.println("successors[i] = " + i + " + 1 = " + (i + 1));
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
        for (int i = 1; i < 3; i++) {
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
        List<Double> min = Arrays.asList(new Double[3]);
        List<Double> max = Arrays.asList(new Double[3]);
        min.set(0, points.get(lastIdx).x());
        min.set(1, points.get(lastIdx).y());
        min.set(2, points.get(lastIdx).z());
        max.set(0, min.get(0));
        max.set(1, min.get(1));
        max.set(2, min.get(2));

        for (int i = 1; i < indexes.size(); i++) {
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
        for (int i = 1; i < 3; i++) {
            Double extent = 0.5f * (max.get(i) - min.get(i));
            ctr.set(i, ctr.get(i) + extent);
            if (extent > maxextent)
                maxextent = extent;
        }

        root = createOctant(ctr.get(0), ctr.get(1), ctr.get(2), maxextent, indexes.get(0), lastIdx, indexes.size());
    }

    public void clear() {
        root = null;
        data = null;
        if (successors != null)
            successors.clear();
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

        double[] factor = {-0.5f, 0.5f};

        if (size > params.getBucketSize() && extent > 2 * params.getMinExtent()) {
            octant.setLeaf(false);
            List<PointT> points = new ArrayList<>(data);

            List<Integer> childStarts = new ArrayList<>(Collections.nCopies(8, 0));
            List<Integer> childEnds = new ArrayList<>(Collections.nCopies(8, 0));
            List<Integer> childSizes = new ArrayList<>(Collections.nCopies(8, 0));

            int idx = startIdx;

            for (int i = 0; i < size; i++) {
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
            for (int i = 0; i < 8; i++) {
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
                    successors.set(octant.getChild(lastChildIdx).getEnd(), octant.getChild(i).getStart());
                lastChildIdx = i;
                octant.setEnd(octant.getChild(i).getEnd());
                firstTime = false;
            }
        }

        return octant;
    }

    public int findNeighbor(PointT query, double minDistance, DistanceType distanceType) {
        double maxDistance = Double.POSITIVE_INFINITY;
        AtomicInteger resultIndex = new AtomicInteger(-1);
        if (root == null)
            return resultIndex.get();
        findNeighbor(root, query, minDistance, maxDistance, resultIndex, distanceType);
        return resultIndex.get();
    }

    public boolean findNeighbor(Octant octant, PointT query, double minDistance, double maxDistance, AtomicInteger resultIndex, DistanceType distanceType) {
        List<PointT> points = data;
        // 1. first descend to leaf and check in leafs points.
        if (octant.isLeaf()) {
            int idx = octant.getStart();
            double sqrMaxDistance = distanceType.sqr(maxDistance);
            double sqrMinDistance = minDistance < 0 ? minDistance : distanceType.sqr(minDistance);

            for (int i = 0; i < octant.getSize(); ++i) {
                PointT p = points.get(idx);
                double dist = distanceType.compute(query, p);
                if (dist > sqrMinDistance && dist < sqrMaxDistance) {
                    resultIndex.set(idx);
                    sqrMaxDistance = dist;
                }
                idx = successors.get(idx);
            }

            maxDistance = distanceType.sqrt(sqrMaxDistance);
            return inside(query, maxDistance, octant);
        }

        // determine Morton code for each point...
        int mortonCode = 0;
        if (query.x() > octant.getX()) mortonCode |= 1;
        if (query.y() > octant.getY()) mortonCode |= 2;
        if (query.z() > octant.getZ()) mortonCode |= 4;

        if (octant.getChild(mortonCode) != null) {
            if (findNeighbor(octant.getChild(mortonCode), query, minDistance, maxDistance, resultIndex, distanceType))
                return true;
        }

        // 2. if current best point completely inside, just return.
        double sqrMaxDistance = distanceType.sqr(maxDistance);

        // 3. check adjacent octants for overlap and check these if necessary.
        for (int c = 0; c < 8; ++c) {
            if (c == mortonCode) continue;
            if (octant.getChild(c) == null) continue;
            if (!overlaps(query, maxDistance, sqrMaxDistance, octant.getChild(c), distanceType)) continue;
            if (findNeighbor(octant.getChild(c), query, minDistance, maxDistance, resultIndex, distanceType))
                return true;  // early pruning
        }

        // all children have been checked...check if point is inside the current octant...
        return inside(query, maxDistance, octant);
    }

    public int findNeighbor(PointT query, DistanceType distanceType) {
        return findNeighbor(query, -1, distanceType);
    }

    private static boolean overlaps(PointT query, double radius, double sqRadius, Octant octant, DistanceType distanceType) {
        // we exploit the symmetry to reduce the test to testing if its inside the Minkowski sum around the positive quadrant.
        double x = query.x() - octant.getX();
        double y = query.y() - octant.getY();
        double z = query.z() - octant.getZ();

        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);

        double maxdist = radius + octant.getExtent();

        // Completely outside, since q' is outside the relevant area.
        if (x > maxdist || y > maxdist || z > maxdist) return false;

        int num_less_extent = (x < octant.getExtent() ? 1 : 0) + (y < octant.getExtent() ? 1 : 0) + ((z < octant.getExtent()) ? 1 : 0);

        // Checking different cases:

        // a. inside the surface region of the octant.
        if (num_less_extent > 1) return true;

        // b. checking the corner region && edge region.
        x = Math.max(x - octant.getExtent(), 0.0f);
        y = Math.max(y - octant.getExtent(), 0.0f);
        z = Math.max(z - octant.getExtent(), 0.0f);

        return (distanceType.norm(x, y, z) < sqRadius);
    }

    private static boolean contains(PointT query, double sqrRadius, Octant octant, DistanceType distanceType) {
        // we exploit the symmetry to reduce the test to test
        // whether the farthest corner is inside the search ball.
        double x = query.x() - octant.getX();
        double y = query.y() - octant.getY();
        double z = query.z() - octant.getZ();

        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        // reminder: (x, y, z) - (-e, -e, -e) = (x, y, z) + (e, e, e)
        x += octant.getExtent();
        y += octant.getExtent();
        z += octant.getExtent();

        return (distanceType.norm(x, y, z) < sqrRadius);
    }

    private static boolean inside(PointT query, double radius, Octant octant) {
        // we exploit the symmetry to reduce the test to test
        // whether the farthest corner is inside the search ball.
        double x = query.x() - octant.getX();
        double y = query.y() - octant.getY();
        double z = query.z() - octant.getZ();

        x = Math.abs(x) + radius;
        y = Math.abs(y) + radius;
        z = Math.abs(z) + radius;

        if (x > octant.getExtent()) return false;
        if (y > octant.getExtent()) return false;
        if (z > octant.getExtent()) return false;

        return true;
    }
}
