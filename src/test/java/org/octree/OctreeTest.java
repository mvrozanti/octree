package org.octree;

import java.util.*;
import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.*;
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
    void testOctreeConstruction() {
        Assertions.assertDoesNotThrow((Executable) Octree::new);
    }

    @Test
    void testInitialization() {
        int N = 1000;
        OctreeParams params = new OctreeParams();
        params.setBucketSize(16);

        Octree oct = new Octree();

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
        for (int i = 0; i < N; ++i) {
            assertTrue(idx < N);
            assertTrue(oct.successors.get(idx) <= N);
            elementCount.set(idx, elementCount.get(idx) + 1);
            assertEquals(1, elementCount.get(idx));
            idx = oct.successors.get(idx);
        }

        for (int i = 0; i < N; ++i)
            assertEquals(1, elementCount.get(i));

        ArrayDeque<Octant> queue = new ArrayDeque<>();
        queue.push(root);
        List<Integer> assignment = new ArrayList<>(Collections.singletonList(-1));

        while (!queue.isEmpty()) {
            Octant octant = queue.pop();

            assertTrue(octant.getStart() < N);

            int octantIdx = octant.getStart();
            int lastIdx = octant.getStart();

            for (int i = 0; i < octant.getSize(); ++i) {
                double x = points.get(octantIdx).x() - octant.getX();
                double y = points.get(octantIdx).y() - octant.getY();
                double z = points.get(octantIdx).z() - octant.getZ();

                assertTrue(abs(x) < octant.getExtent());
                assertTrue(abs(y) < octant.getExtent());
                assertTrue(abs(z) < octant.getExtent());

                assignment.set(octantIdx, -1);
                lastIdx = octantIdx;
                idx = oct.successors.get(idx);
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
                if (lastchild != null)
                    assertEquals(child.getStart(), oct.successors.get(lastchild.getEnd()));

                pointSum += child.getSize();
                lastchild = child;
                int childIdx = child.getStart();
                for (int i = 0; i < child.getSize(); ++i) {
                    assertEquals(-1, assignment.get(childIdx));
                    assignment.set(childIdx, c);
                    idx = oct.successors.get(idx);
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
                for (int i = 0; i < octant.getSize(); ++i) {
                    assertTrue(assignment.get(leafIdx) > -1);
                    idx = oct.successors.get(leafIdx);
                }
            }
        }
    }

    private static void randomPoints(List<PointT> points, int N, int seed) {
        Random random = new Random(seed);
        points.clear();
        for (int i = 0; i < N; ++i) {
            double x = 10 * random.nextDouble() - 5;
            double y = 10 * random.nextDouble() - 5;
            double z = 10 * random.nextDouble() - 5;

            points.add(new Point(x, y, z));
        }
    }
}
