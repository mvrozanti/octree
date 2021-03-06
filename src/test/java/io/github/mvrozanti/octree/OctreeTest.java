package io.github.mvrozanti.octree;

import io.github.mvrozanti.octree.distance.*;
import java.util.*;
import lombok.*;
import org.apache.commons.math3.random.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.*;
import static io.github.mvrozanti.octree.Octree.*;
import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.*;

public class OctreeTest {

    @AllArgsConstructor
    private static class Point implements PointT {
        private double x;
        private double y;
        private double z;

        @Override
        public Double x() {
            return x;
        }

        @Override
        public Double y() {
            return y;
        }

        @Override
        public Double z() {
            return z;
        }
    }

    @Test
    void testInitializationWithMinExtent() {
        int N = 1000;
        OctreeParams params = new OctreeParams();
        params.setBucketSize(16);

        Octree oct = new Octree(null);

        Octant root = oct.root;

        assertNull(root);

        List<PointT> points = new ArrayList<>(N);
        randomPoints(points, N, 1337);

        oct.initialize(points, params);

        root = oct.root;
        assertNotNull(root);
        assertEquals(N, oct.successors.size());

        List<Integer> elementCount = new ArrayList<>(Collections.nCopies(N, 0));
        int idx = root.getStart();
        for (int i = 0; i < N; i++) {
            assertTrue(idx < N);
            assertTrue(oct.successors.get(idx) <= N);
            int count = elementCount.get(idx);
            elementCount.set(idx, count + 1);
            assertEquals(1, elementCount.get(idx));
            idx = oct.successors.get(idx);
        }

        for (int i = 0; i < N; i++)
            assertEquals(1, elementCount.get(i));

        ArrayDeque<Octant> queue = new ArrayDeque<>();
        queue.push(root);
        List<Integer> assignment = new ArrayList<>(Collections.nCopies(N, -1));

        while (!queue.isEmpty()) {
            Octant octant = queue.pop();

            assertTrue(octant.getStart() < N);

            int octantIdx = octant.getStart();
            int lastIdx = octant.getStart();

            for (int i = 0; i < octant.getSize(); i++) {
                double x = points.get(octantIdx).x() - octant.getX();
                double y = points.get(octantIdx).y() - octant.getY();
                double z = points.get(octantIdx).z() - octant.getZ();

                assertTrue(abs(x) <= octant.getExtent());
                assertTrue(abs(y) <= octant.getExtent());
                assertTrue(abs(z) <= octant.getExtent());

                assignment.set(octantIdx, -1);
                lastIdx = octantIdx;
                octantIdx = oct.successors.get(octantIdx);
            }
            assertEquals(octant.getEnd(), lastIdx);

            boolean shouldBeLeaf = true;
            Octant firstchild = null;
            Octant lastchild = null;
            int pointSum = 0;

            for (int c = 0; c < 8; ++c) {
                Octant child = octant.getChild(c);
                if (child == null)
                    continue;
                shouldBeLeaf = false;
                if (firstchild == null)
                    firstchild = child;
                if (lastchild != null)
                    assertEquals(child.getStart(), oct.successors.get(lastchild.getEnd()));

                pointSum += child.getSize();
                lastchild = child;
                int childIdx = child.getStart();
                for (int i = 0; i < child.getSize(); i++) {
                    assertEquals(-1, assignment.get(childIdx));
                    assignment.set(childIdx, c);
                    childIdx = oct.successors.get(childIdx);
                }
                queue.push(child);
            }

            if (firstchild != null)
                assertEquals(octant.getStart(), firstchild.getStart());
            if (lastchild != null)
                assertEquals(octant.getEnd(), lastchild.getEnd());

            assertEquals(shouldBeLeaf, octant.isLeaf());
            assertEquals(octant.getSize() <= params.getBucketSize() || octant.getExtent() < 2 * params.getMinExtent(), octant.isLeaf());

            if (!octant.isLeaf()) {
                assertEquals(octant.getSize(), pointSum);
                int leafIdx = octant.getStart();
                for (int i = 0; i < octant.getSize(); i++) {
                    assertTrue(assignment.get(leafIdx) > -1);
                    leafIdx = oct.successors.get(leafIdx);
                }
            }
        }
    }

    @Test
    void testInsertion() {
        int N = 1000;
        Octree octByInitialization = new Octree(DistanceType.EUCLIDEAN);
        Octree octByInsertion = new Octree(DistanceType.EUCLIDEAN);

        List<PointT> points = new ArrayList<>(N);
        randomPoints(points, N, 2804);
        octByInitialization.initialize(points);

        List<PointT> points2 = new ArrayList<>(N);
        randomPoints(points2, N, 2804);
        for (PointT p : points2)
            octByInsertion.insert(p);
    }

