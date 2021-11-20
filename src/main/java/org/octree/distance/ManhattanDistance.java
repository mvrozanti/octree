package org.octree.distance;

import org.octree.*;
import static java.lang.Math.*;

public class ManhattanDistance extends Distance {

    private static ManhattanDistance INSTANCE;

    private ManhattanDistance() {
    }

    @Override
    public Double compute(PointT p, PointT q) {
        double diff1 = p.x() - q.x();
        double diff2 = p.y() - q.y();
        double diff3 = p.z() - q.z();
        return abs(diff1) + abs(diff2) + abs(diff3);
    }

    @Override
    public Double norm(double x, double y, double z) {
        return abs(x) + abs(y) + abs(z);
    }

    @Override
    public Double sqr(double r) {
        return r;
    }

    @Override
    public Double sqrt(double r) {
        return r;
    }

    protected static ManhattanDistance getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ManhattanDistance();
        return INSTANCE;
    }
}
