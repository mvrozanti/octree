package org.octree;

import lombok.*;
import org.junit.jupiter.api.*;

public class OctreeTest {

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

    void testOctreeConstruction() {
        Assertions.assertDoesNotThrow(() -> {
            Octree octree = new Octree();
        });
    }

}