    @Test
    void testInitialization() {
        int N = 1000;
        OctreeParams params = new OctreeParams();
        params.setBucketSize(16);
        params.setMinExtent(1);

        Octree oct = new Octree(null);

        Octant root = oct.root;

        assertNull(root);

        List<PointT> points = new ArrayList<>(N);
        randomPoints(points, N, 1337);

        oct.initialize(points, params);

        root = oct.root;
        assertNotNull(root);
        assertEquals(N, oct.successors.size());

        List<Integer> elementCount = new ArrayList<>(Collections.nCopies(N, 0));
        int idx = root.getStart();
        for (int i = 0; i < N; i++) {
            assertTrue(idx < N);
            assertTrue(oct.successors.get(idx) <= N);
            int count = elementCount.get(idx);
            elementCount.set(idx, count + 1);
            assertEquals(1, elementCount.get(idx));
            idx = oct.successors.get(idx);
        }

        for (int i = 0; i < N; i++)
            assertEquals(1, elementCount.get(i));

        ArrayDeque<Octant> queue = new ArrayDeque<>();
        queue.push(root);
        List<Integer> assignment = new ArrayList<>(Collections.nCopies(N, -1));

        while (!queue.isEmpty()) {
            Octant octant = queue.pop();

            assertTrue(octant.getStart() < N);

            int octantIdx = octant.getStart();
            int lastIdx = octant.getStart();

            for (int i = 0; i < octant.getSize(); i++) {
                double x = points.get(octantIdx).x() - octant.getX();
                double y = points.get(octantIdx).y() - octant.getY();
                double z = points.get(octantIdx).z() - octant.getZ();

                assertTrue(abs(x) <= octant.getExtent());
                assertTrue(abs(y) <= octant.getExtent());
                assertTrue(abs(z) <= octant.getExtent());

                assignment.set(octantIdx, -1);
                lastIdx = octantIdx;
                octantIdx = oct.successors.get(octantIdx);
            }
            assertEquals(octant.getEnd(), lastIdx);

            boolean shouldBeLeaf = true;
            Octant firstchild = null;
            Octant lastchild = null;
            int pointSum = 0;

            for (int c = 0; c < 8; ++c) {
                Octant child = octant.getChild(c);
                if (child == null)
                    continue;
                shouldBeLeaf = false;
                if (firstchild == null)
                    firstchild = child;
                if (lastchild != null)
                    assertEquals(child.getStart(), oct.successors.get(lastchild.getEnd()));

                pointSum += child.getSize();
                lastchild = child;
                int childIdx = child.getStart();
                for (int i = 0; i < child.getSize(); i++) {
                    assertEquals(-1, assignment.get(childIdx));
                    assignment.set(childIdx, c);
                    childIdx = oct.successors.get(childIdx);
                }
                queue.push(child);
            }

            if (firstchild != null)
                assertEquals(octant.getStart(), firstchild.getStart());
            if (lastchild != null)
                assertEquals(octant.getEnd(), lastchild.getEnd());

            assertEquals(shouldBeLeaf, octant.isLeaf());
            assertEquals(octant.getSize() <= params.getBucketSize(), octant.isLeaf());

            if (!octant.isLeaf()) {
                assertEquals(octant.getSize(), pointSum);
                int leafIdx = octant.getStart();
                for (int i = 0; i < octant.getSize(); i++) {
                    assertTrue(assignment.get(leafIdx) > -1);
                    leafIdx = oct.successors.get(leafIdx);
                }
            }
        }
    }

    @Test
    void testFindNeighbor() {
        int N = 1000;
        Random random = new Random(1234);
        val uniformDistribution = new UniformRandomGenerator(RandomGeneratorFactory.createRandomGenerator(random));

        List<PointT> points = new ArrayList<>();
        randomPoints(points, N, 1234);

        NaiveNeighborSearch bruteforce = new NaiveNeighborSearch(DistanceType.EUCLIDEAN);
        bruteforce.initialize(points);
        Octree octree = new Octree(DistanceType.EUCLIDEAN);
        octree.initialize(points);

        for (int i = 0; i < 10; i++) {
            double normalized_double = abs(uniformDistribution.nextNormalizedDouble()) * N / 2;
            int index = (int) normalized_double;
            PointT query = points.get(index);

            assertEquals(index, bruteforce.findNeighbor(query));
            assertEquals(index, octree.findNeighbor(query));

            int bfneighbor = bruteforce.findNeighbor(query, 0.3);
            int octneighbor = octree.findNeighbor(query, 0.3);

            assertEquals(bfneighbor, octneighbor);
        }
    }

    @Test
    void testRadiusNeighbors() {
        int N = 1000;
        Random random = new Random(1234);
        val uniformDistribution = new UniformRandomGenerator(RandomGeneratorFactory.createRandomGenerator(random));

        List<PointT> points = new ArrayList<>();
        randomPoints(points, N, 1234);

        NaiveNeighborSearch bruteforce = new NaiveNeighborSearch(DistanceType.EUCLIDEAN);
        bruteforce.initialize(points);
        Octree octree = new Octree(DistanceType.EUCLIDEAN);
        octree.initialize(points);

        double[] radii = new double[]{0.5, 1.0, 2.0, 5.0};
        for (int r = 0; r < 4; ++r) {
            for (int i = 0; i < 10; ++i) {
                List<Integer> neighborsBruteforce = new ArrayList<>();
                List<Integer> neighborsOctree = new ArrayList<>();

                int index = (int) abs(uniformDistribution.nextNormalizedDouble()) * N / 2;
                PointT query = points.get(index);

                bruteforce.radiusNeighbors(query, radii[r], neighborsBruteforce);
                octree.radiusNeighbors(query, radii[r], neighborsOctree);
                assertTrue(similarVectors(neighborsBruteforce, neighborsOctree));

                bruteforce.setDistanceType(DistanceType.MANHATTAN);
                octree.setDistanceType(DistanceType.MANHATTAN);
                bruteforce.radiusNeighbors(query, radii[r], neighborsBruteforce);
                octree.radiusNeighbors(query, radii[r], neighborsOctree);
                assertTrue(similarVectors(neighborsBruteforce, neighborsOctree));

                bruteforce.radiusNeighbors(query, radii[r], neighborsBruteforce);
                octree.radiusNeighbors(query, radii[r], neighborsOctree);
                bruteforce.setDistanceType(DistanceType.MAXIMUM);
                octree.setDistanceType(DistanceType.MAXIMUM);
                assertTrue(similarVectors(neighborsBruteforce, neighborsOctree));
            }
        }
    }

    @Test
    void testOverlap() {
        Octree dummy = new Octree(DistanceType.EUCLIDEAN);
        Octant octant = new Octant();
        octant.setX(1.);
        octant.setY(1.);
        octant.setZ(1.);
        octant.setExtent(.5);

        PointT query = new Point(1.25, 1.25, 0.5);
        double radius = 1;
        assertTrue(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.75, 1.0, 1.0);
        radius = .5;

        assertTrue(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.0, 1.75, 1.0);
        assertTrue(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.0, 1.0, 1.75);
        assertTrue(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.0, 1.0, 2.75);
        assertFalse(dummy.overlaps(query, radius, radius * radius, octant));

        // Edge cases:
        query = new Point(1.65, 1.65, 1.25);
        assertTrue(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.25, 1.65, 1.65);
        assertTrue(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.65, 1.25, 1.75);
        assertTrue(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.9, 1.25, 1.9);
        assertFalse(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.25, 1.9, 1.9);
        assertFalse(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.9, 1.9, 1.25);
        assertFalse(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.65, 1.65, 1.65);
        assertTrue(dummy.overlaps(query, radius, radius * radius, octant));

        query = new Point(1.95, 1.95, 1.95);
        assertFalse(dummy.overlaps(query, radius, radius * radius, octant));

        octant.setX(.025);
        octant.setY(-.025);
        octant.setZ(-.025);
        octant.setExtent(.025);

        query = new Point(.025, .025, .025);
        radius = .025;

        assertFalse(dummy.overlaps(query, radius, radius * radius, octant));
    }

    private static void randomPoints(List<PointT> points, int N, int seed) {
        Random random = new Random(seed);
        points.clear();
        for (int i = 0; i < N; i++) {
            double x = 10 * random.nextGaussian() - 5;
            double y = 10 * random.nextGaussian() - 5;
            double z = 10 * random.nextGaussian() - 5;

            points.add(new Point(x, y, z));
        }
    }

    private static <T> boolean similarVectors(List<T> list1, List<T> list2) {
        if (list1.size() != list2.size()) {
            System.out.println("expected size = " + list1.size() + ", but got size = " + list2.size());
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            boolean found = false;
            for (int j = 0; j < list2.size(); j++) {
                if (list1.get(i).equals(list2.get(j))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println(i + "-th element (" + list1.get(i) + ") not found.");
                return false;
            }
        }
        return true;
    }
}
